package cn.fudoc.trade.view;

import cn.fudoc.trade.api.TencentApiService;
import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.api.data.StockInfo;
import cn.fudoc.trade.core.common.FuNotification;
import cn.fudoc.trade.core.action.PinToolBarAction;
import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.common.enumtype.StockTabEnum;
import cn.fudoc.trade.core.state.HoldingsStockState;
import cn.fudoc.trade.core.state.MarketAllStockPersistentState;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.util.ProjectUtils;
import cn.fudoc.trade.util.ToolBarUtils;
import cn.fudoc.trade.view.dialog.GroupAddDialog;
import cn.fudoc.trade.view.dialog.HoldingsStockDialog;
import cn.fudoc.trade.view.render.FuStockSearchListCellRenderer;
import cn.fudoc.trade.view.table.StockTableView;
import com.google.common.collect.Sets;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import icons.FuIcons;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.im.InputContext;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class FuStockSearchPopupView {


    private final JPanel rootPanel;
    // 基础搜索框（稳定 API）
    private final SearchTextField searchField;
    // 结果列表相关
    private final DefaultListModel<StockInfo> resultModel;
    private final JBList<StockInfo> jbList;
    private Timer debounceTimer;
    private final MarketAllStockPersistentState dataSource;

    private final StockTableView stockTableView;

    private final AtomicBoolean pinStatus = new AtomicBoolean(false);

    private final TencentApiService tencentApiService = ApplicationManager.getApplication().getService(TencentApiService.class);

    public FuStockSearchPopupView(StockTableView stockTableView) {
        this.stockTableView = stockTableView;
        this.searchField = createSearchField();
        this.dataSource = MarketAllStockPersistentState.getInstance();
        //初始化结果列表
        this.resultModel = new DefaultListModel<>();
        this.jbList = new JBList<>(this.resultModel);
        this.jbList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        FuStockSearchListCellRenderer fuStockSearchListCellRenderer = new FuStockSearchListCellRenderer();
        this.jbList.setCellRenderer(fuStockSearchListCellRenderer);

        //给搜索框和搜索结果添加监听器
        addListener(fuStockSearchListCellRenderer);
        //防抖定时器（避免频繁搜索）
        this.debounceTimer = new Timer();
        this.rootPanel = new JPanel(new BorderLayout());
        JPanel contentPanel = new JPanel(new BorderLayout());
        JBLabel title = new JBLabel(FuTradeConstants.ADD_STOCK);
        title.setIcon(FuIcons.FU_STOCK);
        title.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(title, BorderLayout.WEST);
        ToolBarUtils.addActionToToolBar(titlePanel, "fu.stock.search.title", createSearchActionGroup(), BorderLayout.EAST);
        this.rootPanel.add(titlePanel, BorderLayout.NORTH);
        this.rootPanel.add(contentPanel, BorderLayout.CENTER);
        JLabel bottom = new JLabel();
        bottom.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        bottom.setText("可输入股票代码/首字母/名称 搜索股票");
        this.rootPanel.add(bottom, BorderLayout.SOUTH);
        contentPanel.add(this.searchField, BorderLayout.NORTH);
        // 中间：结果列表（带滚动条）
        JBScrollPane scrollPane = new JBScrollPane(this.jbList);
        scrollPane.setPreferredSize(new Dimension(450, 350));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
    }


    private DefaultActionGroup createSearchActionGroup() {
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
        defaultActionGroup.add(new PinToolBarAction(pinStatus));
        return defaultActionGroup;
    }

    public void showPopup(Project project) {
        JBPopup popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(this.rootPanel, this.searchField.getTextEditor()) // 关键：传递文本编辑器
                .setResizable(true)
                .setMovable(true)
                .setModalContext(false)
                .setRequestFocus(true)
                .setFocusable(true)
                .setBelongsToGlobalPopupStack(true)
                .setLocateWithinScreenBounds(false)
                // 单击外部时取消弹窗
                .setCancelOnClickOutside(false)
                // 在其他窗口打开时取消
                .setCancelOnOtherWindowOpen(false)
                .setCancelOnWindowDeactivation(false)
                .setCancelOnMouseOutCallback(event -> event.getID() == MouseEvent.MOUSE_PRESSED && !pinStatus.get())
                .createPopup();
        popup.showCenteredInCurrentWindow(project);

    }

    /**
     * 创建支持中文输入的 SearchTextField
     */
    private SearchTextField createSearchField() {
        SearchTextField searchField = new SearchTextField();
        // 关键：直接配置文本编辑器
        JTextComponent textEditor = searchField.getTextEditor();
        textEditor.enableInputMethods(true);
        // 设置字体，确保支持中文显示
        textEditor.setFont(UIManager.getFont("TextField.font"));
        // 添加焦点监听器，确保输入法正确激活
        textEditor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    // 尝试激活中文输入法
                    InputContext inputContext = textEditor.getInputContext();
                    if (inputContext != null) {
                        inputContext.selectInputMethod(Locale.CHINESE);
                    }
                });
            }
        });
        // 同时给外层 SearchTextField也添加焦点监听
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // 确保焦点真正传递给文本编辑器
                SwingUtilities.invokeLater(textEditor::requestFocusInWindow);
            }
        });
        return searchField;
    }


    private void addListener(FuStockSearchListCellRenderer renderer) {
        //输入框监听事件
        this.searchField.addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                triggerSearch();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                triggerSearch();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                triggerSearch();
            }
        });
        // 给 JList 绑定鼠标点击事件
        jbList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 1. 获取点击的单元格索引
                int clickIndex = jbList.locationToIndex(e.getPoint());
                if (clickIndex == -1) {
                    return; // 点击了列表空白区域，忽略
                }
                // 2. 获取该单元格的矩形区域（相对于 JList）
                Rectangle cellRect = jbList.getCellBounds(clickIndex, clickIndex);
                if (cellRect == null) {
                    return;
                }

                // 3. 判断是否点击了按钮区域
                boolean isButtonClick = renderer.isButtonClicked(
                        jbList,
                        cellRect,
                        e.getX(),
                        e.getY()
                );

                // 4. 若是按钮点击，执行逻辑
                if (isButtonClick) {
                    StockInfo clickedStock = jbList.getModel().getElementAt(clickIndex);
                    // 执行按钮点击逻辑（例如切换 isAdd 状态）
                    toggleStockAddStatus(clickedStock);
                    // 刷新列表，让渲染器重新绘制图标
                    jbList.repaint(cellRect); // 只刷新当前单元格，性能更好
                }
            }
        });
    }


    /**
     * 带防抖的搜索触发（200ms 延迟）
     */
    private void triggerSearch() {
        if (this.debounceTimer != null) {
            debounceTimer.cancel();
        }
        this.debounceTimer = new Timer();
        this.debounceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 确保 UI 操作在 EDT 线程执行
                SwingUtilities.invokeLater(() -> {
                    String keyword = searchField.getText().trim();
                    doSearch(keyword);
                });
            }
        }, 200);
    }

    /**
     * 核心搜索逻辑（结合搜索选项过滤结果）
     */
    private void doSearch(String keyword) {
        this.resultModel.clear();
        List<StockInfo> stockInfoList = this.dataSource.match(keyword);
        if (CollectionUtils.isNotEmpty(stockInfoList)) {
            for (StockInfo stockInfo : stockInfoList) {
                stockInfo.setAdd(this.stockTableView.isContainsStock(stockInfo.getStockCode()));
                this.resultModel.addElement(stockInfo);
            }
        }

    }

    private void toggleStockAddStatus(StockInfo stock) {
        stock.setAdd(!stock.isAdd());
        if (stock.isAdd()) {
            //添加股票
            List<RealStockInfo> realStockInfos = tencentApiService.stockList(Sets.newHashSet(stock.getStockCode()));
            if (CollectionUtils.isEmpty(realStockInfos)) {
                FuNotification.notifyWarning(stock.getStockCode() + "股票不存在");
                return;
            }
            //issue #11 MAC弹框问题修复
            if (StockTabEnum.STOCK_HOLD.equals(this.stockTableView.getTabEnum())) {
                if (SystemInfo.isWindows) {
                    //如果是加入持仓 则需要输入成本价和持仓数量
                    HoldingsStockDialog holdingsStockDialog = new HoldingsStockDialog(ProjectUtils.getCurrProject(), this.stockTableView.getTabName(), stock.getStockCode(), stock.getName());
                    if (holdingsStockDialog.showAndGet()) {
                        HoldingsInfo holdingsInfo = holdingsStockDialog.getHoldingsInfo();
                        HoldingsStockState.getInstance().add(this.stockTableView.getTabName(), stock.getStockCode(), holdingsInfo.getCost(), holdingsInfo.getCount());
                    }
                } else {
                    HoldingsStockState.getInstance().add(this.stockTableView.getTabName(), stock.getStockCode(), "0", 0);
                }
            }
            this.stockTableView.addStock(realStockInfos.getFirst());
        } else {
            // 移除股票（确保在 EDT 线程）
            this.stockTableView.removeStock(stock.getStockCode());
        }
    }
}

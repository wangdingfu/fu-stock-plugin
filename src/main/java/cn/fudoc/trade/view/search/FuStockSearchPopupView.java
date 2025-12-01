package cn.fudoc.trade.view.search;

import cn.fudoc.trade.api.data.StockInfo;
import cn.fudoc.trade.state.MarketAllStockPersistentState;
import cn.fudoc.trade.view.StockView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.UIUtil;
import icons.FuIcons;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

public class FuStockSearchPopupView {


    private final JPanel rootPanel;
    // 基础搜索框（稳定 API）
    private final SearchTextField searchField;
    // 结果列表相关
    private final DefaultListModel<StockInfo> resultModel;
    private final JBList<StockInfo> jbList;
    private Timer debounceTimer;
    private final MarketAllStockPersistentState dataSource;

    private final StockView stockView;

    public FuStockSearchPopupView(StockView stockView) {
        this.stockView = stockView;
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
        JLabel title = new JLabel("添加股票");
        title.setIcon(FuIcons.FU_STOCK);
        title.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        this.rootPanel.add(title, BorderLayout.NORTH);
        this.rootPanel.add(contentPanel, BorderLayout.CENTER);

        contentPanel.add(this.searchField, BorderLayout.NORTH);
        // 中间：结果列表（带滚动条）
        JBScrollPane scrollPane = new JBScrollPane(this.jbList);
        scrollPane.setPreferredSize(new Dimension(450, 350));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
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
                .setCancelOnClickOutside(true)
                .setCancelOnOtherWindowOpen(true)
                .setCancelOnWindowDeactivation(false)
                .createPopup();
        popup.showCenteredInCurrentWindow(project);

    }

    /**
     * 创建支持中文输入的SearchTextField
     */
    private SearchTextField createSearchField() {
        SearchTextField searchField = createSearchFieldWithPaintHint();

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
        // 同时给外层SearchTextField也添加焦点监听
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // 确保焦点真正传递给文本编辑器
                SwingUtilities.invokeLater(textEditor::requestFocusInWindow);
            }
        });
        return searchField;
    }
    private SearchTextField createSearchFieldWithPaintHint() {
        SearchTextField searchField = new SearchTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                JTextComponent editor = getTextEditor();
                if (editor.getText().isEmpty() && !editor.hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    try {
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(JBColor.gray);
                        g2.setFont(UIUtil.getLabelFont().deriveFont(Font.ITALIC, 11f));

                        FontMetrics fm = g2.getFontMetrics();
                        String hintText = "中文/拼音/代码";
                        int x = getWidth() - fm.stringWidth(hintText) - 8; // 右侧留出8像素边距
                        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

                        g2.drawString(hintText, x, y);
                    } finally {
                        g2.dispose();
                    }
                }
            }
        };

        // 添加重绘触发
        searchField.getTextEditor().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { searchField.repaint(); }
            @Override public void removeUpdate(DocumentEvent e) { searchField.repaint(); }
            @Override public void changedUpdate(DocumentEvent e) { searchField.repaint(); }
        });

        searchField.getTextEditor().addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { searchField.repaint(); }
            @Override public void focusLost(FocusEvent e) { searchField.repaint(); }
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
                stockInfo.setAdd(this.stockView.isConstants(stockInfo.getStockCode()));
                this.resultModel.addElement(stockInfo);
            }
        }

    }


    private void toggleStockAddStatus(StockInfo stock) {
        stock.setAdd(!stock.isAdd());
        if (stock.isAdd()) {
            //添加股票
            stockView.addStock(stock.getStockCode());
        } else {
            //移除股票
            stockView.removeStock(stock.getStockCode());
        }
    }
}

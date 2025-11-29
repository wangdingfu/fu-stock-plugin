package cn.fudoc.trade.view.search;

import cn.fudoc.trade.api.data.StockInfo;
import cn.fudoc.trade.state.MarketAllStockPersistentState;
import cn.fudoc.trade.view.StockView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FuStockSearchPopupView {


    private final JPanel rootPanel;
    private final JPanel searchPanel = new JPanel();
    // 基础搜索框（稳定 API）
    private final SearchTextField searchField = new SearchTextField();
    // 结果列表相关
    private final DefaultListModel<StockInfo> resultModel;
    private final JBList<StockInfo> jbList;
    private Timer debounceTimer;
    private final MarketAllStockPersistentState dataSource;

    private final StockView stockView;

    public FuStockSearchPopupView(StockView stockView) {
        this.stockView = stockView;
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
        this.rootPanel.add(this.searchField, BorderLayout.NORTH);
        // 中间：结果列表（带滚动条）
        JBScrollPane scrollPane = new JBScrollPane(this.jbList);
        scrollPane.setPreferredSize(new Dimension(450, 350));
        this.rootPanel.add(scrollPane, BorderLayout.CENTER);
    }



    public void showPopup(Project project) {
        JBPopup popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(this.rootPanel, this.searchField) // 焦点绑定到搜索框
                .setProject(project)
                .setRequestFocus(true) // 自动获取焦点
                .setResizable(true) // 可调整大小
                .setCancelOnClickOutside(true) // 点击外部关闭
                .setCancelKeyEnabled(true)
                .createPopup();
        // 4. 显示面板（居中显示）
        popup.showCenteredInCurrentWindow(project);
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

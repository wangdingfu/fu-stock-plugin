package cn.fudoc.trade.view.search;

import cn.fudoc.trade.api.data.StockInfo;
import cn.fudoc.trade.state.MarketAllStockPersistentState;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBUI;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 兼容版高级实时搜索对话框（替代 EditorSearchComponent）
 */
public class StockSearchDialog extends DialogWrapper {
    // 基础搜索框（稳定 API）
    private final SearchTextField searchField;
    // 搜索选项面板（正则、大小写敏感等）
    private final JPanel searchOptionsPanel;
    // 结果列表相关
    private final DefaultListModel<StockInfo> resultModel;
    private final JBList<StockInfo> resultList;
    private Timer debounceTimer;
    private final MarketAllStockPersistentState dataSource;
    public StockSearchDialog() {
        super(true);
        dataSource =  MarketAllStockPersistentState.getInstance();
        // 1. 初始化核心搜索框（SearchTextField 是稳定 API）
        searchField = new SearchTextField();
        // 监听输入变化，触发实时搜索
        searchField.addDocumentListener(new javax.swing.event.DocumentListener() {
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

        // 2. 初始化搜索选项面板（模拟 EditorSearchComponent 的选项功能）
        searchOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        searchOptionsPanel.setBorder(JBUI.Borders.emptyTop(8));


        // 3. 初始化结果列表
        resultModel = new DefaultListModel<>();
        resultList = new JBList<>(resultModel);
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.setCellRenderer(new ComplexListCellRenderer());

        resultList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                int index = resultList.getSelectedIndex();
            }
        });
        // 4. 防抖定时器（避免频繁搜索）
        debounceTimer = new Timer();
        setTitle("添加股票");
        init();
    }


    /**
     * 构建对话框布局（搜索框 + 选项面板 + 结果列表）
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(8, 8));
        contentPanel.setBorder(JBUI.Borders.empty(16));

        // 顶部：搜索框 + 选项面板（垂直排列）
        JPanel searchPanel = new JPanel(new VerticalLayout(8));
        searchPanel.add(searchField);
        searchPanel.add(searchOptionsPanel);
        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // 中间：结果列表（带滚动条）
        JBScrollPane scrollPane = new JBScrollPane(resultList);
        scrollPane.setPreferredSize(new Dimension(450, 350));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        return contentPanel;
    }

    /**
     * 带防抖的搜索触发（200ms 延迟）
     */
    private void triggerSearch() {
        if (debounceTimer != null) {
            debounceTimer.cancel();
        }
        debounceTimer = new Timer();
        debounceTimer.schedule(new TimerTask() {
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
        resultModel.clear();
        List<StockInfo> stockInfoList = dataSource.match(keyword);
        if(CollectionUtils.isNotEmpty(stockInfoList)){
            for (StockInfo stockInfo : stockInfoList) {
                resultModel.addElement(stockInfo);
            }
        }

    }

    /**
     * 获取选中结果（供外部调用）
     */
    @Nullable
    public StockInfo getSelectedResult() {
        return resultList.getSelectedValue();
    }

    /**
     * 释放资源（避免内存泄漏）
     */
    @Override
    public void dispose() {
        if (debounceTimer != null) {
            debounceTimer.cancel();
        }
        super.dispose();
    }
}
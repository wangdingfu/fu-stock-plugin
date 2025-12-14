package cn.fudoc.trade.view.table;

import cn.fudoc.trade.api.TencentApiService;
import cn.fudoc.trade.api.data.RealStockInfo;
import cn.hutool.core.date.DateUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class AbstractStockTableView implements StockTableView {

    /**
     * 当前tab股票集合
     */
    protected final Set<String> stockCodeSet = new HashSet<>();

    protected final JBTable stockTable;
    protected final DefaultTableModel tableModel;

    protected final JLabel tipLabel;

    protected String lastUpdateTime;

    /**
     * 表格展示的标题
     */
    protected abstract String[] getColumnNames();

    /**
     * 持久化删除股票
     */
    protected abstract void removeStockFromState(String stockCode);

    /**
     * 股票信息转换成表格展示的股票内容
     *
     * @param realStockInfo 股票实时信息
     */
    protected abstract Vector<Object> toTableData(RealStockInfo realStockInfo);


    public AbstractStockTableView() {
        this.tableModel = new DefaultTableModel(getColumnNames(), 0) {
            // 设置单元格不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable = new JBTable(tableModel);
        tipLabel = new JLabel();
    }


    protected JBColor getTextColor(double offset) {
        return offset > 0 ? JBColor.RED : JBColor.GREEN;
    }


    @Override
    public void reloadAllStock(String tag) {
        TencentApiService tencentApiService = ApplicationManager.getApplication().getService(TencentApiService.class);
        List<RealStockInfo> realStockInfos = tencentApiService.stockList(getStockCodes());
        if (CollectionUtils.isEmpty(realStockInfos)) {
            return;
        }
        // 清空现有数据
        tableModel.setRowCount(0);
        realStockInfos.forEach(this::addStock);
        lastUpdateTime = DateUtil.now();
    }

    @Override
    public void updateTipTag(String tag) {
        if (StringUtils.isBlank(lastUpdateTime)) {
            lastUpdateTime = "----------";
        }
        tipLabel.setText("最后更新时间：" + lastUpdateTime + (StringUtils.isBlank(tag) ? "" : "   [ " + tag + " ]"));
    }

    /**
     * 初始化当前表格股票信息
     *
     * @param stockCodeSet 股票集合
     */
    protected void init(Set<String> stockCodeSet) {
        if (CollectionUtils.isEmpty(stockCodeSet)) {
            return;
        }
        this.stockCodeSet.addAll(stockCodeSet);
        reloadAllStock();
    }

    /**
     * 当前表格展示的股票集合
     */
    @Override
    public Set<String> getStockCodes() {
        return stockCodeSet;
    }

    /**
     * 当前tab下展示的组件
     */
    @Override
    public JPanel getComponent() {
        //  创建右键菜单
        JPopupMenu popupMenu = createPopupMenu();
        stockTable.setComponentPopupMenu(popupMenu);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(stockTable);
        stockTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(decorator.createPanel(), BorderLayout.CENTER);
        tipLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rootPanel.add(tipLabel, BorderLayout.PAGE_END);
        return rootPanel;
    }

    @Override
    public void addStock(RealStockInfo realStockInfo) {
        String stockCode = realStockInfo.getStockCode();
        if (stockCodeSet.contains(stockCode)) {
            removeStock(stockCode);
        }
        tableModel.addRow(toTableData(realStockInfo));
        stockCodeSet.add(realStockInfo.getStockCode());
    }


    /**
     * 从表格中删除股票
     *
     * @param stockCode 股票代码
     */
    @Override
    public void removeStock(String stockCode) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object valueAt = tableModel.getValueAt(i, 0);
            if (stockCode.equals(valueAt.toString())) {
                int modelRow = stockTable.convertRowIndexToModel(i);
                tableModel.removeRow(modelRow);
                removeStockFromState(stockCode);
                stockCodeSet.remove(stockCode);
            }
        }
    }


    // 创建右键菜单（包含“删除”选项）
    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        // 添加“删除”菜单项
        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(e -> deleteSelectedRow()); // 绑定删除逻辑
        menu.add(deleteItem);
        return menu;
    }


    /**
     * 删除选中行的核心逻辑
     */
    private void deleteSelectedRow() {
        int[] selectedRows = stockTable.getSelectedRows();
        if (selectedRows == null || selectedRows.length == 0) {
            return;
        }
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int modelRow = stockTable.convertRowIndexToModel(selectedRows[i]);
            Object valueAt = tableModel.getValueAt(modelRow, 0);
            tableModel.removeRow(modelRow);
            //持久化移除
            String code = Objects.isNull(valueAt) ? "" : valueAt.toString();
            removeStockFromState(code);
            stockCodeSet.remove(code);
        }
    }
}

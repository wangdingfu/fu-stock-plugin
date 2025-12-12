package cn.fudoc.trade.view.stock;

import cn.fudoc.trade.api.data.RealStockInfo;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

public abstract class AbstractStockTabView implements StockTabView {

    /**
     * 当前tab股票集合
     */
    protected final Set<String> stockCodeSet = new HashSet<>();

    protected final JBTable stockTable;
    private final DefaultTableModel tableModel;

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

    public AbstractStockTabView(Set<String> stockCodeSet) {
        if (CollectionUtils.isNotEmpty(stockCodeSet)) {
            this.stockCodeSet.addAll(stockCodeSet);
        }
        this.tableModel = new DefaultTableModel(getColumnNames(), 0) {
            // 设置单元格不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable = new JBTable(tableModel);
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
        return decorator.createPanel();
    }


    /**
     * 新增股票至表格
     *
     * @param realStockInfo 股票实时信息
     */
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
            tableModel.removeRow(modelRow);
            //持久化移除
            Object valueAt = tableModel.getValueAt(modelRow, 0);
            String code = Objects.isNull(valueAt) ? "" : valueAt.toString();
            removeStockFromState(code);
            stockCodeSet.remove(code);
        }
    }
}

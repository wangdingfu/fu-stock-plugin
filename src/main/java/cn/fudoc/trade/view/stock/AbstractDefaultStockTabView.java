package cn.fudoc.trade.view.stock;

import cn.fudoc.trade.api.data.RealStockInfo;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

public abstract class AbstractDefaultStockTabView extends AbstractStockTabView {

    protected final JPanel rootPanel;
    protected final JBTable stockTable;
    private final DefaultTableModel tableModel;

    protected abstract String[] getColumnNames();

    protected abstract void removeStockFromState(String stockCode);

    protected abstract Vector<Object> toTableData(RealStockInfo realStockInfo);

    protected AbstractDefaultStockTabView(Set<String> stockCodeSet) {
        super(stockCodeSet);
        this.rootPanel = new JPanel(new BorderLayout());
        stockTable = initStockTable();
        this.tableModel = new DefaultTableModel(getColumnNames(), 0) {
            // 设置单元格不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    @Override
    public JPanel getComponent() {
        return this.rootPanel;
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


    private JBTable initStockTable() {
        JBTable stockTable = new JBTable(tableModel);
        // 2. 创建右键菜单
        JPopupMenu popupMenu = createPopupMenu();
        stockTable.setComponentPopupMenu(popupMenu);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(stockTable);
        stockTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.rootPanel.add(decorator.createPanel(), BorderLayout.CENTER);
        return stockTable;
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

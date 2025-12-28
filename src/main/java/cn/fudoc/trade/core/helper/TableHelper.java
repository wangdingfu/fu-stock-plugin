package cn.fudoc.trade.core.helper;

import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class TableHelper {

    private final JBTable stockTable;
    private final DefaultTableModel tableModel;
    private final TableListener tableListener;
    private final JPopupMenu tableMenu;

    public TableHelper(JBTable stockTable, DefaultTableModel tableModel, TableListener tableListener) {
        this.stockTable = stockTable;
        this.tableModel = tableModel;
        this.tableListener = tableListener;
        this.tableMenu = new JPopupMenu();
        //默认新增删除操作
        addMenu("删除", e -> deleteSelectedRow());
    }


    /**
     * 给表格新增右键菜单
     *
     * @param menuTitle      菜单标题
     * @param actionListener 触发事件
     */
    public void addMenu(String menuTitle, ActionListener actionListener) {
        JMenuItem deleteItem = new JMenuItem(menuTitle);
        deleteItem.addActionListener(actionListener);
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
            tableListener.removeRow(modelRow);
            tableModel.removeRow(modelRow);
        }
    }


    public JPanel createTablePanel() {
        stockTable.setComponentPopupMenu(tableMenu);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(stockTable);
        stockTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(decorator.createPanel(), BorderLayout.CENTER);
        return rootPanel;
    }
}

package cn.fudoc.trade.view;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import lombok.Getter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MyPositionsView {
    private final Project project;

    @Getter
    private final JPanel rootPanel;

    private final JBTable stockTable;

    private final DefaultTableModel tableModel;
    /**
     * 定义表格列名
     */
    private static final String[] columnNames = {"名称/市值", "持仓盈亏", "持仓/可用", "现价/成本"};


    public MyPositionsView(Project project) {
        this.project = project;
        this.rootPanel = new JPanel(new BorderLayout());
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            // 设置单元格不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.stockTable = initStockTable();
    }


    private JBTable initStockTable() {
        JBTable stockTable = new JBTable(tableModel);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(stockTable);
        stockTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.rootPanel.add(decorator.createPanel(), BorderLayout.CENTER);
        return stockTable;
    }
}

package cn.fudoc.trade.view.dialog.tab;

import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Objects;

/**
 * 交易记录 tab 视图
 */
public class HoldingsTradeLogTabView extends AbstractHoldingsTabView{
    protected final JBTable stockTable;
    protected final DefaultTableModel tableModel;
    private static final String[] columnNames = {"股票代码","股票名称","交易类型", "交易数量", "交易价格", "交易时间"};

    public HoldingsTradeLogTabView(StockInfoDTO stockInfoDTO, HoldingsInfo holdingsInfo) {
        super(stockInfoDTO, holdingsInfo);
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            // 设置单元格不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable = new JBTable(tableModel);
    }



    @Override
    public String getTabName() {
        return "交易记录";
    }

    @Override
    public JPanel getPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(getTableComponent());
        return mainPanel;
    }

    @Override
    public void submit(HoldingsInfo holdingsInfo) {

    }


    @Override
    public ValidationInfo doValidate() {
        return null;
    }


    protected JPanel getTableComponent(){
        //  创建右键菜单
        JPopupMenu popupMenu = createPopupMenu();
        stockTable.setComponentPopupMenu(popupMenu);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(stockTable);
        stockTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(decorator.createPanel(), BorderLayout.CENTER);
        return rootPanel;
    }

    // 创建右键菜单（包含“删除”选项）
    protected JPopupMenu createPopupMenu() {
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

        }
    }

}

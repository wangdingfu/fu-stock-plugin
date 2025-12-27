package cn.fudoc.trade.view.holdings.tab;

import cn.fudoc.trade.core.common.enumtype.TradeTypeEnum;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.core.state.pojo.TradeInfoLog;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import cn.hutool.core.date.DatePattern;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * 交易记录 tab 视图
 */
public class HoldingsTradeLogTabView extends AbstractHoldingsTabView {
    protected final JBTable stockTable;
    protected final DefaultTableModel tableModel;
    private static final String[] columnNames = {"ID", "交易类型", "交易数量", "交易价格", "交易时间"};

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
        TableColumn idColumn = stockTable.getColumnModel().getColumn(0);
        // 从视图中移除，模型仍保留
        stockTable.getColumnModel().removeColumn(idColumn);

        initData();
    }


    private void initData() {
        List<TradeInfoLog> tradeList = holdingsInfo.getTradeList();
        if (CollectionUtils.isEmpty(tradeList)) {
            return;
        }
        tradeList.forEach(f -> tableModel.addRow(toTableData(f)));
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
        List<TradeInfoLog> tradeList = holdingsInfo.getTradeList();

    }


    protected Vector<Object> toTableData(TradeInfoLog tradeInfoLog) {
        Vector<Object> vector = new Vector<>();
        vector.add(tradeInfoLog.getId());
        vector.add(TradeTypeEnum.getName(tradeInfoLog.getType()));
        vector.add(tradeInfoLog.getCount());
        vector.add(tradeInfoLog.getPrice());
        vector.add(DatePattern.NORM_DATETIME_FORMAT.format(new Date(tradeInfoLog.getTime())));
        return vector;
    }


    @Override
    public ValidationInfo doValidate() {
        return null;
    }


    protected JPanel getTableComponent() {
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
            tableModel.removeRow(modelRow);
        }
    }

}

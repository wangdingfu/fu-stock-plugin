package cn.fudoc.trade.view.table;

import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.core.common.enumtype.StockTabEnum;
import cn.fudoc.trade.core.state.StockGroupPersistentState;
import cn.fudoc.trade.util.NumberFormatUtil;
import cn.hutool.core.util.NumberUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Vector;

/**
 * 自选tab
 */
public class StockGroupTableView extends AbstractStockTableView {

    private final String tabName;
    private static final String[] stockTableColumn = {"股票代码", "股票名称", "当前价格", "涨跌幅(%)", "成交额"};
    private static final String[] colorColumnNames = {"涨跌幅(%)"};
    private final StockGroupPersistentState state;

    public StockGroupTableView(String tabName) {
        this.tabName = tabName;
        for (String columnName : colorColumnNames) {
            stockTable.getColumn(columnName).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    if (Objects.nonNull(value)) {
                        setForeground(getTextColor(NumberUtil.parseDouble(value.toString(), 0.0)));
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });
        }
        this.state = StockGroupPersistentState.getInstance();
        init(this.state.getStockCodes(tabName));
    }

    @Override
    public String getTabName() {
        return tabName;
    }

    @Override
    public StockTabEnum getTabEnum() {
        return StockTabEnum.STOCK_INFO;
    }


    @Override
    protected String[] getColumnNames() {
        return stockTableColumn;
    }

    @Override
    public void addStock(RealStockInfo realStockInfo) {
        super.addStock(realStockInfo);
        this.state.addStock(tabName, realStockInfo.getStockCode());
    }

    @Override
    protected void removeStockFromState(String stockCode) {
        this.state.removeStock(tabName, stockCode);
    }

    @Override
    protected Vector<Object> toTableData(RealStockInfo realStockInfo) {
        Vector<Object> vector = new Vector<>();
        vector.add(realStockInfo.getStockCode());
        vector.add(realStockInfo.getStockName());
        vector.add(realStockInfo.getCurrentPrice());
        vector.add(realStockInfo.getIncreaseRate()+"%");
        vector.add(realStockInfo.getVolume());
        return vector;
    }
}

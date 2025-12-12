package cn.fudoc.trade.view.stock;

import cn.fudoc.trade.api.data.RealStockInfo;

import java.util.Set;
import java.util.Vector;

/**
 * 持仓
 */
public class HoldingsStockTabView extends AbstractStockTabView {

    private final String tabName;
    private static final String[] columnNames = {"名称","数量","市值", "盈亏", "成本"};

    public HoldingsStockTabView(String tabName, Set<String> stockCodeSet) {
        super(stockCodeSet);
        this.tabName = tabName;
    }

    @Override
    public String getTabName() {
        return tabName;
    }


    @Override
    protected String[] getColumnNames() {
        return columnNames;
    }

    @Override
    protected void removeStockFromState(String stockCode) {

    }

    @Override
    protected Vector<Object> toTableData(RealStockInfo realStockInfo) {
        return null;
    }
}

package cn.fudoc.trade.view.stock;

import cn.fudoc.trade.api.data.RealStockInfo;

import java.util.Set;
import java.util.Vector;

/**
 * 自选tab
 */
public class WatchListStockTabView extends AbstractStockTabView {

    private final String tabName;
    private static final String[] stockTableColumn = {"股票代码", "股票名称", "当前价格", "涨跌幅(%)", "成交额"};

    public WatchListStockTabView(String tabName, Set<String> stockCodeSet) {
        super(stockCodeSet);
        this.tabName = tabName;
    }

    @Override
    public String getTabName() {
        return tabName;
    }


    @Override
    protected String[] getColumnNames() {
        return stockTableColumn;
    }

    @Override
    protected void removeStockFromState(String stockCode) {

    }

    @Override
    protected Vector<Object> toTableData(RealStockInfo realStockInfo) {
        Vector<Object> vector = new Vector<>();
        vector.add(realStockInfo.getStockCode());
        vector.add(realStockInfo.getStockName());
        vector.add(realStockInfo.getCurrentPrice());
        vector.add(realStockInfo.getIncreaseRate());
        vector.add(realStockInfo.getVolume());
        return vector;
    }
}

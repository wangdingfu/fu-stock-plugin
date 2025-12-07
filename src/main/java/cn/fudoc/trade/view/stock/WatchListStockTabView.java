package cn.fudoc.trade.view.stock;

import java.util.Set;

/**
 * 自选tab
 */
public class WatchListStockTabView extends AbstractDefaultStockTabView {

    private final String tabName;

    public WatchListStockTabView(String tabName, Set<String> stockCodeSet) {
        super(stockCodeSet);
        this.tabName = tabName;
    }

    @Override
    public String getTabName() {
        return tabName;
    }
}

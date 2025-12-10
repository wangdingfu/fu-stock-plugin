package cn.fudoc.trade.view.stock;

import cn.fudoc.trade.api.data.RealStockInfo;

import javax.swing.*;
import java.util.List;
import java.util.Set;

/**
 * 持仓
 */
public class HoldingsStockTabView extends AbstractStockTabView {

    private final String tabName;

    public HoldingsStockTabView(String tabName, Set<String> stockCodeSet) {
        super(stockCodeSet);
        this.tabName = tabName;
    }

    @Override
    public String getTabName() {
        return tabName;
    }

    @Override
    public JPanel getTabComponent() {
        return null;
    }

    @Override
    public void addStock(String stockCode) {

    }

    @Override
    public void removeStock(String stockCode) {

    }

    @Override
    public void initStockList(List<RealStockInfo> stockInfoList) {

    }
}

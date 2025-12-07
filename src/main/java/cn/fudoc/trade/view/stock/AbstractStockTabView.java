package cn.fudoc.trade.view.stock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractStockTabView implements StockTabView {

    /**
     * 当前tab股票集合
     */
    protected final Set<String> stockCodeSet = new HashSet<>();

    public AbstractStockTabView(Set<String> stockCodeSet) {
        if (CollectionUtils.isNotEmpty(stockCodeSet)) {
            this.stockCodeSet.addAll(stockCodeSet);
        }
    }

    @Override
    public boolean isContainsStock(String stockCode) {
        if (StringUtils.isBlank(stockCode)) {
            return false;
        }
        return stockCodeSet.contains(stockCode);
    }
}

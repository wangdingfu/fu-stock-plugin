package cn.fudoc.trade.view.stock;

import cn.fudoc.trade.common.StockTabEnum;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 股票tab工厂类
 */
public class StockTabFactory {


    public static StockTabView create(String tabName, StockTabEnum stockTabEnum) {
        if (StringUtils.isBlank(tabName) || Objects.isNull(stockTabEnum)) {
            return null;
        }
        if (StockTabEnum.STOCK_INFO.equals(stockTabEnum)) {
            return new WatchListStockTabView(tabName, Sets.newHashSet());
        } else if (StockTabEnum.STOCK_HOLD.equals(stockTabEnum)) {
            return new HoldingsStockTabView(tabName, Sets.newHashSet());
        }
        return null;
    }
}

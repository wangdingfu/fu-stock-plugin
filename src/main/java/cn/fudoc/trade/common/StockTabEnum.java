package cn.fudoc.trade.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StockTabEnum {
    /**
     * 股票信息
     */
    STOCK_INFO("股票分组"),
    /**
     * 持仓信息
     */
    STOCK_HOLD("持仓分组"),
    ;


    private final String groupName;


    @Override
    public String toString() {
        return groupName;
    }
}

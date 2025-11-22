package cn.fudoc.trade.api.data;

import lombok.Getter;
import lombok.Setter;

/**
 * 股票信息
 */
@Getter
@Setter
public class StockInfo {

    /**
     * 股票代码
     */
    private String code;

    /**
     * 股票名称
     */
    private String name;

    /**
     * 股票所属交易所
     */
    private String jys;

}

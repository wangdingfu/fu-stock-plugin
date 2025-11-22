package cn.fudoc.trade.api.data;

import lombok.Getter;
import lombok.Setter;

/**
 * 股票实时交易数据对象
 */
@Getter
@Setter
public class RealStockInfo {

    /**
     * 股票代码（如600519、000858）
     */
    private String stockCode;
    /**
     * 股票名称（如贵州茅台、五 粮 液）
     */
    private String stockName;
    /**
     * 当前价格
     */
    private String currentPrice;
    /**
     * 涨跌幅(%)
     */
    private String increaseRate;
    /**
     * 成交额
     */
    private String volume;
}

package cn.fudoc.trade.strategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockInfo {

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
     * 成交量(万手)（东方财富返回单位为“手”，需转换为“万手”）
     */
    private String volume;
}

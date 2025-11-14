package cn.fudoc.trade.strategy;

import java.util.List;

public interface FetchStockStrategy {


    /**
     * 策略实现类数据来源
     */
    FetchStockSourceEnum source();

    /**
     * 拉取股票数据
     *
     * @param codeList 股票代码集合
     * @return 股票数据集合
     */
    List<StockInfo> fetch(List<String> codeList);
}

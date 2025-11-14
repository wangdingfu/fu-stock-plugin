package cn.fudoc.trade.strategy;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 从腾讯api获取股票数据
 */
@Slf4j
public class TencentFetchStockStrategy implements FetchStockStrategy{
    private static final String BASE_URL = "http://qt.gtimg.cn/q=";

    @Override
    public FetchStockSourceEnum source() {
        return FetchStockSourceEnum.TENCENT;
    }

    @Override
    public List<StockInfo> fetch(List<String> codeList) {
        String codeStr = String.join(",", codeList);
        String requestUrl = BASE_URL + codeStr;;
        try {
            return parseStockSegment(HttpUtil.get(requestUrl));
        }catch (Exception e){
            log.warn("从腾讯获取股票实时信息异常:{}",e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * values[3]==>当前价
     * values[4]==>昨收
     * values[5]==>今开
     * values[6]==>成交量
     * values[30]==>时间
     * values[31]==>涨跌
     * values[32]==>涨幅 后需要新增%
     * values[33]==>当天最高价
     * values[34]==>当天最低价
     * values[36]==>成交量
     * values[37]==>成交额
     * values[38]==>换手 后需要新增%
     * values[43]==>振幅
     * values[44]==>流通值 单位：亿
     * values[45]==>总市值 单位：亿
     * values[46]==>市净率
     */
    private static List<StockInfo> parseStockSegment(String result) {
        List<StockInfo> stockInfoList = new ArrayList<>();
        String[] lines = result.split("\n");
        for (String line : lines) {
            String code = line.substring(line.indexOf("_") + 1, line.indexOf("="));
            String dataStr = line.substring(line.indexOf("=") + 2, line.length() - 2);
            String[] values = dataStr.split("~");
            StockInfo bean = new StockInfo();
            bean.setStockCode(code);
            bean.setStockName(values[1]);
            bean.setCurrentPrice(values[3]);
            bean.setIncreaseRate(values[32]+"%");
            bean.setVolume(values[36]);
            stockInfoList.add(bean);
        }
        return stockInfoList;
    }
}

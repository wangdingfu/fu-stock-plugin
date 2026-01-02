package cn.fudoc.trade.util;

import org.apache.commons.lang3.StringUtils;

public class StockUtils {


    public static String formatStockCode(String stockCode){
        if(StringUtils.isBlank(stockCode)){
            return stockCode;
        }
        return stockCode.replace("sh","").replace("sz","").replace("hk","");
    }
}

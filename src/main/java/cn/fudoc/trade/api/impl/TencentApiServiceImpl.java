package cn.fudoc.trade.api.impl;

import cn.fudoc.trade.api.TencentApiService;
import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.util.FuNumberUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * 腾讯金融API数据获取实现类
 */
@Slf4j
public class TencentApiServiceImpl implements TencentApiService {
    private static final BigDecimal Y = new BigDecimal("10000");
    private static final String BASE_URL = "http://qt.gtimg.cn/q=";


    @Override
    public List<RealStockInfo> stockList(Set<String> codeSet) {
        String codeStr = String.join(",", codeSet);
        String requestUrl = BASE_URL + codeStr;
        try {
            return parseStockSegment(HttpUtil.get(requestUrl));
        } catch (Exception e) {
            log.warn("从腾讯获取股票实时信息异常:{}", e.getMessage());
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
    private static List<RealStockInfo> parseStockSegment(String result) {
        List<RealStockInfo> realStockInfoList = new ArrayList<>();
        String[] lines = result.split("\n");
        for (String line : lines) {
            String code = line.substring(line.indexOf("_") + 1, line.indexOf("="));
            String dataStr = line.substring(line.indexOf("=") + 2, line.length() - 2);
            String[] values = dataStr.split("~");
            RealStockInfo bean = new RealStockInfo();
            bean.setStockCode(code);
            bean.setStockName(values[1]);
            bean.setCurrentPrice(values[3]);
            bean.setYesterdayPrice(values[4]);
            bean.setIncreaseRate(values[32]);
            String[] volume = formatVolume(values[37]);
            bean.setVolume(volume[0]);
            bean.setVolumeUnit(volume[1]);
            realStockInfoList.add(bean);
        }
        return realStockInfoList;
    }


    private static String[] formatVolume(String volume) {
        if(StringUtils.isEmpty(volume) || !NumberUtil.isNumber(volume)){
            return new String[]{"---",""};
        }
        BigDecimal bigDecimal = new BigDecimal(volume);
        if(bigDecimal.compareTo(Y) < 0){
            return new String[]{volume,"万"};
        }
        return new String[]{FuNumberUtil.format(NumberUtil.div(bigDecimal,Y,2)),"亿"};
    }
}

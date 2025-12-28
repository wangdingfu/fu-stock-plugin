package cn.fudoc.trade.core.state.pojo;

import cn.hutool.core.util.IdUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 持仓信息
 */
@Getter
@Setter
public class HoldingsInfo {

    /**
     * 成本价
     */
    private String cost;
    /**
     * 持仓数量
     */
    private Integer count;

    /**
     * 刷新交易记录时间
     */
    private Long refreshTime;

    /**
     * 交易记录(只记录当天交易记录)
     */
    private List<TradeInfoLog> tradeList;

    /**
     * 历史交易记录
     */
    private List<TradeInfoLog> logList;


    /**
     * 添加交易记录
     *
     * @param type  交易类型
     * @param count 交易数量
     * @param price 交易价格
     */
    public void add(Integer type, Integer count, String price, String handlingFee) {
        if (tradeList == null) {
            tradeList = new ArrayList<>();
        }
        TradeInfoLog tradeInfoLog = new TradeInfoLog();
        tradeInfoLog.setId(IdUtil.getSnowflakeNextId());
        tradeInfoLog.setCount(count);
        tradeInfoLog.setPrice(price);
        tradeInfoLog.setType(type);
        tradeInfoLog.setHandlingFee(handlingFee);
        tradeInfoLog.setTime(System.currentTimeMillis());
        tradeList.add(tradeInfoLog);
    }
}

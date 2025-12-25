package cn.fudoc.trade.core.state.pojo;

import lombok.Getter;
import lombok.Setter;

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
     * 交易记录
     */
    private List<TradeInfoLog> tradeLogList;
}

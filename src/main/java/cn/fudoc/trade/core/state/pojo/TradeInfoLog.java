package cn.fudoc.trade.core.state.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * 交易记录
 */
@Getter
@Setter
public class TradeInfoLog {

    /**
     * 交易记录 id
     */
    private Long id;
    /**
     * 交易类型 0:初始化持仓成本 1:买入 2:卖出
     */
    private Integer type;
    /**
     * 交易价格
     */
    private String price;
    /**
     * 交易数量
     */
    private Integer count;

    /**
     * 交易手续费
     */
    private String handlingFee;
    /**
     * 交易时间
     */
    private Long time;

}

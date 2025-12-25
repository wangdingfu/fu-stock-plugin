package cn.fudoc.trade.core.state.pojo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 交易记录
 */
@Getter
@Setter
public class TradeInfoLog {

    /**
     * 交易类型 0:初始化持仓成本 1:买入 2:卖出
     */
    private Integer type;
    /**
     * 交易价格
     */
    private BigDecimal price;
    /**
     * 交易数量
     */
    private Integer count;
    /**
     * 交易时间
     */
    private Long time;

}

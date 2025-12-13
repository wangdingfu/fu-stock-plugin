package cn.fudoc.trade.state.pojo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 持仓信息
 */
@Getter
@Setter
public class HoldingsInfo {

    /**
     * 成本价
     */
    private BigDecimal cost;
    /**
     * 持仓数量
     */
    private Integer count;
}

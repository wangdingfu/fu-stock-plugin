package cn.fudoc.trade.core.state.pojo;

import cn.fudoc.trade.api.data.RealStockInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HoldingsStockInfo extends RealStockInfo {


    /**
     * 成本价
     */
    private BigDecimal cost;
    /**
     * 持仓数量
     */
    private Integer count;
}

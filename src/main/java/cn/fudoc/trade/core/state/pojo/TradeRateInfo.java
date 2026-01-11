package cn.fudoc.trade.core.state.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TradeRateInfo {
    /**
     * 起收
     */
    private String minFee;
    /**
     * 佣金 券商收取 买入卖出均收取
     */
    private String commissionRate;

    /**
     * 印花税 卖出时交易所收取
     */
    private String stampDutyRate;

    /**
     * 过户费（上海）
     */
    private String transferSHRate;
    /**
     * 过户费（深圳）
     */
    private String transferSZRate;

    /**
     * 其他费率
     */
    private String otherRate;

    /**
     * 其他费用
     */
    private String otherFee;

    /**
     * 是否最低收取5元 默认true
     */
    private boolean isMin5;
}

package cn.fudoc.trade.core.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JYSEnum {
    /**
     * 上海交易所
     */
    SH("sh"),
    /**
     * 深圳交易所
     */
    SZ("sz"),
    /**
     * 香港交易所
     */
    HK("hk"),

    DEFAULT("default"),
    ;


    private final String code;
}

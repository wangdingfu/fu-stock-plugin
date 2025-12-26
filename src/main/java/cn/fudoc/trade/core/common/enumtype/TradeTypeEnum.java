package cn.fudoc.trade.core.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum TradeTypeEnum {
    /**
     * 股票信息
     */
    COST(0,"初始化成本" ),
    BUY(1,"买入" ),
    SELL(2,"卖出" ),

    ;


    private final int code;
    private final String name;


    public static String getName(Integer code){
        if(Objects.isNull(code)){
            return "";
        }
        for (TradeTypeEnum value : TradeTypeEnum.values()) {
            if(value.getCode() == code){
                return value.getName();
            }
        }
        return "";
    }
}

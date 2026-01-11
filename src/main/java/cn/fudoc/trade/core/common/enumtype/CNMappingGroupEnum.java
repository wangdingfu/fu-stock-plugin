package cn.fudoc.trade.core.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CNMappingGroupEnum {
    /**
     * 标题名称
     */
    TABLE_TITLE("标题名称"),
    /**
     * 股票分组
     */
    STOCK_GROUP("股票分组"),
    /**
     * 股票名称
     */
    STOCK_NAME("股票名称"),
    ;


    private final String groupName;


    @Override
    public String toString() {
        return groupName;
    }
}

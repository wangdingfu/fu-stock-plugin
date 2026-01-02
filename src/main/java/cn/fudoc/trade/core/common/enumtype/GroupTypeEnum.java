package cn.fudoc.trade.core.common.enumtype;

import icons.FuIcons;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;

@Getter
@AllArgsConstructor
public enum GroupTypeEnum {
    /**
     * 自选股票分组
     */
    STOCK_INFO("自选股票分组", FuIcons.FU_STOCK_INFO_GROUP),
    /**
     * 持仓股票分组
     */
    STOCK_HOLD("持仓股票分组",FuIcons.FU_STOCK_HOLD_GROUP),
    ;


    private final String groupName;
    private final Icon icon;


    @Override
    public String toString() {
        return groupName;
    }
}

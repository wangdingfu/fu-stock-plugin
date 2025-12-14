package cn.fudoc.trade.core.common.enumtype;

import com.intellij.icons.AllIcons;
import icons.FuIcons;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;

@Getter
@AllArgsConstructor
public enum StockTabEnum {
    /**
     * 股票信息
     */
    STOCK_INFO("股票分组", FuIcons.FU_STOCK_INFO_GROUP),
    /**
     * 持仓信息
     */
    STOCK_HOLD("持仓分组",FuIcons.FU_STOCK_HOLD_GROUP),
    ;


    private final String groupName;
    private final Icon icon;


    @Override
    public String toString() {
        return groupName;
    }
}

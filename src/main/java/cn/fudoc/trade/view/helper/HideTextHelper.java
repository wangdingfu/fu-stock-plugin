package cn.fudoc.trade.view.helper;

import cn.fudoc.trade.core.common.enumtype.CNMappingGroupEnum;
import cn.fudoc.trade.core.state.FuStockSettingState;
import cn.fudoc.trade.util.PinyinUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class HideTextHelper {

    private static FuStockSettingState fuStockSettingState;

    private static void init() {
        if (Objects.isNull(fuStockSettingState)) {
            fuStockSettingState = FuStockSettingState.getInstance();
        }
    }


    public static String mapping(String cn, CNMappingGroupEnum groupEnum) {
        if (StringUtils.isBlank(cn) || Objects.isNull(groupEnum)) {
            return cn;
        }
        init();
        String mapping = fuStockSettingState.mapping(groupEnum.getGroupName(), cn);
        if (Objects.isNull(mapping)) {
            mapping = cn.length() <= 2 ? PinyinUtil.getPinyin(cn, "") : PinyinUtil.getFirstLetterRandom(cn);
        }
        return mapping.toUpperCase();
    }


    public static String mappingUnit(String unit) {
        if (StringUtils.isBlank(unit)) {
            return "";
        }
        return switch (unit) {
            case "万" -> "W";
            case "亿" -> "Y";
            default -> unit;
        };
    }
}

package cn.fudoc.trade.view.helper;

import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.common.enumtype.CNMappingGroupEnum;
import cn.fudoc.trade.core.state.FuStockSettingState;
import cn.fudoc.trade.util.PinyinUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HideTextHelper {
    @Getter
    private static final Map<String, String> tableTitleMapping = new HashMap<>();
    @Getter
    private static final Map<String, String> groupMapping = new HashMap<>();

    static {
        tableTitleMapping.put("股票代码", "Code");
        tableTitleMapping.put("股票名称", "Name");
        tableTitleMapping.put("当前价格", "Price");
        tableTitleMapping.put("涨跌幅(%)", "Change(%)");
        tableTitleMapping.put("成交额", "Total");
        tableTitleMapping.put("今日收益", "Today Profit");
        tableTitleMapping.put("持仓收益", "Total Profit");

        groupMapping.put(FuTradeConstants.MY_SELECT_GROUP, FuTradeConstants.MY_SELECT_HIDE_GROUP);
        groupMapping.put(FuTradeConstants.MY_HOLD_GROUP, FuTradeConstants.MY_HOLD_HIDE_GROUP);
    }

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


    public static String[] mapingTable(String[] cnList) {
        if (Objects.isNull(cnList) || cnList.length == 0) {
            return cnList;
        }
        String[] mappings = new String[cnList.length];
        for (int i = 0; i < cnList.length; i++) {
            mappings[i] = mapping(cnList[i], CNMappingGroupEnum.TABLE_TITLE);
        }
        return mappings;
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

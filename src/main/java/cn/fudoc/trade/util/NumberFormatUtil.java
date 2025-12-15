package cn.fudoc.trade.util;

import cn.hutool.core.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

public class NumberFormatUtil {


    public static String format(BigDecimal value) {
        DecimalFormat decimalFormat = new DecimalFormat(",###.##");
        return decimalFormat.format(value);
    }

    public static String formatRate(BigDecimal value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##%");
        return decimalFormat.format(value);
    }


    public static BigDecimal convertBigDecimal(Object value) {
        if(Objects.isNull(value)){
            return BigDecimal.ZERO;
        }
        String strValue = value.toString().trim();
        if (StringUtils.isBlank(strValue)) {
            return BigDecimal.ZERO;
        }
        if (NumberUtil.isNumber(strValue)) {
            return new BigDecimal(strValue);
        }
        return NumberUtil.toBigDecimal(strValue);
    }
}

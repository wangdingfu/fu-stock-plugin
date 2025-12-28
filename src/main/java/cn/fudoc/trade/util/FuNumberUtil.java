package cn.fudoc.trade.util;

import cn.hutool.core.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

public class FuNumberUtil {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    public static final BigDecimal DECIMAL_10000 = new BigDecimal(10000);
    public static final BigDecimal DECIMAL_5 = new BigDecimal(5);

    public static String format(BigDecimal value) {
        DecimalFormat decimalFormat = new DecimalFormat(",###.##");
        return decimalFormat.format(value);
    }

    public static String formatCost(BigDecimal value) {
        DecimalFormat decimalFormat = new DecimalFormat(",###.###");
        return decimalFormat.format(value);
    }

    public static String formatCost(String value) {
        return formatCost(toBigDecimal(value));
    }

    public static String formatRate(BigDecimal value, boolean flag) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##%");
        if (flag) {
            value = value.divide(ONE_HUNDRED, 5, RoundingMode.CEILING);
        }
        return decimalFormat.format(value);
    }


    public static BigDecimal toBigDecimal(Object value) {
        if (Objects.isNull(value)) {
            return BigDecimal.ZERO;
        }
        String strValue = value.toString().trim();
        if (StringUtils.isBlank(strValue)) {
            return BigDecimal.ZERO;
        }
        try {
            if (NumberUtil.isNumber(strValue)) {
                return new BigDecimal(strValue);
            }
            return NumberUtil.toBigDecimal(strValue);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }


    public static Integer toInteger(Object value) {
        if (Objects.isNull(value)) {
            return 0;
        }
        String strValue = value.toString().trim();
        if (StringUtils.isBlank(strValue)) {
            return 0;
        }
        try {
            return NumberUtil.isInteger(strValue) ? Integer.parseInt(strValue) : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}

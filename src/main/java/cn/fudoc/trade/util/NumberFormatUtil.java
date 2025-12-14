package cn.fudoc.trade.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class NumberFormatUtil {


    public static String format(BigDecimal value) {
        DecimalFormat decimalFormat = new DecimalFormat(",###.##");
        return decimalFormat.format(value);
    }

    public static String formatRate(BigDecimal value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##%");
        return decimalFormat.format(value);
    }
}

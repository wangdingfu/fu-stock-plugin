package cn.fudoc.trade.view.render;

import cn.fudoc.trade.util.NumberFormatUtil;
import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Objects;

public class StockColorTableCellRenderer extends DefaultTableCellRenderer {
    public StockColorTableCellRenderer(Integer left) {
        if (Objects.nonNull(left)) {
            setBorder(BorderFactory.createEmptyBorder(0, left, 0, 0));
        }
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setAlignmentY(Component.CENTER_ALIGNMENT);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (Objects.nonNull(value)) {
            BigDecimal bigDecimal = NumberFormatUtil.convertBigDecimal(value);
            value = NumberFormatUtil.formatRate(bigDecimal);
            JBColor textColor = getTextColor(bigDecimal);
            if (Objects.nonNull(textColor)) {
                setForeground(textColor);
            }
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }


    protected JBColor getTextColor(BigDecimal value) {
        int diff = value.compareTo(BigDecimal.ZERO);
        if (diff == 0) {
            return null;
        }
        return diff > 0 ? JBColor.RED : JBColor.GREEN;
    }
}

package cn.fudoc.trade.view.render;

import cn.fudoc.trade.util.FuNumberUtil;
import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class StockColorTableCellRenderer extends DefaultTableCellRenderer {
    private final List<Integer> colorColumnList;
    public StockColorTableCellRenderer(List<Integer> colorColumnList) {
        this.colorColumnList = colorColumnList;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (this.colorColumnList.contains(column) && Objects.nonNull(value)) {
            BigDecimal bigDecimal = FuNumberUtil.toBigDecimal(value);
            value = FuNumberUtil.formatRate(bigDecimal,true);
            JBColor textColor = getTextColor(bigDecimal);
            if (Objects.nonNull(textColor)) {
                setForeground(textColor);
            }
        }else {
            setForeground(table.getForeground());
        }
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // 4. 设置水平居中 + 垂直居中（核心代码）
        if (component instanceof JLabel label) {
            label.setHorizontalAlignment(SwingConstants.CENTER); // 水平居中
            label.setVerticalAlignment(SwingConstants.CENTER);   // 垂直居中
        }

        component.setFont(component.getFont().deriveFont(12.0f));
        return component;
    }


    protected JBColor getTextColor(BigDecimal value) {
        int diff = value.compareTo(BigDecimal.ZERO);
        if (diff == 0) {
            return null;
        }
        return diff > 0 ? JBColor.RED : JBColor.GREEN;
    }
}

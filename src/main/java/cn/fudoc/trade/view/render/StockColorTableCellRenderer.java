package cn.fudoc.trade.view.render;

import cn.hutool.core.util.NumberUtil;
import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Objects;

public class StockColorTableCellRenderer extends DefaultTableCellRenderer {
    public StockColorTableCellRenderer(Integer left) {
        if(Objects.nonNull(left)){
            setBorder(BorderFactory.createEmptyBorder(0,left,0,0));
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (Objects.nonNull(value)) {
            JBColor textColor = getTextColor(NumberUtil.parseDouble(value.toString(), 0.0));
            if (Objects.nonNull(textColor)) {
                setForeground(textColor);
            }
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    protected JBColor getTextColor(double offset) {
        if (offset == 0) {
            return null;
        }
        return offset > 0 ? JBColor.RED : JBColor.GREEN;
    }
}

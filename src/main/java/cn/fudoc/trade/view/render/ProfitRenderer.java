package cn.fudoc.trade.view.render;


import cn.hutool.core.util.NumberUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

public class ProfitRenderer extends JPanel {

    private final JBLabel valueLabel = new JBLabel();

    public ProfitRenderer(String title) {
        // 设置垂直布局，实现上下分行
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setAlignmentY(Component.CENTER_ALIGNMENT);
        JBLabel titleLabel = new JBLabel(title);
        titleLabel.setForeground(JBColor.GRAY);
        titleLabel.setFont(titleLabel.getFont().deriveFont(11.0f));
        valueLabel.setFont(valueLabel.getFont().deriveFont(15.0f).deriveFont(Font.BOLD));
        add(titleLabel);
        add(valueLabel);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(3, 20, 0, 12));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(3, 20, 3, 12));
    }


    public void setValue(String value,boolean isColor) {
        if(isColor){
            double aDouble = NumberUtil.parseDouble(value, 0.0);
            valueLabel.setText(aDouble > 0 ? "+" + value : value);
            valueLabel.setForeground(getTextColor(aDouble));
        }else {
            valueLabel.setText(value);
        }
    }


    protected JBColor getTextColor(double offset) {
        return offset > 0 ? JBColor.RED : JBColor.GREEN;
    }
}

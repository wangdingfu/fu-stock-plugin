package cn.fudoc.trade.util;

import com.intellij.ui.components.JBLabel;

import javax.swing.*;
import java.awt.*;

public class FormPanelUtil {

    public static void addRow(JPanel mainPanel, String labelText, JComponent component) {
        mainPanel.add(createRowPanel(labelText, component));
        mainPanel.add(Box.createVerticalStrut(15));
    }

    public static void addRow(JPanel mainPanel, String labelText, JComponent component,boolean setComponentSize) {
        mainPanel.add(createRowPanel(labelText, component,setComponentSize));
        mainPanel.add(Box.createVerticalStrut(15));
    }

    public static JPanel createRowPanel(String labelText, JComponent component){
        return createRowPanel(labelText,component,true);
    }
    /**
     * 创建单行面板（标签 + 组件）
     */
    public static JPanel createRowPanel(String labelText, JComponent component, boolean setComponentSize) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 固定宽度标签
        JBLabel label = new JBLabel(labelText);
        label.setPreferredSize(new Dimension(100, 30));
        label.setMinimumSize(new Dimension(100, 30));
        label.setMaximumSize(new Dimension(100, 30));
        label.setAlignmentY(Component.CENTER_ALIGNMENT);

        // 组件占满剩余空间
        if(setComponentSize){
            component.setPreferredSize(new Dimension(150, 30));
            component.setMinimumSize(new Dimension(150, 30));
        }
        component.setAlignmentY(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(component);

        return panel;
    }
}

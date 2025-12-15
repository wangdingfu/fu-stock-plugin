package cn.fudoc.trade.view.render;

import cn.hutool.core.util.NumberUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class MultiLineTableCellRenderer extends JPanel implements TableCellRenderer {
    private final JBLabel topLabel = new JBLabel();
    private final JBLabel bottomLabel = new JBLabel();
    private final List<Integer> columnList;
    private final List<Integer> boldColumnList;

    public MultiLineTableCellRenderer(List<Integer> columnList, List<Integer> boldColumnList) {
        this.columnList = columnList;
        this.boldColumnList = boldColumnList;
        // 设置垂直布局，实现上下分行
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setAlignmentY(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalGlue());
        add(topLabel);
        add(bottomLabel);
        add(Box.createVerticalGlue());
        // 关键：Label文本水平居中
        topLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topLabel.setBorder(BorderFactory.createEmptyBorder(3, 20, 0, 12));
        bottomLabel.setBorder(BorderFactory.createEmptyBorder(3, 20, 3, 12));
        if(CollectionUtils.isNotEmpty(boldColumnList)){
            topLabel.setFont(topLabel.getFont().deriveFont(15.0f));
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // 处理选中状态的背景色（适配IDEA主题）
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            topLabel.setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            topLabel.setForeground(table.getForeground());
        }

        // 假设value是封装了上下两行数据的对象（如String[]）
        if (value instanceof String[] content) {
            topLabel.setText(content[0]);
            bottomLabel.setText(content[1]);
        } else {
            topLabel.setText(value == null ? "" : value.toString());
            bottomLabel.setText("");
            topLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        }
        if (boldColumnList.contains(column)) {
            Font defaultFont = bottomLabel.getFont();
            bottomLabel.setFont(defaultFont.deriveFont(11.0f));
            bottomLabel.setForeground(JBColor.GRAY);
        }
        setColor(column);
        return this;
    }


    private void setColor(int column) {
        if (!columnList.contains(column)) {
            return;
        }
        String text = topLabel.getText();
        if (StringUtils.isNotBlank(text)) {
            JBColor textColor = getTextColor(NumberUtil.parseDouble(text, 0.0));
            if (Objects.nonNull(textColor)) {
                topLabel.setForeground(textColor);
            }
        }
        String text2 = bottomLabel.getText();
        if (StringUtils.isNotBlank(text2)) {
            JBColor textColor = getTextColor(NumberUtil.parseDouble(text2, 0.0));
            if (Objects.nonNull(textColor)) {
                bottomLabel.setForeground(textColor);
            }
        }
    }

    protected JBColor getTextColor(double offset) {
        if (offset == 0) {
            return null;
        }
        return offset > 0 ? JBColor.RED : JBColor.GREEN;
    }
}
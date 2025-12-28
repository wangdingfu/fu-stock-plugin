package cn.fudoc.trade.view.render;

import cn.hutool.core.util.NumberUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
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
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 配置GridBag约束：水平+垂直都居中，组件不拉伸
        gbc.anchor = GridBagConstraints.CENTER;
        // 不允许组件拉伸，保持原有尺寸
        gbc.fill = GridBagConstraints.NONE;
        // 列索引固定为0（水平只有一列）
        gbc.gridx = 0;
        // 上下间距，无左右间距（避免偏移）
        gbc.insets = new Insets(3, 0, 0, 0);

        // 添加顶部标签（行索引0）
        gbc.gridy = 0;
        add(topLabel, gbc);

        // 添加底部标签（行索引1，调整上下间距）
        gbc.gridy = 1;
        gbc.insets = new Insets(3, 0, 3, 0);
        add(bottomLabel, gbc);

        // 标签文本强制居中（水平+垂直）
        topLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topLabel.setVerticalAlignment(SwingConstants.CENTER);
        bottomLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomLabel.setVerticalAlignment(SwingConstants.CENTER);

        // 面板自身背景透明（避免遮挡表格样式） 保留不透明，用于显示选中背景
        setOpaque(true);
        // 自适应表格单元格尺寸
        setPreferredSize(new Dimension(0, 0));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // 选中状态样式处理（适配IDEA主题）
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            topLabel.setForeground(table.getSelectionForeground());
            bottomLabel.setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            topLabel.setForeground(table.getForeground());
            bottomLabel.setForeground(table.getForeground());
        }

        // 重置标签边框（避免残留样式影响居中）
        topLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        bottomLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // 处理值渲染
        if (value instanceof String[] content) {
            topLabel.setText(content[0]);
            bottomLabel.setText(content[1]);
        } else {
            topLabel.setText(value == null ? "" : value.toString());
            bottomLabel.setText("");
            // 非数组类型：仅保留上下内边距，无左右边距
            topLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        }

        // 加粗列配置
        if (boldColumnList.contains(column)) {
            Font defaultFont = bottomLabel.getFont();
            bottomLabel.setFont(defaultFont.deriveFont(11.0f));
            bottomLabel.setForeground(JBColor.GRAY);
        }

        setColor(column);

        // 同步表格单元格尺寸
        setSize(table.getColumnModel().getColumn(column).getWidth(), table.getRowHeight(row));

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
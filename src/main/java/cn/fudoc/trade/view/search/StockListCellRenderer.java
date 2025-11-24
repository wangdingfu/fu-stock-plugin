package cn.fudoc.trade.view.search;

import cn.fudoc.trade.api.data.StockInfo;
import cn.fudoc.trade.state.StockGroupPersistentState;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBOptionButton;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import icons.FuIcons;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

/**
 * 自定义 JBList 单元格渲染器：图片 + 主文本 + 右侧可点击操作文本
 */
public class StockListCellRenderer extends DefaultListCellRenderer {
    // 描述文本颜色（适配 IDEA 主题：亮色主题浅灰，暗色主题深灰）
    private static final JBColor DESCRIPTION_COLOR = new JBColor(0x666666, 0xAAAAAA);

    @Override
    public Component getListCellRendererComponent(
            @NotNull JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // 1. 转换数据模型（非目标类型直接返回默认渲染）
        if (!(value instanceof StockInfo stockInfo)) {
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
        Color selectBackground = isSelected ? list.getSelectionBackground() : list.getBackground();
        // 2. 创建单元格主面板（垂直居中，左右边距）
        JBPanel<JBPanel<?>> cellPanel = new JBPanel<>(new BorderLayout(8, 0));
        cellPanel.setBorder(JBUI.Borders.empty(4, 8));

        // 3. 左侧：图片 + 主文本（水平排列）
        JBPanel<JBPanel<?>> leftPanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT, 6, 0));

        JBLabel iconLabel = new JBLabel(getIcon(stockInfo.getJys()));
        leftPanel.setOpaque(true);
        leftPanel.setBackground(selectBackground);
        leftPanel.add(iconLabel);

        // 主文本标签（默认字体，选中时同步列表选中色）
        JBLabel mainLabel = new JBLabel();
        mainLabel.setOpaque(true);
        mainLabel.setBackground(selectBackground);
        mainLabel.setText(buildHtmlText(stockInfo, list, isSelected));
        mainLabel.setVerticalAlignment(SwingConstants.CENTER); // 垂直居中

        leftPanel.add(mainLabel);
        cellPanel.add(leftPanel, BorderLayout.WEST);
//        cellPanel.add(actionLabel, BorderLayout.EAST);

        // 5. 选中状态背景（同步 JBList 选中色）
        cellPanel.setBackground(selectBackground);
        cellPanel.setOpaque(true);

        return cellPanel;
    }

    private Icon getIcon(String jys) {
        if ("SH".equals(jys)) {
            return FuIcons.FU_SH;
        } else if ("SZ".equals(jys)) {
            return FuIcons.FU_SZ;
        } else if ("HK".equals(jys)) {
            return FuIcons.FU_HK;
        }
        return null;
    }


    /**
     * 构建 HTML 文本：重要文本（默认样式）+ 灰色描述文本（小字体）
     *
     * @param item       数据模型
     * @param list       列表组件（用于获取选中色）
     * @param isSelected 是否选中
     * @return 格式化后的 HTML 文本
     */
    private String buildHtmlText(StockInfo item, JList<?> list, boolean isSelected) {
        // 重要文本颜色：选中时用列表选中前景色，未选中用默认前景色
        Color priorityColor = isSelected ? list.getSelectionForeground() : list.getForeground();
        String priorityColorHex = String.format("#%06x", priorityColor.getRGB() & 0xFFFFFF); // 转16进制颜色

        // 拼接 HTML：重要文本 + 描述文本（描述文本为 null 时只显示重要文本）
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        // 重要文本（保留默认字体大小，指定颜色）
        html.append(String.format("<span style='color:%s;'>%s</span>", priorityColorHex, escapeHtml(item.getName())));

        // 描述文本（灰色、小字体，与重要文本间距2px）
        if (StringUtils.isNotBlank(item.getCode())) {
            html.append(String.format(
                    "<span style='color:%s; font-size:8px; margin-left:2px;'> %s</span>",
                    getDescriptionColorHex(isSelected),
                    escapeHtml(item.getCode())
            ));
        }
        html.append("</html>");
        return html.toString();
    }

    /**
     * 获取描述文本的16进制颜色（选中时调整亮度，避免被选中背景覆盖）
     */
    private String getDescriptionColorHex(boolean isSelected) {
        Color color = isSelected ? DESCRIPTION_COLOR.darker() : DESCRIPTION_COLOR;
        return String.format("#%06x", color.getRGB() & 0xFFFFFF);
    }

    /**
     * 转义 HTML 特殊字符（避免文本中包含 <、> 等符号导致样式错乱）
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
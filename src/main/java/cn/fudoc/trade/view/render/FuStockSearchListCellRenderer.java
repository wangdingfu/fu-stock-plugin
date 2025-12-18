package cn.fudoc.trade.view.render;

import cn.fudoc.trade.api.data.StockInfo;
import com.intellij.icons.AllIcons;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import icons.FuIcons;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

public class FuStockSearchListCellRenderer extends JPanel implements ListCellRenderer<StockInfo> {
    // 描述文本颜色（适配 IDEA 主题：亮色主题浅灰，暗色主题深灰）
    private static final JBColor DESCRIPTION_COLOR = new JBColor(0x666666, 0xAAAAAA);
    private final JPanel leftPanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT, 6, 0));
    private final JLabel iconLabel = new JLabel();
    private final JLabel textLabel = new JLabel();
    private final JButton jButton = new JButton();

    // 初始化布局
    public FuStockSearchListCellRenderer() {
        // 设置布局：左侧图标，右侧上下两行文本
        setLayout(new BorderLayout(8, 0)); // 组件间距8px，垂直4px
        setBorder(JBUI.Borders.empty(4, 8));

        // 3. 左侧：图片 + 主文本（水平排列）
        // 左侧图标
        iconLabel.setPreferredSize(new Dimension(26, 26));
        leftPanel.add(iconLabel);
        textLabel.setVerticalAlignment(SwingConstants.CENTER); // 垂直居中
        textLabel.setPreferredSize(new Dimension(200, 26));
        leftPanel.add(textLabel);
        leftPanel.setOpaque(true);

        add(leftPanel, BorderLayout.WEST);
        jButton.setPreferredSize(new Dimension(32, 26));
        jButton.setOpaque(true);
        jButton.setRolloverEnabled(true);
        jButton.setContentAreaFilled(false); // 去掉按钮背景
        jButton.setFocusPainted(false); // 去掉焦点边框
        add(jButton, BorderLayout.EAST);
        // 设置面板透明（避免遮挡背景）
        setOpaque(true);
    }




    @Override
    public Component getListCellRendererComponent(JList<? extends StockInfo> list, StockInfo value, int index, boolean isSelected, boolean cellHasFocus) {
        // 1. 数据绑定（假设value是自定义的PluginItem）
        if (value != null) {
            iconLabel.setIcon(getIcon(value.getJys()));
            textLabel.setText(buildHtmlText(value,list,isSelected));
            jButton.setIcon(value.isAdd() ? FuIcons.FU_STOCK_SELECTED : AllIcons.General.Add);
        }
        Color selectBackground = isSelected ? list.getSelectionBackground() : list.getBackground();
        setBackground(selectBackground);
        leftPanel.setBackground(selectBackground);
        jButton.setBackground(selectBackground);
        // 3. 焦点样式
        setBorder(cellHasFocus ? BorderFactory.createLineBorder(JBColor.BLUE) : BorderFactory.createEmptyBorder());
        return this;
    }

    /**
     * 判断鼠标坐标是否落在按钮区域
     * @param list JList 组件
     * @param cellRect 单元格的矩形区域（相对于 JList）
     * @param mouseX 鼠标相对于 JList 的 X 坐标
     * @param mouseY 鼠标相对于 JList 的 Y 坐标
     * @return true = 点击了按钮
     */
    public boolean isButtonClicked(JList<?> list, Rectangle cellRect, int mouseX, int mouseY) {
        // 1. 计算按钮在 JList 中的绝对坐标（单元格坐标 + 按钮在渲染器中的相对坐标）
        Point buttonLocation = jButton.getLocation(); // 按钮在渲染器中的相对位置
        int buttonX = cellRect.x + buttonLocation.x;
        int buttonY = cellRect.y + buttonLocation.y;
        int buttonWidth = jButton.getWidth();
        int buttonHeight = jButton.getHeight();

        // 2. 判断鼠标坐标是否在按钮范围内
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth
                && mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
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
}

package cn.fudoc.trade.view.render;

import cn.fudoc.trade.util.NumberFormatUtil;
import cn.fudoc.trade.view.dto.HoldStockDataDto;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class RankListCellRenderer extends JPanel implements ListCellRenderer<HoldStockDataDto> {

    private final JLabel rankLabel = new JLabel();
    private final JLabel codeLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel profitLabel = new JLabel();

    public RankListCellRenderer() {
        // 设置布局：左侧图标，右侧上下两行文本
        setLayout(new BorderLayout(8, 0)); // 组件间距8px，垂直4px
        setBorder(JBUI.Borders.empty(4, 8));
        add(rankLabel, BorderLayout.WEST);
        add(nameLabel, BorderLayout.CENTER);
        add(profitLabel, BorderLayout.EAST);
        // 设置面板透明（避免遮挡背景）
        setOpaque(true);
    }


//    private JPanel stockPanel() {
//        JPanel stockPanel = new JPanel();
//        // 设置垂直布局，实现上下分行
//        stockPanel.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        stockPanel.add(codeLabel);
//        stockPanel.add(nameLabel);
//        codeLabel.setForeground(JBColor.GRAY);
//        codeLabel.setFont(codeLabel.getFont().deriveFont(11.0f));
//        nameLabel.setFont(nameLabel.getFont().deriveFont(15.0f).deriveFont(Font.BOLD));
//        codeLabel.setBorder(BorderFactory.createEmptyBorder(5, 20, 3, 12));
//        nameLabel.setBorder(BorderFactory.createEmptyBorder(3, 20, 3, 12));
//        return stockPanel;
//    }

    @Override
    public Component getListCellRendererComponent(JList<? extends HoldStockDataDto> list, HoldStockDataDto value, int index, boolean isSelected, boolean cellHasFocus) {
        // 1. 数据绑定（假设value是自定义的PluginItem）
        if (value != null) {
            rankLabel.setText(index + 1 + "");
            codeLabel.setText(value.getStockCode());
            nameLabel.setText(value.getStockName());
            profitLabel.setText(NumberFormatUtil.format(value.getTodayProfit()));
            profitLabel.setForeground(getTextColor(value.getTodayProfit()));
        }
        return this;
    }

    protected JBColor getTextColor(BigDecimal value) {
        int diff = BigDecimal.ZERO.compareTo(value);
        if (diff == 0) {
            return null;
        }
        return diff > 0 ? JBColor.RED : JBColor.GREEN;
    }
}

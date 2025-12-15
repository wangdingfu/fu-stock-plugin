package cn.fudoc.trade.view.render;

import cn.fudoc.trade.util.NumberFormatUtil;
import cn.fudoc.trade.view.dto.HoldStockDataDto;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Objects;

public class RankListCellRenderer extends JPanel implements ListCellRenderer<HoldStockDataDto> {

    private final JLabel rankLabel = new JLabel();
    private final JLabel codeLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel profitLabel = new JLabel();

    public RankListCellRenderer() {
        // 1. 初始化GridLayout：1行N列（N=组件数），间距0
        setLayout(new GridLayout(1, 3, 0, 0));
        setBorder(JBUI.Borders.empty(4, 8));

        rankLabel.setFont(rankLabel.getFont().deriveFont(15.0f).deriveFont(Font.BOLD));
        nameLabel.setFont(nameLabel.getFont().deriveFont(15.0f).deriveFont(Font.BOLD));
        profitLabel.setFont(profitLabel.getFont().deriveFont(15.0f).deriveFont(Font.BOLD));
        rankLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profitLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(rankLabel);
        add(nameLabel);
        add(profitLabel);
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
            JBColor textColor = getTextColor(value.getTodayProfit());
            if(Objects.nonNull(textColor)){
                profitLabel.setForeground(textColor);
            }
        }
        return this;
    }

    protected JBColor getTextColor(BigDecimal value) {
        int diff = value.compareTo(BigDecimal.ZERO);
        if (diff == 0) {
            return null;
        }
        return diff > 0 ? JBColor.RED : JBColor.GREEN;
    }
}

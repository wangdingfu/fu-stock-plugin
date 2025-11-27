package cn.fudoc.trade.view.search;

import cn.fudoc.trade.api.data.StockInfo;
import com.intellij.icons.AllIcons;
import com.intellij.ui.JBColor;
import icons.FuIcons;

import javax.swing.*;
import java.awt.*;

public class ComplexListCellRenderer extends JPanel implements ListCellRenderer<StockInfo> {

    private final JLabel iconLabel = new JLabel();
    private final JLabel textLabel = new JLabel();
    private final JLabel descLabel = new JLabel();
    private final JButton jButton = new JButton();

    // 初始化布局
    public ComplexListCellRenderer() {
        // 设置布局：左侧图标，右侧上下两行文本
        setLayout(new BorderLayout(8, 4)); // 组件间距8px，垂直4px

        // 左侧图标
        iconLabel.setPreferredSize(new Dimension(24, 24));
        add(iconLabel, BorderLayout.WEST);

        // 右侧文本面板（垂直布局）
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        descLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        descLabel.setForeground(JBColor.GRAY);
        textPanel.add(textLabel);
        textPanel.add(descLabel);
        add(textPanel, BorderLayout.CENTER);
        jButton.addActionListener(e -> {
            System.out.println("456" );
        });
        jButton.setPreferredSize(new Dimension(32, 26));
        add(jButton, BorderLayout.EAST);

        // 设置面板透明（避免遮挡背景）
        setOpaque(true);
    }




    @Override
    public Component getListCellRendererComponent(JList<? extends StockInfo> list, StockInfo value, int index, boolean isSelected, boolean cellHasFocus) {
        // 1. 数据绑定（假设value是自定义的PluginItem）
        if (value != null) {
            iconLabel.setIcon(getIcon(value.getJys()));
            textLabel.setText(value.getName());
            descLabel.setText(value.getCode());
            jButton.setIcon(value.isAdd() ? AllIcons.General.GreenCheckmark : AllIcons.General.Add);
        }

        // 2. 选中状态样式
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            textLabel.setForeground(list.getSelectionForeground());
            descLabel.setForeground(JBColor.WHITE); // 选中时描述文本也变白

        } else {
            setBackground(list.getBackground());
            textLabel.setForeground(list.getForeground());
            descLabel.setForeground(JBColor.GRAY);
        }

        // 3. 焦点样式
        setBorder(cellHasFocus ? BorderFactory.createLineBorder(JBColor.BLUE) : BorderFactory.createEmptyBorder());

        return this;
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

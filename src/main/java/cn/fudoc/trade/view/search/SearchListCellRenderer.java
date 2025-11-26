package cn.fudoc.trade.view.search;

import cn.fudoc.trade.api.data.StockInfo;
import com.intellij.icons.AllIcons;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import icons.FuIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class SearchListCellRenderer extends ColoredListCellRenderer<StockInfo> {

    @Override
    protected void customizeCellRenderer(@NotNull JList<? extends StockInfo> list, StockInfo value, int index, boolean selected, boolean hasFocus) {
        // 1. 添加图标
        setIcon(getIcon(value.getJys()));

        // 2. 添加标题（主文本）
        append(value.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);

        // 3. 添加副标题（灰色）
        append(" - " + value.getCode(), SimpleTextAttributes.GRAY_ATTRIBUTES);

        // 4. 高亮匹配关键词
    }
    private JButton buildToolBarButton(StockInfo stockInfo) {
        JButton button = new JButton();
        button.setIcon(stockInfo.isAdd() ? AllIcons.General.GreenCheckmark : AllIcons.General.Add);
        button.addActionListener(e -> {
            button.setIcon(AllIcons.General.GreenCheckmark);
            System.out.println("456" + stockInfo.getCode());
        });
        button.setPreferredSize(new Dimension(32, 26));
        return button;
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

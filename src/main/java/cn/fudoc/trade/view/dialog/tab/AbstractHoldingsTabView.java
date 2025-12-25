package cn.fudoc.trade.view.dialog.tab;

import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * 维护持仓信息抽象类
 */
public abstract class AbstractHoldingsTabView implements HoldingsTabView {
    protected final JLabel stockCodeLabel;
    protected final JLabel stockNameLabel;
    protected final HoldingsInfo holdingsInfo;

    /**
     * 初始化持仓信息
     *
     * @param holdingsInfo 持仓持久化数据
     */
    protected void initData(HoldingsInfo holdingsInfo) {

    }

    /**
     * 创建展示面板
     */
    protected JPanel createPanel() {
        return null;
    }

    public AbstractHoldingsTabView(StockInfoDTO stockInfoDTO, HoldingsInfo holdingsInfo) {
        this.stockCodeLabel = new JBLabel(stockInfoDTO.stockCode());
        this.stockNameLabel = new JBLabel(stockInfoDTO.stockName());
        this.holdingsInfo = holdingsInfo;
    }


    @Override
    public JPanel getPanel() {
        JPanel mainPanel = addStockPanel();

        JPanel panel = createPanel();
        if (Objects.nonNull(panel)) {
            mainPanel.add(panel);
        }

        return mainPanel;
    }


    protected JPanel addStockPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(JBUI.Borders.empty(20, 30));

        // 股票代码行
        JPanel codePanel = createRowPanel("股票代码：", stockCodeLabel);
        mainPanel.add(codePanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // 股票名称行
        JPanel namePanel = createRowPanel("股票名称：", stockNameLabel);
        mainPanel.add(namePanel);
        mainPanel.add(Box.createVerticalStrut(15));
        return mainPanel;
    }

    /**
     * 创建单行面板（标签 + 组件）
     */
    protected JPanel createRowPanel(String labelText, JComponent component) {
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
        component.setPreferredSize(new Dimension(100, 30));
        component.setMinimumSize(new Dimension(100, 30));
        component.setAlignmentY(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(component);

        return panel;
    }

}

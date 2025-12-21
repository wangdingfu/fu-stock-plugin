package cn.fudoc.trade.view.dialog;

import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.state.HoldingsStockState;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.hutool.core.util.NumberUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;



public class HoldingsStockDialog extends DialogWrapper {

    private final JLabel stockCodeLabel;
    private final JLabel stockNameLabel;
    // 输入组件
    private final JBTextField costField = new JBTextField();
    private final JBTextField countField = new JBTextField();

    // 构造器：接收父窗口（null 则以 IDE 为父窗口）
    public HoldingsStockDialog(@Nullable Project project, String group, String stockCode, String stockName) {
        super(project, true);
        this.stockCodeLabel = new JBLabel(stockCode);
        this.stockNameLabel = new JBLabel(stockName);
        HoldingsInfo holdingsInfo = HoldingsStockState.getInstance().getHoldingsInfo(group, stockCode);
        if (Objects.nonNull(holdingsInfo)) {
            costField.setText(holdingsInfo.getCost());
            Integer count = holdingsInfo.getCount();
            countField.setText(Objects.isNull(count) ? "" : count.toString());
        }
        // 弹框标题
        setTitle("设置持仓信息");
        // 初始化 DialogWrapper（必须调用）
        init();
    }

    // 构建内容面板（BoxLayout 基础布局，无兼容问题）
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
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

        // 成本价行
        JPanel costPanel = createRowPanel("成本价：", costField);
        mainPanel.add(costPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // 持仓数量行
        JPanel countPanel = createRowPanel("持仓数量：", countField);
        mainPanel.add(countPanel);

        return mainPanel;
    }

    /**
     * 创建单行面板（标签 + 组件）
     */
    private JPanel createRowPanel(String labelText, JComponent component) {
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
        component.setPreferredSize(new Dimension(150, 30));
        component.setMinimumSize(new Dimension(150, 30));
        component.setAlignmentY(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(component);

        return panel;
    }

    // 输入校验（保持不变）
    @Override
    protected ValidationInfo doValidate() {
        String costValue = costField.getText().trim();
        String countValue = countField.getText().trim();

        if (StringUtils.isBlank(costValue)) {
            return new ValidationInfo(FuTradeConstants.HOLD_COST_NOTNULL, costField);
        }
        if (StringUtils.isBlank(countValue)) {
            return new ValidationInfo(FuTradeConstants.HOLD_COUNT_NOTNULL, countField);
        }
        if (!NumberUtil.isNumber(costValue)) {
            return new ValidationInfo(FuTradeConstants.HOLD_COST_IS_NUMBER, costField);
        }
        if (!NumberUtil.isInteger(countValue)) {
            return new ValidationInfo(FuTradeConstants.HOLD_COUNT_IS_NUMBER, countField);
        }

        try {
            int count = Integer.parseInt(countValue);
            if (count <= 0) {
                return new ValidationInfo(FuTradeConstants.HOLD_COUNT_GT_ZERO, countField);
            }
        } catch (Exception e) {
            return new ValidationInfo(FuTradeConstants.HOLD_COUNT_FORMAT_ERROR, countField);
        }
        return null;
    }

    // 获取持仓信息（确保 EDT 线程调用）
    public HoldingsInfo getHoldingsInfo() {
        HoldingsInfo holdingsInfo = new HoldingsInfo();
        holdingsInfo.setCost(costField.getText().trim());
        holdingsInfo.setCount(NumberUtil.parseInt(countField.getText().trim()));
        return holdingsInfo;
    }

    // 自定义按钮
    @Override
    protected Action @NotNull [] createActions() {
        getOKAction().putValue(Action.NAME, "确定");
        getCancelAction().putValue(Action.NAME, "取消");
        return new Action[]{getOKAction(), getCancelAction()};
    }
}
package cn.fudoc.trade.view;

import cn.fudoc.trade.state.HoldingsStockState;
import cn.fudoc.trade.state.pojo.HoldingsInfo;
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
import java.math.BigDecimal;
import java.util.Objects;

// 分组弹框
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

    // 构建弹框内容面板（核心布局）
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // 使用 GridBagLayout 实现组件对齐布局
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(5); // 组件间距
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充
        gbc.weightx = 1.0; // 横向权重（占满剩余空间）
        // 1. 分组名称标签 + 输入框
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // 标签不占额外空间
        panel.add(new JBLabel("股票代码："), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // 输入框占满横向空间
        stockCodeLabel.setPreferredSize(new Dimension(200, 28)); // 输入框宽度
        panel.add(stockCodeLabel, gbc);

        // 2. 分组类型标签 + 下拉框
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JBLabel("股票名称："), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        stockNameLabel.setPreferredSize(new Dimension(200, 28)); // 下拉框宽度
        panel.add(stockNameLabel, gbc);

        // 1. 分组名称标签 + 输入框
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0; // 标签不占额外空间
        panel.add(new JBLabel("成本价："), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // 输入框占满横向空间
        costField.setPreferredSize(new Dimension(200, 28)); // 输入框宽度
        panel.add(costField, gbc);

        // 2. 分组类型标签 + 下拉框
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(new JBLabel("持仓数量："), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        countField.setPreferredSize(new Dimension(200, 28)); // 下拉框宽度
        panel.add(countField, gbc);
        return panel;
    }

    // 输入校验（必填项检查）
    @Override
    protected ValidationInfo doValidate() {
        String costValue = costField.getText().trim();
        String countValue = countField.getText().trim();
        if (StringUtils.isBlank(costValue)) {
            // 校验失败：返回错误提示和关联组件（输入框会被高亮）
            return new ValidationInfo("成本价不能为空", costField);
        }
        if (StringUtils.isBlank(countValue)) {
            // 校验失败：返回错误提示和关联组件（输入框会被高亮）
            return new ValidationInfo("持仓数量不能为空", countField);
        }
        if (!NumberUtil.isNumber(costValue)) {
            // 校验失败：返回错误提示和关联组件（输入框会被高亮）
            return new ValidationInfo("成本价必须是数字", costField);
        }
        if (!NumberUtil.isInteger(countValue)) {
            // 校验失败：返回错误提示和关联组件（输入框会被高亮）
            return new ValidationInfo("持仓数量必须是数字", countField);
        }
        // 校验通过：返回 null
        return super.doValidate();
    }

    public HoldingsInfo getHoldingsInfo() {
        HoldingsInfo holdingsInfo = new HoldingsInfo();
        holdingsInfo.setCost(costField.getText().trim());
        holdingsInfo.setCount(NumberUtil.parseInt(countField.getText().trim()));
        return holdingsInfo;
    }


    // 启用“确认”和“取消”按钮（默认启用，可自定义按钮文本）
    @Override
    protected Action @NotNull [] createActions() {
        // 自定义确认按钮文本（默认是“OK”，改为“确定”更符合中文习惯）
        getOKAction().putValue(Action.NAME, "确定");
        getCancelAction().putValue(Action.NAME, "取消");
        return new Action[]{getOKAction(), getCancelAction()};
    }
}
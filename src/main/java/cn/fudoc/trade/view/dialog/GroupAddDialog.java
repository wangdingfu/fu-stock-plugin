package cn.fudoc.trade.view.dialog;

import cn.fudoc.trade.core.common.enumtype.StockTabEnum;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * 添加股票分组弹框
 */
public class GroupAddDialog extends DialogWrapper {
    // 输入组件
    private final JBTextField groupNameField = new JBTextField();
    private final ComboBox<StockTabEnum> groupTypeComboBox = new ComboBox<>(StockTabEnum.values());

    // 构造器：接收父窗口（null 则以 IDE 为父窗口）
    public GroupAddDialog(@Nullable Project project) {
        super(project, true);
        // 弹框标题
        setTitle("新增股票分组");
        // 初始化 DialogWrapper（必须调用）
        init();
        // 初始化组件数据
        initComponents();
    }

    private void initComponents() {
        // 设置默认选中第一项（股票分组）
        groupTypeComboBox.setSelectedIndex(0);
    }

    // 构建弹框内容面板（核心布局）
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // 使用 GridBagLayout 实现组件对齐布局
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 组件间距
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充
        gbc.weightx = 1.0; // 横向权重（占满剩余空间）

        // 1. 分组名称标签 + 输入框
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // 标签不占额外空间
        panel.add(new JBLabel("分组名称："), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // 输入框占满横向空间
        groupNameField.setPreferredSize(new Dimension(250, 28)); // 输入框宽度
        panel.add(groupNameField, gbc);

        // 2. 分组类型标签 + 下拉框
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JBLabel("分组类型："), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        groupTypeComboBox.setPreferredSize(new Dimension(250, 28)); // 下拉框宽度
        panel.add(groupTypeComboBox, gbc);

        return panel;
    }

    // 输入校验（必填项检查）
    @Override
    protected ValidationInfo doValidate() {
        String groupName = groupNameField.getText().trim();
        if (groupName.isEmpty()) {
            // 校验失败：返回错误提示和关联组件（输入框会被高亮）
            return new ValidationInfo("分组名称不能为空", groupNameField);
        }
        // 校验通过：返回 null
        return super.doValidate();
    }

    public String getGroupName() {
        return groupNameField.getText().trim();
    }

    public StockTabEnum getStockTabEnum() {
        return (StockTabEnum)groupTypeComboBox.getSelectedItem();
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
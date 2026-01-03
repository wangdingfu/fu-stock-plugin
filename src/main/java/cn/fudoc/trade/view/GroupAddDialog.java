package cn.fudoc.trade.view;

import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.common.enumtype.GroupTypeEnum;
import cn.fudoc.trade.core.listener.DocumentCallback;
import cn.fudoc.trade.core.listener.TextFieldDocumentListener;
import cn.fudoc.trade.core.state.pojo.StockGroupInfo;
import cn.fudoc.trade.util.FormPanelUtil;
import cn.fudoc.trade.util.PinyinUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.util.Objects;

/**
 * 添加股票分组弹框
 */
public class GroupAddDialog extends DialogWrapper implements DocumentCallback {
    // 输入组件
    private final JBTextField groupNameField = new JBTextField();
    private final JBTextField hideGroupNameField = new JBTextField();
    private final ComboBox<GroupTypeEnum> groupTypeComboBox = new ComboBox<>(GroupTypeEnum.values());

    // 构造器：接收父窗口（null 则以 IDE 为父窗口）
    public GroupAddDialog(@Nullable Project project) {
        super(project, true);
        // 弹框标题
        setTitle("新增股票分组");
        // 初始化 DialogWrapper（必须调用）
        init();
        // 初始化组件数据
        initComponents();
        //添加监听器
        addListener();
    }

    private void initComponents() {
        // 设置默认选中第一项（股票分组）
        groupTypeComboBox.setSelectedIndex(0);
    }

    // 构建弹框内容面板（核心布局）
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(JBUI.Borders.empty(20, 30));
        FormPanelUtil.addRow(mainPanel, "分组名称：", groupNameField);
        FormPanelUtil.addRow(mainPanel, "隐蔽名称：", hideGroupNameField);
        FormPanelUtil.addRow(mainPanel, "分组类型：", groupTypeComboBox);
        return mainPanel;
    }

    // 输入校验（必填项检查）
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isBlank(groupNameField.getText().trim())) {
            return new ValidationInfo(FuTradeConstants.GROUP_NAME_NOT_NULL, groupNameField);
        }
        if (StringUtils.isBlank(hideGroupNameField.getText().trim())) {
            return new ValidationInfo(FuTradeConstants.HIDE_GROUP_NAME_NOT_NULL, groupNameField);
        }
        if (Objects.isNull(groupTypeComboBox.getSelectedItem())) {
            return new ValidationInfo(FuTradeConstants.GROUP_TYPE_NOT_NULL, groupNameField);
        }
        // 校验通过：返回 null
        return super.doValidate();
    }


    public StockGroupInfo getStockGroupInfo() {
        return new StockGroupInfo(groupNameField.getText().trim(), hideGroupNameField.getText().trim(), (GroupTypeEnum) groupTypeComboBox.getSelectedItem());
    }

    private void addListener(){
        groupNameField.getDocument().addDocumentListener(new TextFieldDocumentListener(1, this));
    }

    // 启用“确认”和“取消”按钮（默认启用，可自定义按钮文本）
    @Override
    protected Action @NotNull [] createActions() {
        // 自定义确认按钮文本（默认是“OK”，改为“确定”更符合中文习惯）
        getOKAction().putValue(Action.NAME, "确定");
        getCancelAction().putValue(Action.NAME, "取消");
        return new Action[]{getOKAction(), getCancelAction()};
    }

    @Override
    public void callback(Integer type, DocumentEvent event) {
        String trim = groupNameField.getText().trim();
        hideGroupNameField.setText((trim.length() <=2 ? PinyinUtil.getPinyin(trim,"") : PinyinUtil.getFirstLetterRandom(trim)).toUpperCase());
    }
}
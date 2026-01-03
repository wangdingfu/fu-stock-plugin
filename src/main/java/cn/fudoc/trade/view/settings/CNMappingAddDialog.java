package cn.fudoc.trade.view.settings;

import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.common.enumtype.CNMappingGroupEnum;
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
public class CNMappingAddDialog extends DialogWrapper implements DocumentCallback {
    // 输入组件
    private final JBTextField cnField = new JBTextField();
    private final JBTextField enField = new JBTextField();
    private final ComboBox<CNMappingGroupEnum> typeComboBox = new ComboBox<>(CNMappingGroupEnum.values());

    // 构造器：接收父窗口（null 则以 IDE 为父窗口）
    public CNMappingAddDialog(@Nullable Project project) {
        super(project, true);
        // 弹框标题
        setTitle("新增中文映射");
        // 初始化 DialogWrapper（必须调用）
        init();
        // 初始化组件数据
        initComponents();
        //添加监听器
        addListener();
    }

    private void initComponents() {
        // 设置默认选中第一项（股票分组）
        typeComboBox.setSelectedIndex(0);
    }

    // 构建弹框内容面板（核心布局）
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(JBUI.Borders.empty(20, 30));
        FormPanelUtil.addRow(mainPanel, "中文名称：", cnField);
        FormPanelUtil.addRow(mainPanel, "隐蔽名称：", enField);
        FormPanelUtil.addRow(mainPanel, "分组类型：", typeComboBox);
        return mainPanel;
    }

    // 输入校验（必填项检查）
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isBlank(cnField.getText().trim())) {
            return new ValidationInfo(FuTradeConstants.LANGUAGE_CN_NOT_NULL, cnField);
        }
        if (StringUtils.isBlank(enField.getText().trim())) {
            return new ValidationInfo(FuTradeConstants.HIDE_GROUP_NAME_NOT_NULL, enField);
        }
        if (Objects.isNull(typeComboBox.getSelectedItem())) {
            return new ValidationInfo(FuTradeConstants.LANGUAGE_TYPE_NOT_NULL, typeComboBox);
        }
        // 校验通过：返回 null
        return super.doValidate();
    }


    public Object[] getData() {
        return new Object[]{cnField.getText().trim(), enField.getText().trim(), typeComboBox.getSelectedItem()};
    }


    private void addListener() {
        cnField.getDocument().addDocumentListener(new TextFieldDocumentListener(1, this));
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
        String trim = cnField.getText().trim();
        enField.setText((trim.length() <= 2 ? PinyinUtil.getPinyin(trim, "") : PinyinUtil.getFirstLetterRandom(trim)).toUpperCase());
    }
}
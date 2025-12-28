package cn.fudoc.trade.view.settings.tab;

import com.intellij.openapi.ui.ValidationInfo;

import javax.swing.*;

public interface SettingTab {

    String getTabName();

    /**
     * 创建展示设置的页面
     */
    JPanel createPanel();

    /**
     * 表单信息校验
     */
    ValidationInfo doValidate();

    /**
     * 提交时触发
     */
    void submit();

}

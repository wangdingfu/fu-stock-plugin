package cn.fudoc.trade.view.settings.tab;

import javax.swing.*;

public interface SettingTab {

    String getTabName();

    /**
     * 创建展示设置的页面
     */
    JPanel createPanel();

    /**
     * 提交时触发
     */
    void submit();

}

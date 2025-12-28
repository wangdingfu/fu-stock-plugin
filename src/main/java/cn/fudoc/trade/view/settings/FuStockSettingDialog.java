package cn.fudoc.trade.view.settings;

import cn.fudoc.trade.view.settings.tab.RateSettingsTab;
import cn.fudoc.trade.view.settings.tab.SettingTab;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * 基础信息设置页面
 */
public class FuStockSettingDialog extends DialogWrapper {

    private final JBTabs tabs;

    /**
     * 交易费率维护页面
     */
    private final RateSettingsTab rateSettingsTab = new RateSettingsTab();


    public FuStockSettingDialog(Project project) {
        super(project, true);
        this.tabs = JBTabsFactory.createTabs(project);
        initTab();
        // 弹框标题
        setTitle("基础设置");
        // 初始化 DialogWrapper（必须调用）
        init();
    }

    @Override
    protected void doOKAction() {
        rateSettingsTab.submit();

        super.doOKAction();
    }

    private void initTab() {
        addTab(rateSettingsTab);
    }


    private void addTab(SettingTab settingTab) {
        TabInfo tabInfo = new TabInfo(settingTab.createPanel());
        tabInfo.setText(settingTab.getTabName());
        this.tabs.addTab(tabInfo);
    }


    @Override
    protected @Nullable ValidationInfo doValidate() {
        return rateSettingsTab.doValidate();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(tabs.getComponent());
        rootPanel.setFont(JBUI.Fonts.label(11));
        return rootPanel;
    }
}

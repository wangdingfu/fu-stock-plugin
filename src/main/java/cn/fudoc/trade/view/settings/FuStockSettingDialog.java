package cn.fudoc.trade.view.settings;

import cn.fudoc.trade.view.settings.tab.CNMappingSettingTab;
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
    private RateSettingsTab rateSettingsTab;
    /**
     * 中文映射页面
     */
    private CNMappingSettingTab cnMappingSettingTab;

    public FuStockSettingDialog(Project project) {
        this(project, null);
    }

    public FuStockSettingDialog(Project project, String holdingsGroup) {
        super(project, true);
        this.tabs = JBTabsFactory.createTabs(project);
        initTab(holdingsGroup);
        // 弹框标题
        setTitle("基础设置");
        // 初始化 DialogWrapper（必须调用）
        init();
    }

    @Override
    protected void doOKAction() {
        rateSettingsTab.submit();
        cnMappingSettingTab.submit();
        super.doOKAction();
    }

    private void initTab(String holdingsGroup) {
        this.rateSettingsTab = new RateSettingsTab(holdingsGroup);
        this.cnMappingSettingTab = new CNMappingSettingTab();
        TabInfo tabInfo = addTab(rateSettingsTab);
        addTab(cnMappingSettingTab);
        tabs.select(tabInfo, true);
    }


    private TabInfo addTab(SettingTab settingTab) {
        TabInfo tabInfo = new TabInfo(settingTab.createPanel());
        tabInfo.setText(settingTab.getTabName());
        this.tabs.addTab(tabInfo);
        return tabInfo;
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

package cn.fudoc.trade.view.settings.tab;


import cn.fudoc.trade.core.common.FuBundle;
import cn.fudoc.trade.core.state.FuStockSettingState;
import cn.fudoc.trade.util.FormPanelUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

public class TokenSettingsTab implements SettingTab{

    private static final String TOKEN_TITLE = FuBundle.message("stock.token.generate");

    private final JBTextField tokenField = new JBTextField();
    private final HyperlinkLabel tip1Label;
    public TokenSettingsTab(FuStockSettingState instance) {
        tokenField.setText(instance.getToken());
        this.tip1Label = createTipLabelStyle();
    }

    @Override
    public String getTabName() {
        return "Token设置";
    }

    @Override
    public JPanel createPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(JBUI.Borders.empty(20, 30));
        JPanel costPanel = FormPanelUtil.createRowPanel("Token：", tokenField, true);
        costPanel.add(Box.createHorizontalStrut(5));
        mainPanel.add(costPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(tip1Label);

        return mainPanel;
    }

    @Override
    public ValidationInfo doValidate() {
        return null;
    }

    @Override
    public void submit() {
        String tokenStr = tokenField.getText();
        if (StringUtils.isBlank(tokenStr)) {
            return;
        }
        FuStockSettingState instance = FuStockSettingState.getInstance();
        instance.setToken(tokenStr);
    }


    private HyperlinkLabel createTipLabelStyle() {
        HyperlinkLabel jLabel = new HyperlinkLabel(TOKEN_TITLE);
        jLabel.setHyperlinkTarget("https://www.zhituapi.com/get-free-cert.html");
        // IDEA 风格的蓝色
        jLabel.setForeground(new Color(60, 120, 216));
        jLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));

        return jLabel;
    }


}

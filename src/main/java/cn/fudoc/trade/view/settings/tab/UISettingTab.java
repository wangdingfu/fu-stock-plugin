package cn.fudoc.trade.view.settings.tab;

import cn.fudoc.trade.core.common.FuBundle;
import cn.fudoc.trade.core.common.enumtype.FuPosition;
import cn.fudoc.trade.core.exception.ValidException;
import cn.fudoc.trade.core.state.FuStockSettingState;
import cn.fudoc.trade.util.FormPanelUtil;
import cn.hutool.core.util.NumberUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

public class UISettingTab implements SettingTab {
    private final JBTextField titleFontField = new JBTextField();
    private final JBTextField tableFontField = new JBTextField();
    private final JBTextField tableTipFontField = new JBTextField();

    public UISettingTab() {
        FuStockSettingState instance = FuStockSettingState.getInstance();
        titleFontField.setText(instance.getFontSize(FuPosition.TABLE_TITLE) + "");
        tableFontField.setText(instance.getFontSize(FuPosition.TABLE_CONTENT) + "");
        tableTipFontField.setText(instance.getFontSize(FuPosition.TABLE_CONTENT_SMALL) + "");
    }


    @Override
    public String getTabName() {
        return "外观设置";
    }

    @Override
    public JPanel createPanel() {
        //持仓tab列表 下拉框
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(JBUI.Borders.empty(20, 30));
        FormPanelUtil.addRow(mainPanel, "表格标题字体", titleFontField);
        FormPanelUtil.addRow(mainPanel, "表格单元格字体", tableFontField);
        FormPanelUtil.addRow(mainPanel, "表格副文本字体", tableTipFontField);
        return null;
    }

    @Override
    public ValidationInfo doValidate() {
        try {
            validRateField("表格标题字体", titleFontField);
            validRateField("表格单元格字体", tableFontField);
            validRateField("表格副文本字体", tableTipFontField);
        } catch (ValidException e) {
            return e.getValidationInfo();
        } catch (Exception ignored) {
        }
        return null;
    }

    private void validRateField(String title, JBTextField field) {
        String rateStr = field.getText().trim();
        if (StringUtils.isNoneBlank(rateStr) && !NumberUtil.isDouble(rateStr)) {
            throw new ValidException(new ValidationInfo(FuBundle.message("stock.setting.rate.formatError", title), field));
        }
    }

    @Override
    public void submit() {
        FuStockSettingState instance = FuStockSettingState.getInstance();
        instance.addFont(FuPosition.TABLE_TITLE, convertFont(FuPosition.TABLE_TITLE, titleFontField.getText()));
        instance.addFont(FuPosition.TABLE_CONTENT, convertFont(FuPosition.TABLE_CONTENT, tableFontField.getText()));
        instance.addFont(FuPosition.TABLE_CONTENT_SMALL, convertFont(FuPosition.TABLE_CONTENT_SMALL, tableTipFontField.getText()));

    }


    private float convertFont(FuPosition fuPosition, String text) {
        if (StringUtils.isBlank(text) || !NumberUtil.isDouble(text)) {
            return fuPosition.getDefaultSize();
        }
        return NumberUtil.parseFloat(text);
    }

}

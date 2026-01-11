package cn.fudoc.trade.view.settings.tab;

import cn.fudoc.trade.core.common.FuBundle;
import cn.fudoc.trade.core.common.FuNotification;
import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.exception.ValidException;
import cn.fudoc.trade.core.state.FuStockSettingState;
import cn.fudoc.trade.core.state.StockGroupState;
import cn.fudoc.trade.core.state.pojo.TradeRateInfo;
import cn.fudoc.trade.util.FormPanelUtil;
import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Sets;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.OnOffButton;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Set;

public class RateSettingsTab implements SettingTab {

    private final ComboBox<String> holdingsGroupField;
    private final JBTextField minFeeField = new JBTextField();
    private final JBTextField commissionRateField = new JBTextField();
    private final JBTextField stampDutyRateField = new JBTextField();
    private final JBTextField transferSHRateField = new JBTextField();
    private final JBTextField transferSZRateField = new JBTextField();
    private final JBTextField otherRateField = new JBTextField();
    private final JBTextField otherFeeField = new JBTextField();


    public RateSettingsTab(String holdingsGroup) {
        Set<String> groups;
        if (StringUtils.isBlank(holdingsGroup)) {
            groups = StockGroupState.getInstance().holdingsGroups();
            groups.add(FuTradeConstants.MY_HOLD_GROUP);
        } else {
            groups = Sets.newHashSet(holdingsGroup);
        }
        FuStockSettingState instance = FuStockSettingState.getInstance();
        holdingsGroupField = new ComboBox<>(groups.toArray(new String[]{}));
        holdingsGroupField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initData(instance);
            }
        });
        initData(instance);
    }


    @Override
    public String getTabName() {
        return "交易费率";
    }

    @Override
    public JPanel createPanel() {
        //持仓tab列表 下拉框
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(JBUI.Borders.empty(20, 30));
        FormPanelUtil.addRow(mainPanel, "应用的持仓分组", holdingsGroupField);

        //0、起收
        FormPanelUtil.addRow(mainPanel, "起收", minFeeField);


        //1、券商佣金费率 默认0.0025
        FormPanelUtil.addRow(mainPanel, "券商佣金费率", commissionRateField);

        //2、印花税费率 默认0.0005
        FormPanelUtil.addRow(mainPanel, "印花税费率", stampDutyRateField);

        //3、过户费费率 默认0
        FormPanelUtil.addRow(mainPanel, "过户费费率(沪A)", transferSHRateField);
        FormPanelUtil.addRow(mainPanel, "过户费费率(深A)", transferSZRateField);

        //4、其他费率 默认0
        FormPanelUtil.addRow(mainPanel, "其他费率", otherRateField);

        //5、其他费用 默认0
        FormPanelUtil.addRow(mainPanel, "其他费用", otherFeeField);

        return mainPanel;
    }

    @Override
    public ValidationInfo doValidate() {
        try {
            validRateField("每笔最低收取", minFeeField);
            validRateField("券商佣金费率", commissionRateField);
            validRateField("印花税费率", stampDutyRateField);
            validRateField("过户费费率(沪A)", transferSHRateField);
            validRateField("过户费费率(深A)", transferSZRateField);
            validRateField("其他费率", otherRateField);
            validRateField("券商其他费用佣金费率", otherFeeField);
        } catch (ValidException e) {
            return e.getValidationInfo();
        } catch (Exception ignored) {
        }
        return null;
    }


    private void validRateField(String title, JBTextField field) {
        String rateStr = field.getText().trim();
        if (StringUtils.isNoneBlank(rateStr) && !NumberUtil.isDouble(rateStr) && !NumberUtil.isInteger(rateStr)) {
            throw new ValidException(new ValidationInfo(FuBundle.message("stock.setting.rate.formatError", title), field));
        }
    }

    @Override
    public void submit() {
        String selectedItem = (String) holdingsGroupField.getSelectedItem();
        if (StringUtils.isBlank(selectedItem)) {
            //提示未选中持仓分组
            FuNotification.notifyWarning("请先选择需要应用的持仓分组才能设置费率");
            return;
        }
        FuStockSettingState instance = FuStockSettingState.getInstance();
        TradeRateInfo rate = instance.getRate(selectedItem);
        if (Objects.isNull(rate)) {
            rate = new TradeRateInfo();
            instance.addRate(selectedItem, rate);
        }
        rate.setMinFee(minFeeField.getText().trim());
        rate.setCommissionRate(commissionRateField.getText().trim());
        rate.setStampDutyRate(stampDutyRateField.getText().trim());
        rate.setTransferSHRate(transferSHRateField.getText().trim());
        rate.setTransferSZRate(transferSZRateField.getText().trim());
        rate.setOtherRate(otherRateField.getText().trim());
        rate.setOtherFee(otherFeeField.getText().trim());
    }


    private void initData(FuStockSettingState instance) {
        String selectedItem = (String) holdingsGroupField.getSelectedItem();
        if (StringUtils.isBlank(selectedItem)) {
            selectedItem = FuTradeConstants.MY_HOLD_GROUP;
        }
        TradeRateInfo rate = instance.getRate(selectedItem);
        if (Objects.isNull(rate)) {
            rate = instance.createDefaultTradeRateInfo();
        }
        String minFee = rate.getMinFee();
        minFeeField.setText((StringUtils.isBlank(minFee) && rate.isMin5()) ? "5" : (StringUtils.isBlank(minFee) ? "5" : minFee));
        commissionRateField.setText(rate.getCommissionRate());
        stampDutyRateField.setText(rate.getStampDutyRate());
        String transferSHRate = rate.getTransferSHRate();
        String transferSZRate = rate.getTransferSZRate();
        transferSHRateField.setText(StringUtils.isBlank(transferSHRate) ? "0.00001" : transferSHRate);
        transferSZRateField.setText(StringUtils.isBlank(transferSZRate) ? "0.00001" : transferSZRate);
        otherRateField.setText(rate.getOtherRate());
        otherFeeField.setText(rate.getOtherFee());
    }
}

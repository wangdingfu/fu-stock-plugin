package cn.fudoc.trade.view.settings.tab;

import cn.fudoc.trade.core.common.FuNotification;
import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.state.FuStockSettingState;
import cn.fudoc.trade.core.state.StockGroupState;
import cn.fudoc.trade.core.state.pojo.TradeRateInfo;
import cn.fudoc.trade.util.FormPanelUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.OnOffButton;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.Objects;
import java.util.Set;

public class RateSettingsTab implements SettingTab {

    private final ComboBox<String> holdingsGroupField;
    private final OnOffButton onOffButton = new OnOffButton();
    private final JBTextField commissionRateField = new JBTextField();
    private final JBTextField stampDutyRateField = new JBTextField();
    private final JBTextField transferRateField = new JBTextField();
    private final JBTextField otherRateField = new JBTextField();
    private final JBTextField otherFeeField = new JBTextField();


    public RateSettingsTab() {
        Set<String> groups = StockGroupState.getInstance().holdingsGroups();
        groups.add(FuTradeConstants.MY_POSITIONS_GROUP);
        holdingsGroupField = new ComboBox<>(groups.toArray(new String[]{}));
        initData(FuStockSettingState.getInstance());
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

        //0、是否每笔最低5元 默认开启
        FormPanelUtil.addRow(mainPanel, "是否每笔最低5元", onOffButton, false);


        //1、券商佣金费率 默认0.0025
        FormPanelUtil.addRow(mainPanel, "券商佣金费率", commissionRateField);

        //2、印花税费率 默认0.0005
        FormPanelUtil.addRow(mainPanel, "印花税费率", stampDutyRateField);

        //3、过户费费率 默认0
        FormPanelUtil.addRow(mainPanel, "过户费费率", transferRateField);

        //4、其他费率 默认0
        FormPanelUtil.addRow(mainPanel, "其他费率", otherRateField);

        //5、其他费用 默认0
        FormPanelUtil.addRow(mainPanel, "其他费用", otherFeeField);

        return mainPanel;
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
        rate.setMin5(onOffButton.isSelected());
        rate.setCommissionRate(commissionRateField.getText().trim());
        rate.setStampDutyRate(stampDutyRateField.getText().trim());
        rate.setTransferRate(transferRateField.getText().trim());
        rate.setOtherRate(otherRateField.getText().trim());
        rate.setOtherFee(otherFeeField.getText().trim());
    }


    private void initData(FuStockSettingState instance) {
        holdingsGroupField.setSelectedItem(FuTradeConstants.MY_POSITIONS_GROUP);
        TradeRateInfo rate = instance.getRate(FuTradeConstants.MY_POSITIONS_GROUP);
        if (Objects.isNull(rate)) {
            rate = instance.createDefaultTradeRateInfo();
        }
        onOffButton.setSelected(rate.isMin5());
        commissionRateField.setText(rate.getCommissionRate());
        stampDutyRateField.setText(rate.getStampDutyRate());
        transferRateField.setText(rate.getTransferRate() );
        otherRateField.setText(rate.getOtherRate() );
        otherFeeField.setText(rate.getOtherFee() );
    }
}

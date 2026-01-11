package cn.fudoc.trade.view.holdings.tab;

import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.core.state.pojo.TradeRateInfo;
import cn.fudoc.trade.util.FormPanelUtil;
import cn.fudoc.trade.util.FuNumberUtil;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import cn.fudoc.trade.view.helper.CalculateCostHelper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.math.BigDecimal;

/**
 * 持仓卖出 tab
 */
public class HoldingsSellTabView extends AbstractHoldingsTabView {


    // 输入组件
    private final JBTextField priceField = new JBTextField();
    private final JBTextField countField = new JBTextField();

    public HoldingsSellTabView(StockInfoDTO stockInfoDTO, HoldingsInfo holdingsInfo) {
        super(stockInfoDTO, holdingsInfo);
    }

    @Override
    public String getTabName() {
        return FuTradeConstants.TabName.HOLDINGS_SELL_TAB;
    }

    @Override
    protected void createPanel(JPanel mainPanel) {
        FormPanelUtil.addRow(mainPanel, "卖出价：", priceField);
        FormPanelUtil.addRow(mainPanel, "卖出数量：", countField);
    }

    @Override
    public void submit(HoldingsInfo holdingsInfo) {
        //计算手续费
        TradeRateInfo rateInfo = getRateInfo();
        Integer count = FuNumberUtil.toInteger(countField.getText().trim());
        BigDecimal price = FuNumberUtil.toBigDecimal(priceField.getText().trim());
        BigDecimal handlingFee = CalculateCostHelper.calculateHandlingFee(2, rateInfo, price, count, stockInfoDTO.jys());
        holdingsInfo.add(2, count, price.toString(), handlingFee.toString());
    }

    @Override
    protected boolean isAddRateTip() {
        return true;
    }

    @Override
    public ValidationInfo doValidate() {
        validNumber("卖出价", priceField);
        validInteger("卖出数量", countField);
        return null;
    }


}

package cn.fudoc.trade.view.holdings.tab;

import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.listener.DocumentCallback;
import cn.fudoc.trade.core.listener.TextFieldDocumentListener;
import cn.fudoc.trade.core.state.FuStockSettingState;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.core.state.pojo.TradeRateInfo;
import cn.fudoc.trade.util.FormPanelUtil;
import cn.fudoc.trade.util.FuNumberUtil;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import cn.fudoc.trade.view.holdings.helper.CalculateCostHelper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.math.BigDecimal;

/**
 * 分红 tab
 */
public class HoldingsDividendTabView extends AbstractHoldingsTabView implements DocumentCallback {


    /**
     * 每股分红价格
     */
    private final JBTextField priceField = new JBTextField();
    /**
     * 参与分红数量
     */
    private final JBTextField countField = new JBTextField();
    /**
     * 本次分红总金额
     */
    private final JLabel amountField = new JLabel();

    public HoldingsDividendTabView(StockInfoDTO stockInfoDTO, HoldingsInfo holdingsInfo) {
        super(stockInfoDTO, holdingsInfo);
        // 给两个输入框绑定监听
        priceField.getDocument().addDocumentListener(new TextFieldDocumentListener(1, this));
        countField.getDocument().addDocumentListener(new TextFieldDocumentListener(2, this));
    }

    @Override
    public String getTabName() {
        return FuTradeConstants.TabName.HOLDINGS_DIVIDEND_TAB;
    }

    @Override
    protected void createPanel(JPanel mainPanel) {
        FormPanelUtil.addRow(mainPanel, "每股分红金额：", priceField);
        FormPanelUtil.addRow(mainPanel, "分红总股数：", countField);
        FormPanelUtil.addRow(mainPanel, "分红总金额：", amountField);
    }

    @Override
    public void submit(HoldingsInfo holdingsInfo) {
        //计算手续费
        TradeRateInfo rate = FuStockSettingState.getInstance().getRate(stockInfoDTO.group());
        Integer count = FuNumberUtil.toInteger(countField.getText().trim());
        BigDecimal price = FuNumberUtil.toBigDecimal(priceField.getText().trim());
        BigDecimal handlingFee = CalculateCostHelper.calculateHandlingFee(2, rate, price, count);
        holdingsInfo.add(3, count, price.toString(), handlingFee.toString());
    }

    @Override
    public ValidationInfo doValidate() {
        validNumber("每股分红金额", priceField);
        validInteger("分红总股数", countField);
        return null;
    }


    @Override
    public void callback(Integer type, DocumentEvent event) {
        BigDecimal price = FuNumberUtil.toBigDecimal(priceField.getText().trim());
        BigDecimal count = FuNumberUtil.toBigDecimal(countField.getText().trim());
        amountField.setText(price.multiply(count) + "");
    }
}

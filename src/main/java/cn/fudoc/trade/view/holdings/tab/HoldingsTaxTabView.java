package cn.fudoc.trade.view.holdings.tab;

import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.util.FormPanelUtil;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;

/**
 * 股息红利税补缴 tab
 */
public class HoldingsTaxTabView extends AbstractHoldingsTabView {


    /**
     * 本次分红总金额
     */
    private final JBTextField amountField = new JBTextField();

    public HoldingsTaxTabView(StockInfoDTO stockInfoDTO, HoldingsInfo holdingsInfo) {
        super(stockInfoDTO, holdingsInfo);
    }

    @Override
    public String getTabName() {
        return "股息红利税补缴";
    }

    @Override
    protected void createPanel(JPanel mainPanel) {
        FormPanelUtil.addRow(mainPanel, "补缴税额：", amountField);
    }

    @Override
    public void submit(HoldingsInfo holdingsInfo) {
        holdingsInfo.add(4, 0, "", amountField.getText().trim());
    }

    @Override
    public ValidationInfo doValidate() {
        return null;
    }


}

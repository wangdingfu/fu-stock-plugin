package cn.fudoc.trade.view.holdings.tab;

import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.util.FormPanelUtil;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.math.BigDecimal;

/**
 * 持仓买入 tab
 */
public class HoldingsBuyTabView extends AbstractHoldingsTabView {


    // 输入组件
    private final JBTextField priceField = new JBTextField();
    private final JBTextField countField = new JBTextField();

    public HoldingsBuyTabView(StockInfoDTO stockInfoDTO, HoldingsInfo holdingsInfo) {
        super(stockInfoDTO, holdingsInfo);
    }

    @Override
    public String getTabName() {
        return "买入";
    }



    @Override
    protected void createPanel(JPanel mainPanel) {
        FormPanelUtil.addRow(mainPanel,"买入价：", priceField);
        FormPanelUtil.addRow(mainPanel,"买入数量：", countField);
    }

    @Override
    public void submit(HoldingsInfo holdingsInfo) {
        holdingsInfo.add(1, Integer.parseInt(countField.getText()), priceField.getText());
    }

    @Override
    public ValidationInfo doValidate() {
        return null;
    }

}

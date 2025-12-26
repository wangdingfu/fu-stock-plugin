package cn.fudoc.trade.view.dialog.tab;

import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import java.math.BigDecimal;

/**
 * 持仓卖出 tab
 */
public class HoldingsSellTabView extends AbstractHoldingsTabView{


    // 输入组件
    private final JBTextField priceField = new JBTextField();
    private final JBTextField countField = new JBTextField();

    public HoldingsSellTabView(StockInfoDTO stockInfoDTO, HoldingsInfo holdingsInfo) {
        super(stockInfoDTO, holdingsInfo);
    }

    @Override
    public String getTabName() {
        return "卖出";
    }

    @Override
    protected JPanel createPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        // 成本价行
        JPanel costPanel = createRowPanel("卖出价：", priceField);
        mainPanel.add(costPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        // 持仓数量行
        JPanel countPanel = createRowPanel("卖出数量：", countField);
        mainPanel.add(countPanel);
        return mainPanel;
    }


    @Override
    public void submit(HoldingsInfo holdingsInfo) {
        holdingsInfo.add(2, Integer.parseInt(countField.getText()), new BigDecimal(priceField.getText()));
    }

    @Override
    public ValidationInfo doValidate() {
        return null;
    }



}

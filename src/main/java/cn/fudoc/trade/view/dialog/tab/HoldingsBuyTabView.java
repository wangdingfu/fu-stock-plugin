package cn.fudoc.trade.view.dialog.tab;

import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;

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
    protected JPanel createPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        // 成本价行
        JPanel costPanel = createRowPanel("买入价：", priceField);
        mainPanel.add(costPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        // 持仓数量行
        JPanel countPanel = createRowPanel("买入数量：", countField);
        mainPanel.add(countPanel);
        return mainPanel;
    }


    @Override
    public void submit(HoldingsInfo holdingsInfo) {

    }

    @Override
    public ValidationInfo doValidate() {
        return null;
    }

    @Override
    protected void initData(HoldingsInfo holdingsInfo) {

    }

}

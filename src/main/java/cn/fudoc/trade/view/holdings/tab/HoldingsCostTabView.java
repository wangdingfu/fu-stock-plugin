package cn.fudoc.trade.view.holdings.tab;

import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.listener.DocumentCallback;
import cn.fudoc.trade.core.listener.TextFieldDocumentListener;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.util.FormPanelUtil;
import cn.fudoc.trade.util.FuNumberUtil;
import cn.fudoc.trade.view.dto.HoldingsTodayInfo;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import cn.fudoc.trade.view.holdings.helper.CalculateCostHelper;
import cn.hutool.core.util.NumberUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.Objects;


/**
 * 维护持仓成本 tab
 */
public class HoldingsCostTabView extends AbstractHoldingsTabView implements DocumentCallback {


    // 输入组件
    private final JBTextField costField = new JBTextField();
    private final JBTextField countField = new JBTextField();

    private final JLabel actualCostLabel;
    private final JLabel actualCountLabel;
    private final JLabel tip1Label;
    private final JLabel tip2Label;

    public HoldingsCostTabView(StockInfoDTO stockInfoDTO, HoldingsInfo holdingsInfo) {
        super(stockInfoDTO, holdingsInfo);
        this.actualCostLabel = createTipLabelStyle();
        this.actualCountLabel = createTipLabelStyle();
        this.tip1Label = createTipLabelStyle();
        this.tip2Label = createTipLabelStyle();
        addTextFieldListeners();
        initData(holdingsInfo);
    }

    @Override
    public String getTabName() {
        return FuTradeConstants.TabName.HOLDINGS_COST_TAB;
    }

    @Override
    protected void initData(HoldingsInfo holdingsInfo) {
        if (Objects.isNull(holdingsInfo)) {
            return;
        }

        costField.setText(FuNumberUtil.formatCost(holdingsInfo.getCost()));
        Integer count = holdingsInfo.getCount();
        countField.setText(Objects.isNull(count) ? "" : count.toString());

        tip1Label.setText("💡 提示1：维护的成本价和持仓数量将被视为上一交易日结束后的持仓成本和数量");
        tip2Label.setText("💡 提示2：实际成本和数量会根据当前维护的成本和今日交易实时计算得出");
    }


    @Override
    protected void createPanel(JPanel mainPanel) {
        // 成本价行
        JPanel costPanel = FormPanelUtil.createRowPanel("持仓成本：", costField);
        costPanel.add(Box.createHorizontalStrut(5));
        costPanel.add(actualCostLabel);
        mainPanel.add(costPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        // 持仓数量行
        JPanel countPanel = FormPanelUtil.createRowPanel("持仓数量：", countField);
        countPanel.add(Box.createHorizontalStrut(5));
        countPanel.add(actualCountLabel);
        mainPanel.add(countPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        //提示信息
        mainPanel.add(this.tip1Label);
        mainPanel.add(this.tip2Label);
    }

    @Override
    public void submit(HoldingsInfo holdingsInfo) {
        holdingsInfo.setCost(costField.getText().trim());
        holdingsInfo.setCount(Integer.parseInt(countField.getText().trim()));
        holdingsInfo.add(0, Integer.parseInt(countField.getText()), costField.getText(), "0");
    }


    @Override
    public ValidationInfo doValidate() {
        validInteger("持仓数量", countField);
        validNumber("持仓成本", costField);
        return null;
    }


    /**
     * 为两个输入框添加文本变更监听
     */
    private void addTextFieldListeners() {
        // 给两个输入框绑定监听
        costField.getDocument().addDocumentListener(new TextFieldDocumentListener(1, this));
        countField.getDocument().addDocumentListener(new TextFieldDocumentListener(2, this));
    }


    public JLabel createTipLabelStyle() {
        JLabel jLabel = new JLabel();
        jLabel.setPreferredSize(new Dimension(250, 20));
        jLabel.setForeground(new Color(60, 120, 216)); // IDEA 风格的蓝色
        jLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        return jLabel;
    }


    private void updateTipInfo() {
        String countStr = countField.getText().trim();
        String costStr = costField.getText().trim();
        Integer count = NumberUtil.isInteger(countStr) ? Integer.parseInt(countStr) : 0;
        HoldingsTodayInfo calculate = CalculateCostHelper.calculate(costStr, count, holdingsInfo.getTradeList());
        actualCountLabel.setText("实际数量: " + calculate.getTotal());
        actualCostLabel.setText("实际成本: " + FuNumberUtil.formatCost(calculate.getCurrentCost()));
    }


    @Override
    public void callback(Integer type, DocumentEvent event) {
        updateTipInfo();
    }
}

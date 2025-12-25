package cn.fudoc.trade.view.dialog.tab;

import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.listener.DocumentCallback;
import cn.fudoc.trade.core.listener.TextFieldDocumentListener;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import cn.hutool.core.util.NumberUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextField;
import org.apache.commons.lang3.StringUtils;

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

    public HoldingsCostTabView(StockInfoDTO stockInfoDTO, HoldingsInfo holdingsInfo) {
        super(stockInfoDTO, holdingsInfo);
        this.actualCostLabel = createTipLabelStyle();
        this.actualCountLabel = createTipLabelStyle();
        addTextFieldListeners();
        initData(holdingsInfo);
    }

    @Override
    public String getTabName() {
        return "成本信息";
    }

    @Override
    protected void initData(HoldingsInfo holdingsInfo) {
        if (Objects.isNull(holdingsInfo)) {
            return;
        }
        costField.setText(holdingsInfo.getCost());
        Integer count = holdingsInfo.getCount();
        countField.setText(Objects.isNull(count) ? "" : count.toString());

    }

    @Override
    protected JPanel createPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        // 成本价行
        JPanel costPanel = createRowPanel("成本价：", costField);
        costPanel.add(Box.createHorizontalStrut(5));
        costPanel.add(actualCostLabel);
        mainPanel.add(costPanel);
//        mainPanel.add(Box.createVerticalStrut(5));
//        //提示信息
//        mainPanel.add(actualCostLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        // 持仓数量行
        JPanel countPanel = createRowPanel("持仓数量：", countField);
        countPanel.add(Box.createHorizontalStrut(5));
        countPanel.add(actualCountLabel);
        mainPanel.add(countPanel);
//        mainPanel.add(Box.createVerticalStrut(5));
//        mainPanel.add(actualCountLabel);
        return mainPanel;
    }



    @Override
    public void submit(HoldingsInfo holdingsInfo) {

    }

    @Override
    public ValidationInfo doValidate() {
        String costValue = costField.getText().trim();
        String countValue = countField.getText().trim();

        if (StringUtils.isBlank(costValue)) {
            return new ValidationInfo(FuTradeConstants.HOLD_COST_NOTNULL, costField);
        }
        if (StringUtils.isBlank(countValue)) {
            return new ValidationInfo(FuTradeConstants.HOLD_COUNT_NOTNULL, countField);
        }
        if (!NumberUtil.isNumber(costValue)) {
            return new ValidationInfo(FuTradeConstants.HOLD_COST_IS_NUMBER, costField);
        }
        if (!NumberUtil.isInteger(countValue)) {
            return new ValidationInfo(FuTradeConstants.HOLD_COUNT_IS_NUMBER, countField);
        }

        try {
            int count = Integer.parseInt(countValue);
            if (count <= 0) {
                return new ValidationInfo(FuTradeConstants.HOLD_COUNT_GT_ZERO, countField);
            }
        } catch (Exception e) {
            return new ValidationInfo(FuTradeConstants.HOLD_COUNT_FORMAT_ERROR, countField);
        }
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

    private void updateActualCostTipInfo() {
        String actualCost = costField.getText();
        String text = "实际成本: " + actualCost;
        actualCostLabel.setText(text);
    }

    private void updateActualCountTipInfo() {
        String actualCost = countField.getText();
        String text = "实际数量: " + actualCost;
        actualCountLabel.setText(text);
    }

    @Override
    public void callback(Integer type, DocumentEvent event) {
        if (type == 1) {
            updateActualCostTipInfo();
        } else {
            updateActualCountTipInfo();
        }
    }
}

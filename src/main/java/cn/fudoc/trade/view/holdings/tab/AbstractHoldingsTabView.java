package cn.fudoc.trade.view.holdings.tab;

import cn.fudoc.trade.core.common.FuBundle;
import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.exception.ValidException;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.util.FormPanelUtil;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import cn.hutool.core.util.NumberUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

/**
 * 维护持仓信息抽象类
 */
public abstract class AbstractHoldingsTabView implements HoldingsTabView {
    protected final JLabel stockCodeLabel;
    protected final JLabel stockNameLabel;
    protected final StockInfoDTO stockInfoDTO;
    protected final HoldingsInfo holdingsInfo;

    /**
     * 初始化持仓信息
     *
     * @param holdingsInfo 持仓持久化数据
     */
    protected void initData(HoldingsInfo holdingsInfo) {

    }

    /**
     * 创建展示面板
     */
    protected void createPanel(JPanel mainPanel) {
    }

    public AbstractHoldingsTabView(StockInfoDTO stockInfoDTO, HoldingsInfo holdingsInfo) {
        this.stockCodeLabel = new JBLabel(stockInfoDTO.stockCode());
        this.stockNameLabel = new JBLabel(stockInfoDTO.stockName());
        this.holdingsInfo = holdingsInfo;
        this.stockInfoDTO = stockInfoDTO;
    }


    @Override
    public JPanel getPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(JBUI.Borders.empty(20, 30));
        FormPanelUtil.addRow(mainPanel, "股票代码：", stockCodeLabel);
        FormPanelUtil.addRow(mainPanel, "股票名称：", stockNameLabel);
        createPanel(mainPanel);
        return mainPanel;
    }


    protected void validInteger(String title,JBTextField field) {
        validNumber(title, field, true);
    }

    protected void validNumber(String title,JBTextField field) {
        validNumber(title, field,false);
    }

    protected void validNumber(String title,JBTextField field, boolean isInteger) {
        String fieldValue = field.getText().trim();
        if (StringUtils.isBlank(fieldValue)) {
            throw new ValidException(new ValidationInfo("请输入" + title, field));
        }
        if (isInteger) {
            if (!NumberUtil.isInteger(fieldValue)) {
                throw new ValidException(new ValidationInfo(FuBundle.message(FuTradeConstants.STOCK_VALID_FORMAT_ERROR, title), field));
            }
            return;
        }
        if (!NumberUtil.isDouble(fieldValue) && !NumberUtil.isInteger(fieldValue)) {
            throw new ValidException(new ValidationInfo(FuBundle.message(FuTradeConstants.STOCK_VALID_FORMAT_ERROR, title), field));
        }

    }

}

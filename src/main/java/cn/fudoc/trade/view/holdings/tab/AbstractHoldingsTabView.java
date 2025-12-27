package cn.fudoc.trade.view.holdings.tab;

import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.util.FormPanelUtil;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;

/**
 * 维护持仓信息抽象类
 */
public abstract class AbstractHoldingsTabView implements HoldingsTabView {
    protected final JLabel stockCodeLabel;
    protected final JLabel stockNameLabel;
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
    }


    @Override
    public JPanel getPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(JBUI.Borders.empty(20, 30));
        FormPanelUtil.addRow(mainPanel,"股票代码：", stockCodeLabel);
        FormPanelUtil.addRow(mainPanel,"股票名称：", stockNameLabel);
        createPanel(mainPanel);
        return mainPanel;
    }
}

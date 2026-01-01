package cn.fudoc.trade.view.holdings.tab;

import cn.fudoc.trade.core.common.FuBundle;
import cn.fudoc.trade.core.common.FuNotification;
import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.exception.ValidException;
import cn.fudoc.trade.core.state.FuStockSettingState;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.core.state.pojo.TradeRateInfo;
import cn.fudoc.trade.util.FormPanelUtil;
import cn.fudoc.trade.util.ProjectUtils;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import cn.fudoc.trade.view.settings.FuStockSettingDialog;
import cn.hutool.core.util.NumberUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import icons.FuIcons;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.util.Objects;

/**
 * 维护持仓信息抽象类
 */
public abstract class AbstractHoldingsTabView implements HoldingsTabView {
    protected final JLabel stockCodeLabel;
    protected final JLabel stockNameLabel;
    protected final StockInfoDTO stockInfoDTO;
    protected final HoldingsInfo holdingsInfo;


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
        if (isAddRateTip()) {
            //是否需要添加费率
            addTipRate(mainPanel);
        }
        return mainPanel;
    }


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

    /**
     * 是否需要添加费率提示
     */
    protected boolean isAddRateTip() {
        return false;
    }

    /**
     * 判断是否需要添加设置费率提示
     */
    protected void addTipRate(JPanel mainPanel) {
        TradeRateInfo rate = FuStockSettingState.getInstance().getRate(stockInfoDTO.group());
        JLabel tipLabel;
        HyperlinkLabel linkLabel = new HyperlinkLabel(FuTradeConstants.LINK_RATE_LABEL);
        if (Objects.isNull(rate)) {
            linkLabel.setForeground(JBColor.RED);
            tipLabel = createTipLabelStyle(true);
            tipLabel.setText("提示：您还未设置交易费率，请先设置交易费率在进行买入");
        } else {
            tipLabel = new JLabel();
        }
        JPanel rowPanel = FormPanelUtil.createRowPanel(tipLabel);
        rowPanel.add(Box.createHorizontalStrut(5));
        linkLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        linkLabel.setToolTipText(FuTradeConstants.LINK_RATE_LABEL);
        linkLabel.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                openRateSettingDialog();
            }
        });
        rowPanel.add(linkLabel);
        mainPanel.add(rowPanel);
        mainPanel.add(Box.createVerticalStrut(15));
    }


    /**
     * 获取设置的手续费 费率信息
     */
    protected TradeRateInfo getRateInfo() {
        FuStockSettingState instance = FuStockSettingState.getInstance();
        TradeRateInfo rate = instance.getRate(stockInfoDTO.group());
        if (Objects.isNull(rate)) {
            openRateSettingDialog();
            rate = instance.getRate(stockInfoDTO.group());
        }
        if (Objects.isNull(rate)) {
            rate = instance.createDefaultTradeRateInfo();
            FuNotification.notifyWarning("设置费率异常，将使用默认费率为您计算手续费");
        }
        return rate;
    }


    private void openRateSettingDialog() {
        new FuStockSettingDialog(ProjectUtils.getCurrProject(), stockInfoDTO.group()).showAndGet();
    }


    protected JLabel createTipLabelStyle(boolean isIcon) {
        return createTipLabelStyle(isIcon, new Color(60, 120, 216));
    }


    protected JLabel createTipLabelStyle(boolean isIcon, Color color) {
        JLabel jLabel = new JLabel();
        // IDEA 风格的蓝色
        jLabel.setForeground(color);
        jLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        if (isIcon) {
            jLabel.setIcon(FuIcons.FU_TIP);
        }
        return jLabel;
    }


    protected void validInteger(String title, JBTextField field) {
        validNumber(title, field, true);
    }

    protected void validNumber(String title, JBTextField field) {
        validNumber(title, field, false);
    }

    protected void validNumber(String title, JBTextField field, boolean isInteger) {
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

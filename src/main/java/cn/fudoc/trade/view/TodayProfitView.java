package cn.fudoc.trade.view;

import cn.fudoc.trade.view.dto.HoldStockDataDto;
import cn.fudoc.trade.view.render.ProfitRenderer;
import cn.fudoc.trade.view.render.RankListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TodayProfitView {
    /**
     * 今日收益
     */
    private final ProfitRenderer todayProfitPanel = new ProfitRenderer("今日");
    /**
     * 持仓收益
     */
    private final ProfitRenderer todayHoldProfitPanel = new ProfitRenderer("持仓收益");
    /**
     * 总市值
     */
    private final ProfitRenderer companyValuePanel = new ProfitRenderer("总市值");


    private final JBList<HoldStockDataDto> rankList;
    private final DefaultListModel<HoldStockDataDto> rankListModel;

    @Getter
    public final JPanel rootPanel;

    public TodayProfitView() {
        this.rootPanel = new JPanel(new BorderLayout());
        JPanel indexPanel = new JPanel();
        indexPanel.setLayout(new BoxLayout(indexPanel, BoxLayout.X_AXIS));
        indexPanel.add(Box.createHorizontalGlue());
        indexPanel.add(todayProfitPanel);
        indexPanel.add(Box.createHorizontalGlue());
        indexPanel.add(todayHoldProfitPanel);
        indexPanel.add(Box.createHorizontalGlue());
        indexPanel.add(companyValuePanel);
        indexPanel.add(Box.createHorizontalGlue());
        indexPanel.setPreferredSize(new Dimension(rootPanel.getWidth(), 50));
        indexPanel.setSize(new Dimension(rootPanel.getWidth(), 50));
        this.rootPanel.add(indexPanel, BorderLayout.NORTH);

        JPanel rankPanel = new JPanel(new BorderLayout());
        this.rankListModel = new DefaultListModel<>();
        this.rankList = new JBList<>(this.rankListModel);
        this.rankList.setFixedCellHeight(40);
        this.rankList.setFont(this.rankList.getFont().deriveFont(15.0f));
        this.rankList.setCellRenderer(new RankListCellRenderer());
        rankPanel.add(createProfitPanel(), BorderLayout.NORTH);
        rankPanel.add(new JBScrollPane(this.rankList), BorderLayout.CENTER);
        this.rootPanel.add(rankPanel, BorderLayout.CENTER);
    }

    private JPanel createProfitPanel() {
        JLabel rankLabel = new JLabel("今日收益贡献度排名");
        JLabel profitLabel = new JLabel("今日收益");
        rankLabel.setForeground(JBColor.GRAY);
        rankLabel.setFont(rankLabel.getFont().deriveFont(13.0f));
        profitLabel.setForeground(JBColor.GRAY);
        profitLabel.setFont(rankLabel.getFont().deriveFont(13.0f));
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(rankLabel);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(profitLabel);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.setPreferredSize(new Dimension(titlePanel.getWidth(), 50));
        titlePanel.setSize(new Dimension(titlePanel.getWidth(), 50));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        return titlePanel;
    }


    public void setTodayProfit(String todayProfit) {
        todayProfitPanel.setValue(todayProfit, true);
    }

    public void setHoldProfit(String holdProfit) {
        todayHoldProfitPanel.setValue(holdProfit, true);
    }

    public void setCompanyValue(String companyValue) {
        companyValuePanel.setValue(companyValue, false);
    }


    public void initData(List<HoldStockDataDto> dataList) {
        this.rankListModel.clear();
        if (CollectionUtils.isNotEmpty(dataList)) this.rankListModel.addAll(dataList);
    }
}

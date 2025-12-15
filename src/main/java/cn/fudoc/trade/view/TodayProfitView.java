package cn.fudoc.trade.view;

import cn.fudoc.trade.view.dto.HoldStockDataDto;
import cn.fudoc.trade.view.render.ProfitRenderer;
import cn.fudoc.trade.view.render.RankListCellRenderer;
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
        this.rankList.setCellRenderer(new RankListCellRenderer());
        rankPanel.add(new JBScrollPane(this.rankList), BorderLayout.CENTER);
        this.rootPanel.add(rankPanel, BorderLayout.CENTER);
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



    public void initData(List<HoldStockDataDto> dataList){
        this.rankListModel.clear();
        if(CollectionUtils.isNotEmpty(dataList)) this.rankListModel.addAll(dataList);
    }
}

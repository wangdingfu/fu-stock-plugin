package cn.fudoc.trade.view.table;

import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.core.common.enumtype.CNMappingGroupEnum;
import cn.fudoc.trade.core.common.enumtype.GroupTypeEnum;
import cn.fudoc.trade.core.state.HoldingsStockState;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.core.state.pojo.StockGroupInfo;
import cn.fudoc.trade.util.FuNumberUtil;
import cn.fudoc.trade.util.PinyinUtil;
import cn.fudoc.trade.view.dto.HoldingsTodayInfo;
import cn.fudoc.trade.view.helper.CalculateCostHelper;
import cn.fudoc.trade.view.helper.HideTextHelper;
import cn.fudoc.trade.view.render.StockColorTableCellRenderer;
import com.google.common.collect.Lists;
import icons.FuIcons;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Vector;

/**
 * 持仓 tab
 */
public class HoldStockGroupHideTableView extends AbstractHoldingsTable {


    private static final String[] columnNames = {"Code", "Name", "Price", "Today Profit", "Total Profit"};
    private final HoldingsStockState state;

    public HoldStockGroupHideTableView(StockGroupInfo stockGroupInfo) {
        super(stockGroupInfo);
        addListener();
        this.state = HoldingsStockState.getInstance();
        init(this.state.getStockCodes(groupName()));
        stockTable.setRowSorter(getSorter(Lists.newArrayList(1, 2, 3)));
        stockTable.setDefaultRenderer(Object.class, new StockColorTableCellRenderer(Lists.newArrayList()));
        TableColumn idColumn = stockTable.getColumnModel().getColumn(0);
        // 从视图中移除，模型仍保留
        stockTable.getColumnModel().removeColumn(idColumn);

    }


    @Override
    public JPanel getComponent() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        JPanel tableComponent = tableHelper.createTablePanel();
        tableComponent.add(createTableHintLabel(), BorderLayout.NORTH);
        tipLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rootPanel.add(tableComponent, BorderLayout.CENTER);
        rootPanel.add(tipLabel, BorderLayout.PAGE_END);
        return rootPanel;
    }

    /**
     * 创建表格上方的引导文字标签
     */
    public static JLabel createTableHintLabel() {
        JLabel hintLabel = new JLabel("Tips：Double-click any cell to edit the holding cost.");
        hintLabel.setIcon(FuIcons.FU_TIP);
        // IDEA 风格的蓝色
        hintLabel.setForeground(new Color(60, 120, 216));
        hintLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        return hintLabel;
    }


    @Override
    protected boolean isHide() {
        return true;
    }

    @Override
    public GroupTypeEnum getTabEnum() {
        return GroupTypeEnum.STOCK_HOLD;
    }


    @Override
    protected String[] getColumnNames() {
        return columnNames;
    }

    @Override
    protected void removeStockFromState(String stockCode) {
        state.remove(groupName(), stockCode);
    }


    @Override
    protected Vector<Object> toTableData(RealStockInfo realStockInfo) {
        Vector<Object> vector = new Vector<>();
        HoldingsInfo holdingsInfo = state.getHoldingsInfo(groupName(), realStockInfo.getStockCode());
        BigDecimal cost,
                PL = BigDecimal.ZERO,
                todayProfit = BigDecimal.ZERO;
        int total;
        if (Objects.nonNull(holdingsInfo)) {
            HoldingsTodayInfo holdingsTodayInfo = CalculateCostHelper.calculate(holdingsInfo);
            //持仓成本价
            cost = holdingsTodayInfo.getCurrentCost();
            //持仓数量
            total = holdingsTodayInfo.getTotal();
            BigDecimal currentPrice = new BigDecimal(realStockInfo.getCurrentPrice());
            BigDecimal totalDecimal = new BigDecimal(total);
            //盈亏=持仓*(当前价-成本价)
            PL = currentPrice.subtract(cost).multiply(totalDecimal).setScale(4, RoundingMode.CEILING);
            BigDecimal yesterdayPrice = FuNumberUtil.toBigDecimal(realStockInfo.getYesterdayPrice());
            todayProfit = CalculateCostHelper.calculateProfit(currentPrice, yesterdayPrice, holdingsInfo);
        }
        vector.add(realStockInfo.getStockCode());
        //名称
        vector.add(HideTextHelper.mapping(realStockInfo.getStockName(), CNMappingGroupEnum.STOCK_NAME));
        //现价
        vector.add(realStockInfo.getCurrentPrice());
        String todayProfitPrefix = todayProfit.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        String PLPrefix = PL.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        //今日收益
        vector.add(todayProfitPrefix + FuNumberUtil.format(todayProfit));
        //持仓收益
        vector.add(PLPrefix + FuNumberUtil.format(PL));
        return vector;
    }


}

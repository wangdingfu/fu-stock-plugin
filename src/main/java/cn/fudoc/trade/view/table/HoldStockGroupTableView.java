package cn.fudoc.trade.view.table;

import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.core.common.enumtype.GroupTypeEnum;
import cn.fudoc.trade.core.state.HoldingsStockState;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.core.state.pojo.StockGroupInfo;
import cn.fudoc.trade.util.FuNumberUtil;
import cn.fudoc.trade.view.TodayProfitView;
import cn.fudoc.trade.view.dto.HoldStockDataDto;
import cn.fudoc.trade.view.dto.HoldingsTodayInfo;
import cn.fudoc.trade.view.holdings.helper.CalculateCostHelper;
import cn.fudoc.trade.view.render.MultiLineTableCellRenderer;
import com.google.common.collect.Lists;
import com.intellij.openapi.ui.Splitter;
import icons.FuIcons;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;

/**
 * 持仓 tab
 */
public class HoldStockGroupTableView extends AbstractHoldingsTable {

    private static final String[] columnNames = {"代码", "名称 / 市值", "持仓盈亏", "持仓 / 可用", "现价 / 成本", "今日收益",};
    private final HoldingsStockState state;


    private final TodayProfitView todayProfitView = new TodayProfitView();


    public HoldStockGroupTableView(StockGroupInfo stockGroupInfo) {
        super(stockGroupInfo);
        addListener();
        int rowHeight = stockTable.getRowHeight();
        stockTable.setRowHeight(rowHeight * 2 + 10);
        MultiLineTableCellRenderer cellRenderer = new MultiLineTableCellRenderer(Lists.newArrayList(1, 4), Lists.newArrayList(1, 2, 3, 4));
        cellRenderer.setAlignmentX(SwingConstants.CENTER);
        cellRenderer.setAlignmentY(SwingConstants.CENTER);
        for (String columnName : getColumnNames()) {
            stockTable.getColumn(columnName).setCellRenderer(cellRenderer);
        }
        TableColumn idColumn = stockTable.getColumnModel().getColumn(0);
        // 从视图中移除，模型仍保留
        stockTable.getColumnModel().removeColumn(idColumn);
        this.state = HoldingsStockState.getInstance();
        init(this.state.getStockCodes(groupName()));
        stockTable.setRowSorter(getHoldingsTableRowSorter());
    }


    @Override
    public JPanel getComponent() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        JPanel tableComponent = tableHelper.createTablePanel();
        tableComponent.add(createTableHintLabel(), BorderLayout.NORTH);
        Splitter splitter = new Splitter(true);
        splitter.setFirstComponent(tableComponent);

        splitter.setSecondComponent(todayProfitView.getRootPanel());
        rootPanel.add(splitter, BorderLayout.CENTER);

        tipLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rootPanel.add(tipLabel, BorderLayout.PAGE_END);
        return rootPanel;
    }

    /**
     * 创建表格上方的引导文字标签
     */
    public static JLabel createTableHintLabel() {
        JLabel hintLabel = new JLabel("提示：双击单元格可编辑持仓成本");
        hintLabel.setIcon(FuIcons.FU_TIP);
        hintLabel.setForeground(new Color(60, 120, 216)); // IDEA 风格的蓝色
        hintLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        return hintLabel;
    }

    @Override
    protected void tableDataChanged() {
        List<HoldStockDataDto> tableDataList = getTableDataList();
        //计算今日收益
        todayProfitView.setTodayProfit(FuNumberUtil.format(tableDataList.stream().map(HoldStockDataDto::getTodayProfit).reduce(BigDecimal.ZERO, BigDecimal::add)));

        //计算持仓收益
        todayProfitView.setHoldProfit(FuNumberUtil.format(tableDataList.stream().map(HoldStockDataDto::getAllProfit).reduce(BigDecimal.ZERO, BigDecimal::add)));

        //计算总市值
        todayProfitView.setCompanyValue(FuNumberUtil.format(tableDataList.stream().map(HoldStockDataDto::getCompanyValue).reduce(BigDecimal.ZERO, BigDecimal::add)));

        tableDataList.sort(Comparator.comparing(HoldStockDataDto::getTodayProfit).reversed());
        todayProfitView.initData(tableDataList);
    }

    @Override
    protected boolean isHide() {
        return false;
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
        BigDecimal cost = BigDecimal.ZERO,
                companyValue = BigDecimal.ZERO,
                PL = BigDecimal.ZERO,
                PLRate = BigDecimal.ZERO,
                todayProfit = BigDecimal.ZERO,
                increaseRate = BigDecimal.ZERO;
        int count = 0, total = 0;
        if (Objects.nonNull(holdingsInfo)) {
            HoldingsTodayInfo holdingsTodayInfo = CalculateCostHelper.calculate(holdingsInfo);
            //持仓成本价
            cost = holdingsTodayInfo.getCurrentCost();
            //持仓数量
            total = holdingsTodayInfo.getTotal();
            //可用数量
            count = holdingsTodayInfo.getCount();
            BigDecimal currentPrice = new BigDecimal(realStockInfo.getCurrentPrice());
            BigDecimal totalDecimal = new BigDecimal(total);
            //市值=持仓*当前价
            companyValue = totalDecimal.multiply(currentPrice).setScale(4, RoundingMode.CEILING);
            //盈亏=持仓*(当前价-成本价)
            PL = currentPrice.subtract(cost).multiply(totalDecimal).setScale(4, RoundingMode.CEILING);
            //盈亏比=(成本价-当前价)/成本价
            PLRate = cost.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : currentPrice.subtract(cost).divide(cost, 4, RoundingMode.CEILING);

            //今日收益计算
            increaseRate = new BigDecimal(realStockInfo.getIncreaseRate());
            BigDecimal yesterdayPrice = FuNumberUtil.toBigDecimal(realStockInfo.getYesterdayPrice());
            todayProfit = CalculateCostHelper.calculateProfit(currentPrice, yesterdayPrice, holdingsInfo);
        }

        //股票代码
        vector.add(realStockInfo.getStockCode());
        //名称/市值
        vector.add(new String[]{realStockInfo.getStockName(), FuNumberUtil.format(companyValue)});
        //持仓盈亏
        String PLRatePrefix = PLRate.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        String PLPrefix = PL.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        vector.add(new String[]{PLPrefix + FuNumberUtil.format(PL), PLRatePrefix + FuNumberUtil.formatRate(PLRate, false)});
        //持仓数量
        vector.add(new String[]{total + "", count + ""});
        //现价/成本
        vector.add(new String[]{realStockInfo.getCurrentPrice(), cost.setScale(3, RoundingMode.CEILING).toString()});
        //今日收益
        String todayProfitPrefix = todayProfit.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        String profitRatePrefix = increaseRate.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        vector.add(new String[]{todayProfitPrefix + FuNumberUtil.format(todayProfit), profitRatePrefix + FuNumberUtil.formatRate(increaseRate, true)});
        return vector;
    }


    private List<HoldStockDataDto> getTableDataList() {
        List<HoldStockDataDto> dataList = new ArrayList<>();
        Vector<Vector> dataVector = tableModel.getDataVector();
        dataVector.forEach(vector -> {
            Object first = vector.getFirst();
            if (Objects.isNull(first)) {
                return;
            }
            HoldStockDataDto holdStockDataDto = new HoldStockDataDto();
            holdStockDataDto.setStockCode(first.toString());
            holdStockDataDto.setStockName(convertValue(vector.get(1), 0));
            holdStockDataDto.setCompanyValue(FuNumberUtil.toBigDecimal(convertValue(vector.get(1), 1)));
            holdStockDataDto.setAllProfit(FuNumberUtil.toBigDecimal(convertValue(vector.get(2), 0)));
            holdStockDataDto.setTodayProfit(FuNumberUtil.toBigDecimal(convertValue(vector.get(5), 0)));
            dataList.add(holdStockDataDto);
        });
        return dataList;
    }
}

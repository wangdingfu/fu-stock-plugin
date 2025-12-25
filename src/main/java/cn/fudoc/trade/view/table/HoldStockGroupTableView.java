package cn.fudoc.trade.view.table;

import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.core.common.enumtype.StockTabEnum;
import cn.fudoc.trade.core.state.HoldingsStockState;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.util.NumberFormatUtil;
import cn.fudoc.trade.util.ProjectUtils;
import cn.fudoc.trade.view.TodayProfitView;
import cn.fudoc.trade.view.dialog.HoldingsStockDialog;
import cn.fudoc.trade.view.dto.HoldStockDataDto;
import cn.fudoc.trade.view.render.MultiLineTableCellRenderer;
import com.google.common.collect.Lists;
import com.intellij.openapi.ui.Splitter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;

/**
 * 持仓 tab
 */
public class HoldStockGroupTableView extends AbstractStockTableView {

    private final String tabName;
    private static final String[] columnNames = {"代码", "名称 / 市值", "持仓盈亏", "持仓数量", "现价 / 成本", "今日收益",};
    private final HoldingsStockState state;


    private final TodayProfitView todayProfitView = new TodayProfitView();


    public HoldStockGroupTableView(String tabName) {
        this.tabName = tabName;
        addListener();
        int rowHeight = stockTable.getRowHeight();
        stockTable.setRowHeight(rowHeight * 2 + 20);
        for (String columnName : getColumnNames()) {
            stockTable.getColumn(columnName).setCellRenderer(new MultiLineTableCellRenderer(Lists.newArrayList(1, 4), Lists.newArrayList(1, 3, 4)));
        }
        TableColumn idColumn = stockTable.getColumnModel().getColumn(0);
        // 从视图中移除，模型仍保留
        stockTable.getColumnModel().removeColumn(idColumn);
        this.state = HoldingsStockState.getInstance();
        init(this.state.getStockCodes(tabName));
        stockTable.setRowSorter(getDefaultTableModelTableRowSorter());
    }

    private @NotNull TableRowSorter<DefaultTableModel> getDefaultTableModelTableRowSorter() {
        TableRowSorter<DefaultTableModel> tableRowSorter = new TableRowSorter<>(tableModel);
        tableRowSorter.setComparator(1, (o1, o2) -> {
            String value1 = convertValue(o1, 1);
            String value2 = convertValue(o2, 1);
            return NumberFormatUtil.convertBigDecimal(value1).compareTo(NumberFormatUtil.convertBigDecimal(value2));
        });
        tableRowSorter.setComparator(2, (o1, o2) -> {
            String value1 = convertValue(o1, 0);
            String value2 = convertValue(o2, 0);
            return NumberFormatUtil.convertBigDecimal(value1).compareTo(NumberFormatUtil.convertBigDecimal(value2));
        });
        tableRowSorter.setComparator(3, Comparator.comparing(o -> NumberFormatUtil.convertBigDecimal(o.toString())));
        tableRowSorter.setComparator(5, (o1, o2) -> {
            String value1 = convertValue(o1, 0);
            String value2 = convertValue(o2, 0);
            return NumberFormatUtil.convertBigDecimal(value1).compareTo(NumberFormatUtil.convertBigDecimal(value2));
        });
        return tableRowSorter;
    }


    @Override
    public JPanel getComponent() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        JPanel tableComponent = getTableComponent();
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
        JLabel hintLabel = new JLabel("💡 提示：双击单元格可编辑持仓成本");
        hintLabel.setForeground(new Color(60, 120, 216)); // IDEA 风格的蓝色
        hintLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        return hintLabel;
    }

    @Override
    protected void tableDataChanged() {
        List<HoldStockDataDto> tableDataList = getTableDataList();
        //计算今日收益
        todayProfitView.setTodayProfit(NumberFormatUtil.format(tableDataList.stream().map(HoldStockDataDto::getTodayProfit).reduce(BigDecimal.ZERO, BigDecimal::add)));

        //计算持仓收益
        todayProfitView.setHoldProfit(NumberFormatUtil.format(tableDataList.stream().map(HoldStockDataDto::getAllProfit).reduce(BigDecimal.ZERO, BigDecimal::add)));

        //计算总市值
        todayProfitView.setCompanyValue(NumberFormatUtil.format(tableDataList.stream().map(HoldStockDataDto::getCompanyValue).reduce(BigDecimal.ZERO, BigDecimal::add)));

        tableDataList.sort(Comparator.comparing(HoldStockDataDto::getTodayProfit).reversed());
        todayProfitView.initData(tableDataList);
    }

    @Override
    public String getTabName() {
        return tabName;
    }

    @Override
    public StockTabEnum getTabEnum() {
        return StockTabEnum.STOCK_HOLD;
    }


    @Override
    protected String[] getColumnNames() {
        return columnNames;
    }

    @Override
    protected void removeStockFromState(String stockCode) {
        state.remove(tabName, stockCode);
    }

    @Override
    protected Vector<Object> toTableData(RealStockInfo realStockInfo) {
        Vector<Object> vector = new Vector<>();
        HoldingsInfo holdingsInfo = state.getHoldingsInfo(tabName, realStockInfo.getStockCode());
        BigDecimal cost = BigDecimal.ZERO,
                companyValue = BigDecimal.ZERO,
                PL = BigDecimal.ZERO,
                PLRate = BigDecimal.ZERO,
                todayProfit = BigDecimal.ZERO,
                increaseRate = BigDecimal.ZERO;
        int count = 0;
        if (Objects.nonNull(holdingsInfo)) {
            //持仓成本价
            cost = new BigDecimal(holdingsInfo.getCost());
            //持仓数量
            count = holdingsInfo.getCount();
            BigDecimal currentPrice = new BigDecimal(realStockInfo.getCurrentPrice());
            BigDecimal countDecimal = new BigDecimal(count);
            //市值=持仓*当前价
            companyValue = countDecimal.multiply(currentPrice).setScale(4, RoundingMode.CEILING);
            //盈亏=持仓*(当前价-成本价)
            PL = currentPrice.subtract(cost).multiply(countDecimal).setScale(4, RoundingMode.CEILING);
            //盈亏比=(成本价-当前价)/成本价
            PLRate = cost.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : currentPrice.subtract(cost).divide(cost, 4, RoundingMode.CEILING);

            //今日收益计算 （当前价-上一日收盘价）*持仓数量
            BigDecimal yesterdayPrice = new BigDecimal(realStockInfo.getYesterdayPrice());
            increaseRate = new BigDecimal(realStockInfo.getIncreaseRate());
            todayProfit = currentPrice.subtract(yesterdayPrice).multiply(countDecimal).setScale(4, RoundingMode.CEILING);
        }

        //股票代码
        vector.add(realStockInfo.getStockCode());
        //名称/市值
        vector.add(new String[]{realStockInfo.getStockName(), NumberFormatUtil.format(companyValue)});
        //持仓盈亏
        String PLRatePrefix = PLRate.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        String PLPrefix = PL.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        vector.add(new String[]{PLPrefix + NumberFormatUtil.format(PL), PLRatePrefix + NumberFormatUtil.formatRate(PLRate, false)});
        //持仓数量
        vector.add(count);
        //现价/成本
        vector.add(new String[]{realStockInfo.getCurrentPrice(), cost.setScale(3, RoundingMode.CEILING).toString()});
        //今日收益
        String todayProfitPrefix = todayProfit.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        String profitRatePrefix = increaseRate.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        vector.add(new String[]{todayProfitPrefix + NumberFormatUtil.format(todayProfit), profitRatePrefix + NumberFormatUtil.formatRate(increaseRate, true)});
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
            holdStockDataDto.setCompanyValue(NumberFormatUtil.convertBigDecimal(convertValue(vector.get(1), 1)));
            holdStockDataDto.setAllProfit(NumberFormatUtil.convertBigDecimal(convertValue(vector.get(2), 0)));
            holdStockDataDto.setTodayProfit(NumberFormatUtil.convertBigDecimal(convertValue(vector.get(5), 0)));
            dataList.add(holdStockDataDto);
        });
        return dataList;
    }


    private String convertValue(Object value, int index) {
        if (Objects.isNull(value)) {
            return "";
        }
        if (value instanceof String[] content && content.length > index) {
            return content[index];
        }
        return "";
    }


    private void addListener() {
        super.stockTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int selectedRow = stockTable.getSelectedRow();
                    int modelRow = stockTable.convertRowIndexToModel(selectedRow);
                    Object valueAt = tableModel.getValueAt(modelRow, 0);
                    Object valueAt1 = tableModel.getValueAt(modelRow, 1);
                    String code = Objects.isNull(valueAt) ? "" : valueAt.toString();
                    String name = (valueAt1 instanceof String[] values && values.length > 0) ? values[0] : "";
                    HoldingsStockDialog holdingsStockDialog = new HoldingsStockDialog(ProjectUtils.getCurrProject(), tabName, code, name);
                    if (holdingsStockDialog.showAndGet()) {
                        reloadAllStock();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
}

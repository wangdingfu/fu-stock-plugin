package cn.fudoc.trade.view.table;

import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.core.common.enumtype.StockTabEnum;
import cn.fudoc.trade.core.state.HoldingsStockState;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.util.NumberFormatUtil;
import cn.fudoc.trade.util.ProjectUtils;
import cn.fudoc.trade.view.dialog.HoldingsStockDialog;
import cn.fudoc.trade.view.dto.HoldStockDataDto;
import cn.fudoc.trade.view.render.MultiLineTableCellRenderer;
import cn.fudoc.trade.view.render.ProfitRenderer;
import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import com.intellij.openapi.ui.Splitter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/**
 * 持仓tab
 */
public class HoldStockGroupTableView extends AbstractStockTableView {

    private final String tabName;
    private static final String[] columnNames = {"代码", "名称 / 市值", "持仓盈亏", "持仓数量", "现价 / 成本", "今日收益",};
    private final HoldingsStockState state;

    /**
     * 今日收益
     */
    private final ProfitRenderer todayProfit = new ProfitRenderer("今日");
    /**
     * 持仓收益
     */
    private final ProfitRenderer todayHoldProfit = new ProfitRenderer("持仓收益");
    /**
     * 总市值
     */
    private final ProfitRenderer todayAllMoney = new ProfitRenderer("总市值");

    public HoldStockGroupTableView(String tabName) {
        this.tabName = tabName;
        addListener();
        int rowHeight = stockTable.getRowHeight();
        stockTable.setRowHeight(rowHeight * 2);
        for (String columnName : getColumnNames()) {
            stockTable.getColumn(columnName).setCellRenderer(new MultiLineTableCellRenderer(Lists.newArrayList(1, 4)));
        }
        TableColumn idColumn = stockTable.getColumnModel().getColumn(0);
        // 从视图中移除，模型仍保留
        stockTable.getColumnModel().removeColumn(idColumn);
        this.state = HoldingsStockState.getInstance();
        init(this.state.getStockCodes(tabName));
    }


    @Override
    public JPanel getComponent() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        JPanel tableComponent = getTableComponent();
        Splitter splitter = new Splitter(true);
        splitter.setFirstComponent(tableComponent);
        //今日收益
        JPanel profitPanel = new JPanel(new BorderLayout());
        JPanel indexPanel = new JPanel();
        indexPanel.setLayout(new BoxLayout(indexPanel, BoxLayout.X_AXIS));
        indexPanel.add(Box.createHorizontalGlue());
        indexPanel.add(todayProfit);
        indexPanel.add(Box.createHorizontalGlue());
        indexPanel.add(todayHoldProfit);
        indexPanel.add(Box.createHorizontalGlue());
        indexPanel.add(todayAllMoney);
        indexPanel.add(Box.createHorizontalGlue());
        indexPanel.setPreferredSize(new Dimension(rootPanel.getWidth(), 50));
        indexPanel.setSize(new Dimension(rootPanel.getWidth(), 50));
        profitPanel.add(indexPanel, BorderLayout.NORTH);
        splitter.setSecondComponent(profitPanel);
        rootPanel.add(splitter, BorderLayout.CENTER);

        tipLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rootPanel.add(tipLabel, BorderLayout.PAGE_END);
        return rootPanel;
    }


    @Override
    protected void tableDataChanged() {
        List<HoldStockDataDto> tableDataList = getTableDataList();
        //计算今日收益
        todayProfit.setValue(NumberFormatUtil.format(tableDataList.stream().map(HoldStockDataDto::getTodayProfit).reduce(BigDecimal.ZERO, BigDecimal::add)), true);

        //计算持仓收益
        todayHoldProfit.setValue(NumberFormatUtil.format(tableDataList.stream().map(HoldStockDataDto::getAllProfit).reduce(BigDecimal.ZERO, BigDecimal::add)), true);

        //计算总市值
        todayAllMoney.setValue(NumberFormatUtil.format(tableDataList.stream().map(HoldStockDataDto::getCompanyValue).reduce(BigDecimal.ZERO, BigDecimal::add)), false);
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
        //持仓成本价
        BigDecimal cost = Objects.isNull(holdingsInfo) ? BigDecimal.ZERO : new BigDecimal(holdingsInfo.getCost());
        //持仓数量
        int count = Objects.isNull(holdingsInfo) ? 0 : holdingsInfo.getCount();
        BigDecimal currentPrice = new BigDecimal(realStockInfo.getCurrentPrice());
        BigDecimal countDecimal = new BigDecimal(count);
        //市值=持仓*当前价
        BigDecimal companyValue = countDecimal.multiply(currentPrice).setScale(4, RoundingMode.CEILING);
        //盈亏=持仓*(当前价-成本价)
        BigDecimal PL = currentPrice.subtract(cost).multiply(countDecimal).setScale(4, RoundingMode.CEILING);
        //盈亏比=(成本价-当前价)/成本价
        BigDecimal PLRate = currentPrice.subtract(cost).divide(cost, 4, RoundingMode.CEILING);

        //今日收益计算 （当前价-上一日收盘价）*持仓数量
        BigDecimal yesterdayPrice = new BigDecimal(realStockInfo.getYesterdayPrice());
        BigDecimal increaseRate = new BigDecimal(realStockInfo.getIncreaseRate()).divide(new BigDecimal("100"), 5, RoundingMode.CEILING);
        BigDecimal todayProfit = currentPrice.subtract(yesterdayPrice).multiply(countDecimal).setScale(4, RoundingMode.CEILING);

        //表格数据

        //股票代码
        vector.add(realStockInfo.getStockCode());
        //名称/市值
        vector.add(new String[]{realStockInfo.getStockName(), NumberFormatUtil.format(companyValue)});
        //持仓盈亏
        String PLRatePrefix = PLRate.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        String PLPrefix = PL.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        vector.add(new String[]{PLPrefix + NumberFormatUtil.format(PL), PLRatePrefix + NumberFormatUtil.formatRate(PLRate)});
        //持仓数量
        vector.add(count);
        //现价/成本
        vector.add(new String[]{realStockInfo.getCurrentPrice(), cost.setScale(3, RoundingMode.CEILING).toString()});
        //今日收益
        String todayProfitPrefix = todayProfit.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        String profitRatePrefix = increaseRate.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        vector.add(new String[]{todayProfitPrefix + NumberFormatUtil.format(todayProfit), profitRatePrefix + NumberFormatUtil.formatRate(increaseRate)});
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
            holdStockDataDto.setCompanyValue(convertBigDecimal(convertValue(vector.get(1), 1)));
            holdStockDataDto.setAllProfit(convertBigDecimal(convertValue(vector.get(2), 0)));
            holdStockDataDto.setTodayProfit(convertBigDecimal(convertValue(vector.get(5), 0)));
            dataList.add(holdStockDataDto);
        });
        return dataList;
    }

    private BigDecimal convertBigDecimal(String value) {
        if (StringUtils.isBlank(value)) {
            return BigDecimal.ZERO;
        }
        if (NumberUtil.isNumber(value)) {
            return new BigDecimal(value);
        }
        return NumberUtil.toBigDecimal(value);
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
                        HoldingsInfo holdingsInfo = holdingsStockDialog.getHoldingsInfo();
                        state.add(tabName, code, holdingsInfo.getCost(), holdingsInfo.getCount());
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

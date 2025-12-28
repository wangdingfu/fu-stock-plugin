package cn.fudoc.trade.view.table;

import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.core.common.enumtype.StockTabEnum;
import cn.fudoc.trade.core.state.StockGroupPersistentState;
import cn.fudoc.trade.util.FuNumberUtil;
import cn.fudoc.trade.view.render.StockColorTableCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.Comparator;
import java.util.Vector;

/**
 * 自选 tab
 */
public class StockGroupTableView extends AbstractStockTableView {

    private final String tabName;
    private static final String[] stockTableColumn = {"股票代码", "股票名称", "当前价格", "涨跌幅(%)", "成交额"};
    private static final String[] colorColumnNames = {"涨跌幅(%)"};
    private final StockGroupPersistentState state;

    public StockGroupTableView(String tabName) {
        this.tabName = tabName;
        for (String columnName : colorColumnNames) {
            stockTable.getColumn(columnName).setCellRenderer(new StockColorTableCellRenderer(null));
        }
        this.state = StockGroupPersistentState.getInstance();
        init(this.state.getStockCodes(tabName));
        stockTable.setRowSorter(getDefaultTableModelTableRowSorter());
    }


    private @NotNull TableRowSorter<DefaultTableModel> getDefaultTableModelTableRowSorter() {
        TableRowSorter<DefaultTableModel> tableRowSorter = new TableRowSorter<>(tableModel);
        tableRowSorter.setComparator(2, Comparator.comparing(FuNumberUtil::toBigDecimal));
        tableRowSorter.setComparator(3, Comparator.comparing(FuNumberUtil::toBigDecimal));
        tableRowSorter.setComparator(4, Comparator.comparing(FuNumberUtil::toBigDecimal));
        return tableRowSorter;
    }

    @Override
    public String getTabName() {
        return tabName;
    }

    @Override
    public StockTabEnum getTabEnum() {
        return StockTabEnum.STOCK_INFO;
    }


    @Override
    protected String[] getColumnNames() {
        return stockTableColumn;
    }

    @Override
    public void addStock(RealStockInfo realStockInfo) {
        super.addStock(realStockInfo);
        this.state.addStock(tabName, realStockInfo.getStockCode());
    }

    @Override
    protected void removeStockFromState(String stockCode) {
        this.state.removeStock(tabName, stockCode);
    }

    @Override
    protected Vector<Object> toTableData(RealStockInfo realStockInfo) {
        Vector<Object> vector = new Vector<>();
        vector.add(realStockInfo.getStockCode());
        vector.add(realStockInfo.getStockName());
        vector.add(realStockInfo.getCurrentPrice());
        vector.add(realStockInfo.getIncreaseRate() + "%");
        vector.add(realStockInfo.getVolume());
        return vector;
    }
}

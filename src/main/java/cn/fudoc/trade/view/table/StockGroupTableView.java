package cn.fudoc.trade.view.table;

import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.core.common.enumtype.GroupTypeEnum;
import cn.fudoc.trade.core.state.StockGroupPersistentState;
import cn.fudoc.trade.core.state.pojo.StockGroupInfo;
import cn.fudoc.trade.util.StockUtils;
import cn.fudoc.trade.view.render.StockColorTableCellRenderer;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Vector;

/**
 * 自选 tab
 */
public class StockGroupTableView extends AbstractStockTableView {


    private static final String[] stockTableColumn = {"股票代码", "股票名称", "当前价格", "涨跌幅(%)", "成交额"};
    private static final List<Integer> colorColumns = Lists.newArrayList(3);
    private final StockGroupPersistentState state;

    public StockGroupTableView(StockGroupInfo stockGroupInfo) {
        super(stockGroupInfo);
        this.state = StockGroupPersistentState.getInstance();
        init(this.state.getStockCodes(groupName()));
        stockTable.setDefaultRenderer(Object.class, new StockColorTableCellRenderer(colorColumns));
        stockTable.setRowSorter(getDefaultTableModelTableRowSorter());
    }

    @Override
    protected boolean isHide() {
        return false;
    }

    @Override
    public GroupTypeEnum getTabEnum() {
        return GroupTypeEnum.STOCK_INFO;
    }

    @Override
    protected String[] getColumnNames() {
        return stockTableColumn;
    }

    @Override
    public void addStock(RealStockInfo realStockInfo) {
        super.addStock(realStockInfo);
        this.state.addStock(groupName(), realStockInfo.getStockCode());
    }

    @Override
    protected void removeStockFromState(String stockCode) {
        this.state.removeStock(groupName(), stockCode);
    }

    @Override
    protected Vector<Object> toTableData(RealStockInfo realStockInfo) {
        Vector<Object> vector = new Vector<>();
        vector.add(StockUtils.formatStockCode(realStockInfo.getStockCode()));
        vector.add(realStockInfo.getStockName());
        vector.add(realStockInfo.getCurrentPrice());
        vector.add(realStockInfo.getIncreaseRate() + "%");
        vector.add(realStockInfo.getVolume() + " " + realStockInfo.getVolumeUnit());
        return vector;
    }
}

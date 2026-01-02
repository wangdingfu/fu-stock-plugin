package cn.fudoc.trade.view.table;

import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.core.common.enumtype.GroupTypeEnum;
import cn.fudoc.trade.core.state.StockGroupPersistentState;
import cn.fudoc.trade.core.state.pojo.StockGroupInfo;
import cn.fudoc.trade.util.PinyinUtil;
import cn.fudoc.trade.util.StockUtils;
import cn.fudoc.trade.view.render.StockColorTableCellRenderer;
import com.google.common.collect.Lists;

import java.util.*;

/**
 * 自选 tab
 */
public class StockGroupHideTableView extends AbstractStockTableView {


    private static final String[] stockTableColumn = {"Code", "Name", "Price", "Change(%)", "Total"};
    private final StockGroupPersistentState state;

    public StockGroupHideTableView(StockGroupInfo stockGroupInfo) {
        super(stockGroupInfo);
        this.state = StockGroupPersistentState.getInstance();
        init(this.state.getStockCodes(groupName()));
        stockTable.setRowSorter(getDefaultTableModelTableRowSorter());
        stockTable.setDefaultRenderer(Object.class, new StockColorTableCellRenderer(Lists.newArrayList()));
    }


    @Override
    protected boolean isHide() {
        return true;
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
        vector.add(PinyinUtil.getFirstLetterRandom(realStockInfo.getStockName()));
        vector.add(realStockInfo.getCurrentPrice());
        vector.add(realStockInfo.getIncreaseRate() + "%");
        vector.add(realStockInfo.getVolume()+" " + PinyinUtil.getFirstLetterRandom(realStockInfo.getVolumeUnit()).toUpperCase());
        return vector;
    }
}

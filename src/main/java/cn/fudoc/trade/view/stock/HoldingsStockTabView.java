package cn.fudoc.trade.view.stock;

import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.common.StockTabEnum;
import cn.fudoc.trade.state.HoldingsStockState;
import cn.fudoc.trade.state.pojo.HoldingsInfo;
import cn.fudoc.trade.util.ProjectUtils;
import cn.fudoc.trade.view.HoldingsStockDialog;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

/**
 * 持仓
 */
public class HoldingsStockTabView extends AbstractStockTabView {

    private final String tabName;
    private static final String[] columnNames = {"代码", "名称", "市值", "持仓", "盈亏", "当前价", "成本"};
    private final HoldingsStockState holdingsStockState;

    public HoldingsStockTabView(String tabName, Set<String> stockCodeSet) {
        super(stockCodeSet);
        this.tabName = tabName;
        this.holdingsStockState = HoldingsStockState.getInstance();
        super.stockTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int selectedRow = stockTable.getSelectedRow();
                    //持久化移除
                    int modelRow = stockTable.convertRowIndexToModel(selectedRow);
                    Object valueAt = tableModel.getValueAt(modelRow, 0);
                    String code = Objects.isNull(valueAt) ? "" : valueAt.toString();
                    HoldingsStockDialog holdingsStockDialog = new HoldingsStockDialog(ProjectUtils.getCurrProject());
                    if (holdingsStockDialog.showAndGet()) {
                        HoldingsInfo holdingsInfo = holdingsStockDialog.getHoldingsInfo();
                        holdingsStockState.add(tabName, code, holdingsInfo.getCost(), holdingsInfo.getCount());
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
        TableColumn idColumn = stockTable.getColumnModel().getColumn(0);
        // 从视图中移除，模型仍保留
        stockTable.getColumnModel().removeColumn(idColumn);
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

    }

    @Override
    protected Vector<Object> toTableData(RealStockInfo realStockInfo) {
        Vector<Object> vector = new Vector<>();
        HoldingsInfo holdingsInfo = holdingsStockState.getHoldingsInfo(tabName, realStockInfo.getStockCode());
        if (Objects.isNull(holdingsInfo)) {
            HoldingsStockDialog holdingsStockDialog = new HoldingsStockDialog(ProjectUtils.getCurrProject());
            if (holdingsStockDialog.showAndGet()) {
                holdingsInfo = holdingsStockDialog.getHoldingsInfo();
                holdingsStockState.add(tabName, realStockInfo.getStockCode(), holdingsInfo.getCost(), holdingsInfo.getCount());
            }
        }
        //持仓成本价
        BigDecimal cost = Objects.isNull(holdingsInfo) ? BigDecimal.ZERO : holdingsInfo.getCost();
        //持仓数量
        int count = Objects.isNull(holdingsInfo) ? 0 : holdingsInfo.getCount();
        //股票代码
        vector.add(realStockInfo.getStockCode());
        //股票名称
        vector.add(realStockInfo.getStockName());
        //市值=持仓*当前价
        vector.add(cost.multiply(new BigDecimal(realStockInfo.getCurrentPrice())));
        //持仓数量
        vector.add(count);
        //盈亏=持仓*(当前价-成本价)
        vector.add(new BigDecimal(realStockInfo.getCurrentPrice()).subtract(cost).multiply(new BigDecimal(count)));
        //当前价
        vector.add(realStockInfo.getCurrentPrice());
        //成本价
        vector.add(cost);
        return vector;
    }
}

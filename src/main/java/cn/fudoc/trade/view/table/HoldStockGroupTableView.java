package cn.fudoc.trade.view.table;

import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.core.common.enumtype.StockTabEnum;
import cn.fudoc.trade.core.state.HoldingsStockState;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.util.NumberFormatUtil;
import cn.fudoc.trade.util.ProjectUtils;
import cn.fudoc.trade.view.dialog.HoldingsStockDialog;
import cn.fudoc.trade.view.render.MultiLineTableCellRenderer;
import com.google.common.collect.Lists;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Vector;

/**
 * 持仓tab
 */
public class HoldStockGroupTableView extends AbstractStockTableView {

    private final String tabName;
    private static final String[] columnNames = {"代码", "名称 / 市值", "持仓盈亏", "持仓数量", "现价 / 成本"};
    private final HoldingsStockState state;

    public HoldStockGroupTableView(String tabName) {
        this.tabName = tabName;
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
        int rowHeight = stockTable.getRowHeight();
        stockTable.setRowHeight(rowHeight * 2);
        for (String columnName : getColumnNames()) {
            stockTable.getColumn(columnName).setCellRenderer(new MultiLineTableCellRenderer(Lists.newArrayList(1)));
        }

        TableColumn idColumn = stockTable.getColumnModel().getColumn(0);
        // 从视图中移除，模型仍保留
        stockTable.getColumnModel().removeColumn(idColumn);
        this.state = HoldingsStockState.getInstance();
        init(this.state.getStockCodes(tabName));

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
        BigDecimal companyValue = countDecimal.multiply(currentPrice).setScale(2, RoundingMode.CEILING);
        //盈亏=持仓*(当前价-成本价)
        BigDecimal PL = currentPrice.subtract(cost).multiply(countDecimal).setScale(2, RoundingMode.CEILING);
        //盈亏比=(成本价-当前价)/成本价
        BigDecimal PLRate = currentPrice.subtract(cost).divide(cost, 4, RoundingMode.CEILING);

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
        return vector;
    }
}

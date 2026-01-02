package cn.fudoc.trade.view.table;

import cn.fudoc.trade.core.common.FuNotification;
import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.state.pojo.StockGroupInfo;
import cn.fudoc.trade.util.ProjectUtils;
import cn.fudoc.trade.view.holdings.HoldingsStockDialog;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

public abstract class AbstractHoldingsTable extends AbstractStockTableView{


    public AbstractHoldingsTable(StockGroupInfo stockGroupInfo) {
        super(stockGroupInfo);
    }

    protected void addListener() {
        super.stockTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //双击进入设置持仓信息页面
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    openDialog(FuTradeConstants.TabName.HOLDINGS_COST_TAB);
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


    protected void openDialog(String openTab) {
        int selectedRow = stockTable.getSelectedRow();
        if(selectedRow == -1){
            FuNotification.notifyWarning("请先选中行在右键");
            return;
        }
        int modelRow = stockTable.convertRowIndexToModel(selectedRow);
        Object valueAt = tableModel.getValueAt(modelRow, 0);
        Object valueAt1 = tableModel.getValueAt(modelRow, 1);
        String code = Objects.isNull(valueAt) ? "" : valueAt.toString();
        String name = (valueAt1 instanceof String[] values && values.length > 0) ? values[0] : "";
        HoldingsStockDialog holdingsStockDialog = new HoldingsStockDialog(ProjectUtils.getCurrProject(), getTabName(), code, name, openTab);
        if (holdingsStockDialog.showAndGet()) {
            reloadAllStock();
        }
    }
}

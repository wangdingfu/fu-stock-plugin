package cn.fudoc.trade.view.holdings.tab;

import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.common.enumtype.TradeTypeEnum;
import cn.fudoc.trade.core.helper.TableHelper;
import cn.fudoc.trade.core.helper.TableListener;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.core.state.pojo.TradeInfoLog;
import cn.fudoc.trade.util.FuNumberUtil;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.NumberUtil;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.table.JBTable;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.math.BigDecimal;
import java.util.*;

/**
 * 交易记录 tab 视图
 */
public class HoldingsTradeLogTabView extends AbstractHoldingsTabView implements TableListener {
    private static final String[] columnNames = {"ID", "交易类型", "交易数量", "交易价格", "手续费", "交易时间"};


    protected final JBTable stockTable;
    protected final DefaultTableModel tableModel;
    private final TableHelper tableHelper;

    public HoldingsTradeLogTabView(StockInfoDTO stockInfoDTO, HoldingsInfo holdingsInfo) {
        super(stockInfoDTO, holdingsInfo);
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            // 设置单元格不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable = new JBTable(tableModel);
        TableColumn timeColumn = stockTable.getColumnModel().getColumn(5);
        timeColumn.setPreferredWidth(120);
        TableColumn idColumn = stockTable.getColumnModel().getColumn(0);
        // 从视图中移除，模型仍保留
        stockTable.getColumnModel().removeColumn(idColumn);
        this.tableHelper = new TableHelper(this.stockTable, this.tableModel, this);
        initData();
    }


    @Override
    public String getTabName() {
        return FuTradeConstants.TabName.HOLDINGS_LOG_TAB;
    }

    @Override
    public JPanel getPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(tableHelper.createTablePanel());
        return mainPanel;
    }

    @Override
    public void submit(HoldingsInfo holdingsInfo) {

        List<TradeInfoLog> tradeList = holdingsInfo.getTradeList();
        Set<Long> tableIdSet = getTableIdSet();
        //移除不在表格中的交易记录
        tradeList.removeIf(f -> !tableIdSet.contains(f.getId()));
    }



    @Override
    public ValidationInfo doValidate() {
        //没有输入内容 无需校验
        return null;
    }


    @Override
    public void removeRow(int modelRow) {
        //无需处理
    }


    private Set<Long> getTableIdSet() {
        Vector<Vector> dataVector = tableModel.getDataVector();
        Set<Long> idSet = new HashSet<>();
        for (Vector vector : dataVector) {
            Object o = vector.get(0);
            if (Objects.nonNull(o) && NumberUtil.isLong(o.toString())) {
                idSet.add(NumberUtil.binaryToLong(o.toString()));
            }
        }
        return idSet;
    }

    protected Vector<Object> toTableData(TradeInfoLog tradeInfoLog) {
        Vector<Object> vector = new Vector<>();
        vector.add(tradeInfoLog.getId());
        vector.add(TradeTypeEnum.getName(tradeInfoLog.getType()));
        Integer count = tradeInfoLog.getCount();
        vector.add(Objects.isNull(count) ? "" : count);
        vector.add(FuNumberUtil.formatCost(tradeInfoLog.getPrice()));
        vector.add(FuNumberUtil.formatCost(tradeInfoLog.getHandlingFee()));
        vector.add(DatePattern.NORM_DATETIME_FORMAT.format(new Date(tradeInfoLog.getTime())));
        return vector;
    }




    private void initData() {
        List<TradeInfoLog> tradeList = holdingsInfo.getTradeList();
        if (CollectionUtils.isEmpty(tradeList)) {
            return;
        }
        tradeList.forEach(f -> tableModel.addRow(toTableData(f)));
    }


}

package cn.fudoc.trade.view.table.profit;

import cn.fudoc.trade.view.dto.HoldStockDataDto;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

class ProfitTableModel extends AbstractTableModel {
    // 列名（注意：实际表格中“排名”是第一列，“股票名称+代码”合并显示）
    private final String[] COLUMN_NAMES = {"排名", "股票信息", "今日收益"};
    private List<HoldStockDataDto> dataList = new ArrayList<>();

    // 初始化测试数据（对应截图内容）
    public ProfitTableModel() {
    }

    // 批量设置数据（核心：动态传入）
    public void setData(List<HoldStockDataDto> newData) {
        this.dataList = newData; // 避免外部修改原列表
        fireTableDataChanged(); // 通知表格刷新所有数据
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public int getRowCount() {
        return dataList.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        HoldStockDataDto item = dataList.get(row);
        return switch (col) {
            case 0 -> row+1;
            case 1 -> new String[]{item.getStockName() , item.getStockCode()}; // 合并名称+代码（换行显示）
            case 2 -> item.getTodayProfit();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return switch (col) {
            case 0, 2 -> Integer.class;
            case 1 -> String.class;
            default -> Object.class;
        };
    }
}
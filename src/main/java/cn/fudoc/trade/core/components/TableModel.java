package cn.fudoc.trade.core.components;

import cn.fudoc.trade.core.helper.TableListener;
import com.intellij.util.ui.EditableModel;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class TableModel extends DefaultTableModel implements EditableModel {

    private final TableListener tableListener;
    public TableModel(String[] columnNames, TableListener tableListener){
        super(columnNames,0);
        this.tableListener = tableListener;
    }

    @Override
    public void addRow() {
        super.addRow(tableListener.addRow());
    }

    @Override
    public void exchangeRows(int oldIndex, int newIndex) {
        if (canExchangeRows(oldIndex, newIndex)) {
            //table 中移动
            super.moveRow(oldIndex, oldIndex, newIndex);
        }
    }

    @Override
    public boolean canExchangeRows(int i, int i1) {
        return true;
    }
}

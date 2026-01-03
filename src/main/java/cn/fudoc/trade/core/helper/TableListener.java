package cn.fudoc.trade.core.helper;

public interface TableListener {

    void removeRow(int modelRow);

    default Object[] addRow(){
        return new Object[]{};
    }
}

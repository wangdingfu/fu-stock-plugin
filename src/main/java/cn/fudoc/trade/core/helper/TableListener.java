package cn.fudoc.trade.core.helper;

public interface TableListener {

    void removeRow(int modelRow);

    default Object[] addRow(){
        return new Object[]{};
    }

    default boolean isCellEditable(int i, int i1){
        return true;
    }
}

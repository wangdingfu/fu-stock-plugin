package cn.fudoc.trade.view;

import cn.fudoc.trade.view.stock.StockTabView;

import javax.swing.*;

public interface FuStockItemView {


    JComponent getComponent();


    StockTabView getSelected();


    void add(String tab);

}

package cn.fudoc.trade.view;

import cn.fudoc.trade.view.stock.StockTabView;

import javax.swing.*;

public interface FuStockTabView {

    /**
     * 展示的组件
     */
    JComponent getComponent();

    /**
     * 选中的tab
     */
    StockTabView getSelected();

    /**
     * 新增一个tab
     */
    void add(String tab);

}

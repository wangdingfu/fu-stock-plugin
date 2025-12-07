package cn.fudoc.trade.view.stock;

import javax.swing.*;
import java.util.Set;

public abstract class AbstractDefaultStockTabView extends AbstractStockTabView {


    public AbstractDefaultStockTabView(Set<String> stockCodeSet) {
        super(stockCodeSet);
    }

    @Override
    public JPanel getTabComponent() {
        return null;
    }

    @Override
    public void startTask() {

    }

    @Override
    public void stopTask() {

    }

    @Override
    public void reloadStock() {

    }


    @Override
    public void addStock(String stockCode) {

    }

    @Override
    public void removeStock(String stockCode) {

    }
}

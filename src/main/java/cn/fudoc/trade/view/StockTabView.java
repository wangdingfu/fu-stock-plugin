package cn.fudoc.trade.view;

import javax.swing.*;

public interface StockTabView {

    /**
     *
     * @return 分组tab名称
     */
    String getTabName();


    /**
     * tab展示的组件
     */
    JPanel getTabComponent();


    /**
     * 开启自动加载股票任务
     */
    void startTask();


    /**
     * 停止自动加载股票任务
     */
    void stopTask();


    /**
     * 加载股票实时信息
     */
    void reloadStock();

}

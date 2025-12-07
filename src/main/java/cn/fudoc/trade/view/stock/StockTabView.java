package cn.fudoc.trade.view.stock;

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
    boolean startTask();


    /**
     * 停止自动加载股票任务
     */
    void stopTask();

    /**
     * 停止自动加载股票任务
     */
    void shutdownTask();
    /**
     * 加载股票实时信息
     */
    void reloadStock();


    /**
     * 当前tab中是否包含指定股票
     *
     * @param stockCode 股票代码
     * @return true 当前tab已经存在了该股票
     */
    boolean isContainsStock(String stockCode);

    /**
     * 添加股票至当前tab分组
     *
     * @param stockCode 股票代码
     */
    void addStock(String stockCode);

    /**
     * 从当前tab分组移除股票
     *
     * @param stockCode 股票代码
     */
    void removeStock(String stockCode);
}

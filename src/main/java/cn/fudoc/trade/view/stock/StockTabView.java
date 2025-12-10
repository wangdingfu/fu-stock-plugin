package cn.fudoc.trade.view.stock;

import cn.fudoc.trade.api.data.RealStockInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.List;
import java.util.Set;

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

    /**
     * 获取当前tab下的股票代码集合
     *
     * @return 股票代码集合
     */
    Set<String> getStockCodes();

    /**
     * 初始化股票实时信息
     *
     * @param stockInfoList 股票实时信息
     */
    void initStockList(List<RealStockInfo> stockInfoList);

    /**
     * 当前tab中是否包含指定股票
     *
     * @param stockCode 股票代码
     * @return true 当前tab已经存在了该股票
     */
    default boolean isContainsStock(String stockCode) {
        if (StringUtils.isBlank(stockCode)) {
            return false;
        }
        Set<String> stockCodes = getStockCodes();
        if (CollectionUtils.isEmpty(stockCodes)) {
            return false;
        }
        return stockCodes.contains(stockCode);
    }
}

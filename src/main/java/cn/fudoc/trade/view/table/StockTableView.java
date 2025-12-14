package cn.fudoc.trade.view.table;

import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.core.common.enumtype.StockTabEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.Set;

public interface StockTableView {

    /**
     *
     * @return 分组tab名称
     */
    String getTabName();

    /**
     * 股票tab类型
     */
    StockTabEnum getTabEnum();

    /**
     * tab展示的组件
     */
    JPanel getComponent();

    /**
     * 添加股票至当前tab分组
     *
     * @param realStockInfo 股票实时信息
     */
    void addStock(RealStockInfo realStockInfo);

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
     * 重新加载所有股票
     */
    default void reloadAllStock() {
        reloadAllStock(null);
    }

    /**
     * 重新加载所有股票
     */
    void reloadAllStock(String tag);


    /**
     * 更新提示信息 tag标识
     *
     * @param tag 例如开盘后自动刷新等提示信息
     */
    void updateTipTag(String tag);

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

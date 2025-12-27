package cn.fudoc.trade.core.state.pojo;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 持仓信息
 */
@Getter
@Setter
public class HoldingsInfo {

    /**
     * 成本价
     */
    private String cost;
    /**
     * 持仓数量
     */
    private Integer count;

    /**
     * 交易记录(只记录当天交易记录)
     */
    private List<TradeInfoLog> tradeList;

    /**
     * 历史交易记录
     */
    private List<TradeInfoLog> logList;


    /**
     * 添加交易记录
     *
     * @param type  交易类型
     * @param count 交易数量
     * @param price 交易价格
     */
    public void add(Integer type, Integer count, String price) {
        if (tradeList == null) {
            tradeList = Lists.newArrayList();
        }
        TradeInfoLog tradeInfoLog = new TradeInfoLog();
        tradeInfoLog.setId(IdUtil.getSnowflakeNextId());
        tradeInfoLog.setCount(count);
        tradeInfoLog.setPrice(price);
        tradeInfoLog.setType(type);
        tradeInfoLog.setTime(System.currentTimeMillis());
        tradeList.add(tradeInfoLog);
    }

    public BigDecimal calculateCost() {
        return calculateCost(this.cost);
    }

    public Integer calculateCount() {
        return calculateCount(this.count);
    }

    /**
     * 计算实际持仓成本
     */
    public BigDecimal calculateCost(String cost) {
        //TODO 计算持仓成本
        return new BigDecimal(cost);
    }

    /**
     * 计算实际持仓数量
     */
    public Integer calculateCount(Integer count) {
        if (CollectionUtils.isEmpty(tradeList)) {
            return count;
        }
        return count + tradeList.stream().mapToInt(TradeInfoLog::getCount).sum();
    }
}

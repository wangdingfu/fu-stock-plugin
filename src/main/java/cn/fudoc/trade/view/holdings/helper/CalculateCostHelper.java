package cn.fudoc.trade.view.holdings.helper;

import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.core.state.pojo.TradeInfoLog;
import cn.fudoc.trade.util.FuNumberUtil;
import cn.fudoc.trade.view.dto.HoldingsTodayInfo;
import cn.hutool.core.date.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 计算持仓成本
 */
public class CalculateCostHelper {
    /**
     * 计算持仓成本
     *
     * @param holdingsInfo 当前持仓信息
     * @return 计算出来的当前持仓信息
     */
    public static HoldingsTodayInfo calculate(HoldingsInfo holdingsInfo) {
        return calculate(holdingsInfo.getCost(), holdingsInfo.getCount(), holdingsInfo.getTradeList());
    }

    /**
     * 计算持仓成本
     *
     * @param cost      今日以前初始成本
     * @param count     今日以前初始持仓数量
     * @param tradeList 今日交易记录
     * @return 今日实时持仓成本
     */
    public static HoldingsTodayInfo calculate(String cost, Integer count, List<TradeInfoLog> tradeList) {
        BigDecimal currentCost = FuNumberUtil.toBigDecimal(cost);
        Integer currentCount = count;
        BigDecimal currentProfit = BigDecimal.ZERO;
        if (tradeList == null || tradeList.isEmpty()) {
            return new HoldingsTodayInfo(currentCost, currentCount, currentCount);
        }
        tradeList.sort(Comparator.comparing(TradeInfoLog::getTime));
        long todayBeginDay = DateUtil.beginOfDay(new Date()).getTime();
        for (TradeInfoLog tradeInfoLog : tradeList) {
            Integer type = tradeInfoLog.getType();
            if (type == 0) {
                //只有买入 和 卖出交易记录 才用于计算当前成本 其他类型不考虑
                continue;
            }
            if (todayBeginDay >= tradeInfoLog.getTime()) {
                //不是当日交易记录 不考虑
                continue;
            }
            BigDecimal tradePrice = FuNumberUtil.toBigDecimal(tradeInfoLog.getPrice());
            //手续费
            BigDecimal handlingFee = FuNumberUtil.toBigDecimal(tradeInfoLog.getHandlingFee());
            Integer tradeCount = tradeInfoLog.getCount();
            BigDecimal totalAmount;
            if (type == 1) {
                //买入动作 计算当前成本
                totalAmount = multiply(currentCost, currentCount).add(multiply(tradePrice, tradeCount).add(currentProfit).add(handlingFee));
                currentCount += tradeCount;
            } else if (type == 2) {
                //卖出动作 计算当前成本
                totalAmount = multiply(currentCost, currentCount).subtract(multiply(tradePrice, tradeCount).subtract(handlingFee));
                currentCount -= tradeCount;
            } else if (type == 3) {
                //分红
                totalAmount = multiply(currentCost, currentCount).subtract(multiply(tradePrice, tradeCount));
            } else if (type == 4) {
                //股息红利税补缴
                totalAmount = multiply(currentCost, currentCount).add(handlingFee);
            } else {
                //其他情况暂不考虑
                continue;
            }
            if (currentCount == 0) {
                currentProfit = totalAmount;
                currentCost = BigDecimal.ZERO;
                continue;
            } else {
                currentProfit = BigDecimal.ZERO;
            }
            currentCost = totalAmount.divide(new BigDecimal(currentCount), 24, RoundingMode.CEILING);
        }
        return new HoldingsTodayInfo(currentCost, currentCount, currentCount);
    }


    /**
     * 计算今日收益
     *
     * @param currentPrice   当前价
     * @param yesterdayPrice 昨日收盘价
     * @param holdingsInfo   持仓信息
     * @return 今日收益
     */
    public static BigDecimal calculateProfit(BigDecimal currentPrice, BigDecimal yesterdayPrice, HoldingsInfo holdingsInfo) {
        //历史持仓数量*（当前价-昨日收盘价）+ 今日买入数量*（当前价-买入价） + 今日卖出数量 * （当前价-昨日收盘价）
        BigDecimal diffPrice = currentPrice.subtract(yesterdayPrice);
        BigDecimal todayProfit = multiply(diffPrice, holdingsInfo.getCount());
        List<TradeInfoLog> tradeList = holdingsInfo.getTradeList();
        if (tradeList == null || tradeList.isEmpty()) {
            return todayProfit;
        }
        long todayBeginDay = DateUtil.beginOfDay(new Date()).getTime();
        for (TradeInfoLog tradeInfoLog : tradeList) {
            Integer type = tradeInfoLog.getType();
            if (type == 0) {
                //只有买入 和 卖出交易记录 才用于计算当前成本 其他类型不考虑
                continue;
            }
            if (todayBeginDay >= tradeInfoLog.getTime()) {
                //不是当日交易记录 不考虑
                continue;
            }
            BigDecimal tradeProfit;
            BigDecimal tradePrice = FuNumberUtil.toBigDecimal(tradeInfoLog.getPrice());
            //手续费
            BigDecimal handlingFee = FuNumberUtil.toBigDecimal(tradeInfoLog.getHandlingFee());
            Integer tradeCount = tradeInfoLog.getCount();
            if (type == 1) {
                //买入动作
                tradeProfit = multiply(currentPrice.subtract(tradePrice),tradeCount).subtract(handlingFee);
            } else if (type == 2) {
                //卖出动作 计算当前成本
                tradeProfit = multiply(currentPrice.subtract(yesterdayPrice),tradeCount).subtract(handlingFee);
            }else {
                //其他情况暂不考虑
                continue;
            }
            todayProfit = todayProfit.add(tradeProfit);
        }
        return todayProfit;
    }


    private static BigDecimal multiply(BigDecimal cost, Integer count) {
        return cost.multiply(new BigDecimal(count));
    }

}

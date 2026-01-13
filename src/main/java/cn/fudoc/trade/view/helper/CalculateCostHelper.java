package cn.fudoc.trade.view.helper;

import cn.fudoc.trade.core.common.enumtype.JYSEnum;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.core.state.pojo.TradeInfoLog;
import cn.fudoc.trade.core.state.pojo.TradeRateInfo;
import cn.fudoc.trade.util.FuNumberUtil;
import cn.fudoc.trade.view.dto.HoldingsTodayInfo;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 计算持仓成本
 */
public class CalculateCostHelper {


    /**
     * 计算当前实时持仓成本
     *
     */
    public static HoldingsTodayInfo calculate(BigDecimal currentPrice, BigDecimal yesterdayPrice, HoldingsInfo holdingsInfo) {
        //计算持仓成本前 刷新当天持仓信息
        refreshTodayHoldingsInfo(currentPrice, yesterdayPrice, holdingsInfo);
        return calculate(currentPrice, yesterdayPrice, holdingsInfo.getCost(), holdingsInfo.getCount(), holdingsInfo.getTradeList(), true);
    }

    /**
     * 计算当前实时持仓成本
     *
     */
    public static HoldingsTodayInfo calculate(String cost, Integer count, HoldingsInfo holdingsInfo) {
        return calculate(BigDecimal.ZERO, BigDecimal.ZERO, cost, count, Objects.isNull(holdingsInfo) ? Lists.newArrayList() : holdingsInfo.getTradeList(), true);
    }

    /**
     * 计算今日收益
     *
     * @param currentPrice   当前价
     * @param yesterdayPrice 昨日收盘价
     * @param cost           昨日收盘成本价
     * @param count          昨日收盘持仓数量
     * @param tradeList      交易记录
     * @param isToday        是否只计算今日交易记录
     * @return 今日收益
     */
    public static HoldingsTodayInfo calculate(BigDecimal currentPrice, BigDecimal yesterdayPrice, String cost, Integer count, List<TradeInfoLog> tradeList, boolean isToday) {
        //历史持仓数量*（当前价-昨日收盘价）+ 今日买入数量*（当前价-买入价） + 今日卖出数量 * （当前价-昨日收盘价）
        BigDecimal diffPrice = currentPrice.subtract(yesterdayPrice);
        //当前持仓成本
        BigDecimal currentCost = FuNumberUtil.toBigDecimal(cost);
        //当前持仓数量
        Integer currentCount = count;
        //上一交易日的持仓数量（可卖数量）
        Integer lastDayCount = count;
        if (tradeList == null || tradeList.isEmpty()) {
            BigDecimal profit = multiply(diffPrice, count);
            return new HoldingsTodayInfo(currentCost, currentCount, currentCount, profit, profit);
        }
        //截止到上一交易日的总收益
        BigDecimal lastDayProfit = multiply(yesterdayPrice.subtract(currentCost), count);
        BigDecimal todayProfit = BigDecimal.ZERO;
        long todayBeginDay = DateUtil.beginOfDay(new Date()).getTime();
        for (TradeInfoLog tradeInfoLog : tradeList) {
            Integer type = tradeInfoLog.getType();
            if (type == 0) {
                //只有买入 和 卖出交易记录 才用于计算当前成本 其他类型不考虑
                continue;
            }
            if (isToday && todayBeginDay >= tradeInfoLog.getTime()) {
                //不是当日交易记录 不考虑
                continue;
            }
            //这笔交易当前产生的利润
            BigDecimal tradeProfit = BigDecimal.ZERO;
            //发生这比交易后的总市值 如果清仓 则总市值为总利润
            BigDecimal totalAmount = currentCount == 0 ? lastDayProfit.add(todayProfit) : multiply(currentCost, currentCount);
            BigDecimal tradePrice = FuNumberUtil.toBigDecimal(tradeInfoLog.getPrice());
            //手续费
            BigDecimal handlingFee = FuNumberUtil.toBigDecimal(tradeInfoLog.getHandlingFee());
            Integer tradeCount = tradeInfoLog.getCount();
            if (type == 1) {
                //买入动作 发生这笔交易后 总市值 = 发生交易前的成本价*持仓数量 + 本次交易价格*交易数量+手续费（为什么要加手续费而不是减？===> ）
                totalAmount = totalAmount.add(multiply(tradePrice, tradeCount).add(handlingFee));
                currentCount += tradeCount;
                //本次交易的收益
                tradeProfit = multiply(currentPrice.subtract(tradePrice), tradeCount).subtract(handlingFee);
            } else if (type == 2) {
                //卖出动作 发生这笔交易后 总市值 = 发生交易前的成本价*持仓数量 - 本次交易价格*交易数量 - 手续费（为什么要减手续费而不是加？===> ）
                totalAmount = totalAmount.subtract(multiply(tradePrice, tradeCount).subtract(handlingFee));
                //当前持仓数量（实际持仓数量）
                currentCount -= tradeCount;
                //卖出的是上一交易日持仓的数量 所以需要减掉上一交易日持仓数量
                lastDayCount -= tradeCount;
                //卖出上一交易日股票的收益
                tradeProfit = multiply(tradePrice.subtract(yesterdayPrice), tradeCount).subtract(handlingFee);
            } else if (type == 3) {
                //分红
                totalAmount = totalAmount.subtract(multiply(tradePrice, tradeCount));
            } else if (type == 4) {
                //股息红利税补缴
                totalAmount = totalAmount.add(handlingFee);
            } else {
                //其他情况暂不考虑
                continue;
            }
            //当前成本 = 当前总市值 / 当前持仓数量
            currentCost = currentCount == 0 ? FuNumberUtil.toBigDecimal(cost) : totalAmount.divide(new BigDecimal(currentCount), 24, RoundingMode.CEILING);
            //今日收益累加上本次交易收益
            todayProfit = todayProfit.add(tradeProfit);
        }
        if (lastDayCount > 0) {
            //上一交易日还未卖出的持仓收益
            todayProfit = todayProfit.add(multiply(diffPrice, lastDayCount));
        }
        return new HoldingsTodayInfo(currentCost, currentCount, lastDayCount, todayProfit, lastDayProfit.add(todayProfit));
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
        List<TradeInfoLog> tradeList = holdingsInfo.getTradeList();
        if (tradeList == null || tradeList.isEmpty()) {
            return multiply(diffPrice, holdingsInfo.getCount());
        }
        BigDecimal todayProfit = BigDecimal.ZERO;
        Integer remainingCount = holdingsInfo.getCount();
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
                tradeProfit = multiply(currentPrice.subtract(tradePrice), tradeCount).subtract(handlingFee);
            } else if (type == 2) {
                //卖出动作 计算当前成本
                remainingCount -= tradeCount;
                tradeProfit = multiply(tradePrice.subtract(yesterdayPrice), tradeCount).subtract(handlingFee);
            } else {
                //其他情况暂不考虑
                continue;
            }
            todayProfit = todayProfit.add(tradeProfit);
        }
        if (remainingCount > 0) {
            todayProfit = todayProfit.add(multiply(diffPrice, remainingCount));
        }
        return todayProfit;
    }


    /**
     * 计算交易手续费 仅针对买入 卖出时计算
     *
     * @param type       类型 1:买入 2:卖出
     * @param rate       手续费配置信息
     * @param tradePrice 交易价格
     * @param tradeCount 交易数量
     * @return 本次交易收取的手续费
     */
    public static BigDecimal calculateHandlingFee(Integer type, TradeRateInfo rate, BigDecimal tradePrice, Integer tradeCount, JYSEnum jysEnum) {
        if (Objects.isNull(type) || Objects.isNull(rate) || (type != 1 && type != 2)) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalAmount = multiply(tradePrice, tradeCount);
        BigDecimal handlingFee = BigDecimal.ZERO;
        BigDecimal commissionRate = FuNumberUtil.toBigDecimal(rate.getCommissionRate());
        BigDecimal commissionFee = totalAmount.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal minFee = FuNumberUtil.toBigDecimal(rate.getMinFee());
        if (minFee.compareTo(commissionFee) > 0) {
            //最低收取5元
            commissionFee = minFee;
        }
        //券商佣金
        handlingFee = handlingFee.add(commissionFee);

        //印花税 卖出时才计算
        if (type == 2) {
            BigDecimal stampDutyRate = FuNumberUtil.toBigDecimal(rate.getStampDutyRate());
            //印花税默认保留两位小数 四舍五入
            handlingFee = handlingFee.add(totalAmount.multiply(stampDutyRate).setScale(2, RoundingMode.HALF_UP));
        }

        //过户费
        BigDecimal transferRate = FuNumberUtil.toBigDecimal(transferRate(rate, jysEnum));
        handlingFee = handlingFee.add(totalAmount.multiply(transferRate).setScale(2, RoundingMode.HALF_UP));

        //其他费率
        BigDecimal otherRate = FuNumberUtil.toBigDecimal(rate.getOtherRate());
        handlingFee = handlingFee.add(totalAmount.multiply(otherRate).setScale(2, RoundingMode.HALF_UP));

        //其他费用
        BigDecimal otherFee = FuNumberUtil.toBigDecimal(rate.getOtherFee());
        handlingFee = handlingFee.add(otherFee);

        //本次交易手续费
        return handlingFee;
    }


    private static String transferRate(TradeRateInfo rate, JYSEnum jysEnum) {
        if (JYSEnum.SH.equals(jysEnum)) {
            return rate.getTransferSHRate();
        } else if (JYSEnum.SZ.equals(jysEnum)) {
            return rate.getTransferSZRate();
        }
        return "0";
    }


    /**
     * 刷新当天持仓信息 每天第一次加载时刷新
     */
    public static void refreshTodayHoldingsInfo(BigDecimal currentPrice, BigDecimal yesterdayPrice, HoldingsInfo holdingsInfo) {
        List<TradeInfoLog> tradeList = holdingsInfo.getTradeList();
        if (CollectionUtils.isEmpty(tradeList)) {
            //没有交易记录 无需处理
            return;
        }
        long todayBeginDay = DateUtil.beginOfDay(new Date()).getTime();
        Long refreshTime = holdingsInfo.getRefreshTime();
        if (Objects.nonNull(refreshTime) && refreshTime > todayBeginDay) {
            //当天已经刷新过 无需再次刷新
            return;
        }
        List<TradeInfoLog> logList = holdingsInfo.getLogList();
        //当天之前的交易信息
        List<TradeInfoLog> beforeTradeList = tradeList.stream().filter(f -> f.getTime() < todayBeginDay).toList();
        //计算上一交易日的持仓成本 并将上一交易日的持仓成本设置到当前持仓成本信息中
        HoldingsTodayInfo holdingsTodayInfo = calculate(currentPrice, yesterdayPrice, holdingsInfo.getCost(), holdingsInfo.getCount(), beforeTradeList, false);
        holdingsInfo.setCost(holdingsTodayInfo.getCurrentCost().toString());
        holdingsInfo.setCount(holdingsTodayInfo.getTotal());

        //将上一交易日的交易记录移动到日志表中
        if (Objects.isNull(logList)) {
            logList = new ArrayList<>();
            holdingsInfo.setLogList(logList);
        }
        logList.addAll(beforeTradeList);
        tradeList.removeIf(f -> f.getTime() < todayBeginDay);
        //记录本次刷新时间 防止当天重复刷新
        holdingsInfo.setRefreshTime(System.currentTimeMillis());
    }

    private static BigDecimal multiply(BigDecimal price, Integer count) {
        if (Objects.isNull(count)) {
            count = 0;
        }
        return price.multiply(new BigDecimal(count));
    }

}

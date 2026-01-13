package cn.fudoc.trade.view.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class HoldingsTodayInfo {

    /**
     * 当前实际持仓成本
     */
    private BigDecimal currentCost;
    /**
     * 当前总共仓位
     */
    private Integer total;
    /**
     * 当日可用仓位
     */
    private Integer count;

    /**
     * 今日收益
     */
    private BigDecimal todayProfit;

    public HoldingsTodayInfo(BigDecimal currentCost,Integer total,Integer count){
        this.currentCost = currentCost;
        this.total = total;
        this.count = count;
    }
}

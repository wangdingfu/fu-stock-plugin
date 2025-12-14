package cn.fudoc.trade.view.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HoldStockDataDto {

    private String stockCode;

    private BigDecimal companyValue;

    private BigDecimal allProfit;

    private BigDecimal todayProfit;
}

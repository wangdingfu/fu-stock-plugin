package cn.fudoc.trade.core.state.index;

import cn.fudoc.trade.api.data.StockInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MatchResult {

    /**
     * 匹配的股票
     */
    private StockInfo stockInfo;

    /**
     * 相似度 最高1（代表完全匹配）  最低0
     */
    private double similarity;
}

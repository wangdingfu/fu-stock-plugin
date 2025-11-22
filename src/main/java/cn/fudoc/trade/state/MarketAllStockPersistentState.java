package cn.fudoc.trade.state;

import cn.fudoc.trade.api.data.StockInfo;
import cn.fudoc.trade.state.index.MatchResult;
import cn.fudoc.trade.state.index.StockIndex;
import com.google.common.collect.Lists;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@State(
        name = "fuAllStock",
        storages = @Storage("fu-all-stock.xml")
)
@Getter
@Setter
public class MarketAllStockPersistentState implements PersistentStateComponent<MarketAllStockPersistentState> {

    /**
     * 大A股票索引
     */
    private StockIndex A = new StockIndex(false);
    /**
     * 香港股票索引
     */
    private StockIndex HK = new StockIndex(true);


    public void initMarketA(List<StockInfo> stockInfoList) {
        this.A = new StockIndex(stockInfoList, false);
    }

    public void initMarketHK(List<StockInfo> stockInfoList) {
        this.HK = new StockIndex(stockInfoList, true);
    }


    public List<StockInfo> match(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return new ArrayList<>();
        }
        List<MatchResult> marketAMatchList = A.match(keyword);
        if (Objects.nonNull(marketAMatchList) && !marketAMatchList.isEmpty() && marketAMatchList.stream().anyMatch(a -> a.getSimilarity() == 1L)) {
            return marketAMatchList.stream().filter(f -> f.getSimilarity() == 1D).map(MatchResult::getStockInfo).collect(Collectors.toList());
        }
        List<MatchResult> marketHKMatchList = HK.match(keyword);
        if (Objects.nonNull(marketHKMatchList) && !marketHKMatchList.isEmpty() && marketHKMatchList.stream().anyMatch(a -> a.getSimilarity() == 1L)) {
            return marketHKMatchList.stream().filter(f -> f.getSimilarity() == 1D).map(MatchResult::getStockInfo).collect(Collectors.toList());
        }
        List<MatchResult> matchList = Lists.newArrayList();
        if (Objects.nonNull(marketAMatchList) && !marketAMatchList.isEmpty()) {
            matchList.addAll(marketAMatchList);
        }
        if (Objects.nonNull(marketHKMatchList) && !marketHKMatchList.isEmpty()) {
            matchList.addAll(marketHKMatchList);
        }
        //取匹配度最高的10条返回
        return matchList.stream().sorted(Comparator.comparing(MatchResult::getSimilarity).reversed()).limit(10)
                .map(MatchResult::getStockInfo).collect(Collectors.toList());
    }


    @Override
    public @Nullable MarketAllStockPersistentState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull MarketAllStockPersistentState stockGroupPersistentState) {
        XmlSerializerUtil.copyBean(stockGroupPersistentState, this);
    }
}

package cn.fudoc.trade.core.state;

import cn.fudoc.trade.api.data.StockInfo;
import cn.fudoc.trade.core.state.index.MatchResult;
import cn.fudoc.trade.core.state.index.StockIndex;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
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
     * 每次更新市场股票数据时间
     */
    private Long updateTime;

    /**
     * 大A股票索引
     */
    private StockIndex A;
    /**
     * 香港股票索引
     */
    private StockIndex HK;

    public static MarketAllStockPersistentState getInstance() {
        return ApplicationManager.getApplication().getService(MarketAllStockPersistentState.class);
    }


    public List<StockInfo> match(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return new ArrayList<>();
        }
        if (Objects.isNull(A)) {
            A = new StockIndex(false);
        }
        if (Objects.isNull(HK)) {
            HK = new StockIndex(true);
        }
        List<MatchResult> marketAMatchList = A.match(keyword);
        List<MatchResult> marketHKMatchList = HK.match(keyword);
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

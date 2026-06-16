package cn.fudoc.trade.core.state;

import cn.fudoc.trade.api.ZTApiService;
import cn.fudoc.trade.api.data.StockInfo;
import cn.fudoc.trade.core.state.index.MatchResult;
import cn.fudoc.trade.core.state.index.StockIndex;
import cn.hutool.core.date.DateUtil;
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

import java.util.*;
import java.util.stream.Collectors;

@State(
        name = "fuAllStock",
        storages = @Storage("fu-all-stock.xml")
)
@Getter
@Setter
public class MarketAllStockPersistentState implements PersistentStateComponent<MarketAllStockPersistentState> {
    static List<String> tokenList = Lists.newArrayList(
            "0CB7C441-D071-4AF6-9B8C-BE31BF5E33C0",
            "0E540F1C-806B-41EB-AADF-F3C4BF30054F",
            "0E540F1C-806B-41EB-AADF-F3C4BF30054F",
            "FB45AB6C-02B0-45B7-817E-44C82B5171EA",
            "FB45AB6C-02B0-45B7-817E-44C82B5171EA",
            "F65566C5-624A-4E51-ACC4-DC11132AF6E1"

    );

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

    /**
     * 当天最后一次使用token时间
     */
    private Long todayLastUsedTokenTime;

    /**
     * token使用次数统计
     */
    private Map<String, Integer> tokenUsedMap = new HashMap<>();


    public boolean aDataIsEmpty() {
        if (Objects.isNull(A) || A.isEmpty()) {
            ZTApiService ztApiService = ApplicationManager.getApplication().getService(ZTApiService.class);
            A = new StockIndex(ztApiService.marketA(), false);
            return A.isEmpty();
        }
        return false;
    }


    public String usedToken() {
        if (Objects.isNull(this.todayLastUsedTokenTime) || System.currentTimeMillis() > this.todayLastUsedTokenTime) {
            //新的一天 重置
            this.todayLastUsedTokenTime = DateUtil.endOfDay(new Date()).getTime();
            this.tokenUsedMap.clear();
        }
        for (String token : tokenList) {
            Integer count = tokenUsedMap.get(token);
            if (Objects.isNull(count) || count < 200) {
                count = Objects.isNull(count) ? 1 : ++count;
                tokenUsedMap.put(token, count);
                return token;
            }
        }
        return tokenList.getFirst();
    }



    public static MarketAllStockPersistentState getInstance() {
        return ApplicationManager.getApplication().getService(MarketAllStockPersistentState.class);
    }


    public List<StockInfo> match(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return new ArrayList<>();
        }
        List<MatchResult> matchList = Lists.newArrayList();

        if (Objects.nonNull(A)) {
            matchList.addAll(A.match(keyword));
        }
        if (Objects.nonNull(HK)) {
            matchList.addAll(HK.match(keyword));
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

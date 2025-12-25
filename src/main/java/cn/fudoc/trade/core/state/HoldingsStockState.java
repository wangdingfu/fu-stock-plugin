package cn.fudoc.trade.core.state;

import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import com.google.common.collect.Sets;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * 股票持仓数据
 */
@State(
        name = "fuStockHoldings",
        storages = @Storage("fu-stock-holdings.xml")
)
@Getter
@Setter
public class HoldingsStockState implements PersistentStateComponent<HoldingsStockState> {


    /**
     * key：持仓tab名称（支持多个持仓分组）
     * value: key:股票代码 value：持仓信息
     */
    private Map<String, Map<String, HoldingsInfo>> holdings = new HashMap<>();

    public Set<String> getStockCodes(String group) {
        if (StringUtils.isBlank(group)) {
            return Sets.newHashSet();
        }
        Map<String, HoldingsInfo> holdingsInfoMap = holdings.get(group);
        return Objects.isNull(holdingsInfoMap) ? Sets.newHashSet() : holdingsInfoMap.keySet();
    }

    /**
     * 获取持仓信息
     *
     * @param group     持仓分组
     * @param stockCode 股票代码
     * @return 持仓信息
     */
    public HoldingsInfo getHoldingsInfo(String group, String stockCode) {
        Map<String, HoldingsInfo> stockMap = holdings.get(group);
        if (stockMap == null) {
            return null;
        }
        return stockMap.get(stockCode);
    }

    /**
     * 新增持仓
     *
     * @param group 持仓分组
     * @param code  持仓股票代码
     */
    public void add(String group, String code, HoldingsInfo holdingsInfo) {
        if (StringUtils.isBlank(group) || StringUtils.isBlank(code) || Objects.isNull(holdingsInfo)) {
            return;
        }
        Map<String, HoldingsInfo> codeMap = holdings.computeIfAbsent(group, k -> new HashMap<>());
        codeMap.put(code, holdingsInfo);
    }

    /**
     * 新增持仓
     *
     * @param group 持仓分组
     * @param code  持仓股票代码
     * @param cost  成本价
     * @param count 持仓数量
     */
    public void add(String group, String code, String cost, Integer count) {
        if (StringUtils.isBlank(group) || StringUtils.isBlank(code) || StringUtils.isBlank(code) || Objects.isNull(count)) {
            return;
        }
        Map<String, HoldingsInfo> codeMap = holdings.computeIfAbsent(group, k -> new HashMap<>());
        HoldingsInfo holdingsInfo = new HoldingsInfo();
        holdingsInfo.setCost(cost);
        holdingsInfo.setCount(count);
        codeMap.put(code, holdingsInfo);
    }


    public void remove(String group, String code) {
        if (StringUtils.isBlank(group) || StringUtils.isBlank(code)) {
            return;
        }
        Map<String, HoldingsInfo> holdingsInfoMap = holdings.get(group);
        if (Objects.isNull(holdingsInfoMap)) {
            return;
        }
        holdingsInfoMap.remove(code);
    }


    public static HoldingsStockState getInstance() {
        return ApplicationManager.getApplication().getService(HoldingsStockState.class);
    }

    @Override
    public @Nullable HoldingsStockState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull HoldingsStockState holdingsStockState) {
        XmlSerializerUtil.copyBean(holdingsStockState, this);
    }
}

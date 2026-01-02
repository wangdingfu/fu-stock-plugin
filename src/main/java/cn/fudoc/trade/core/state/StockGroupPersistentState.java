package cn.fudoc.trade.core.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


@State(
        name = "fuStockInfo",
        storages = @Storage("fu-stock-info.xml")
)
@Getter
@Setter
public class StockGroupPersistentState implements PersistentStateComponent<StockGroupPersistentState> {

    /**
     * key：分组 value：股票代码
     */
    private Map<String, Set<String>> stockMap = new HashMap<>();

    public void addGroup(String group) {
        if (StringUtils.isEmpty(group)) {
            return;
        }
        if (this.stockMap.containsKey(group)) {
            return;
        }
        this.stockMap.put(group, new HashSet<>());
    }


    public Set<String> getStockCodes(String group) {
        if (StringUtils.isBlank(group)) {
            return Collections.emptySet();
        }
        return this.stockMap.get(group);
    }

    public void removeGroup(String group) {
        if (StringUtils.isEmpty(group)) {
            return;
        }
        this.stockMap.remove(group);
    }


    public void removeStock(String group, String code) {
        if (StringUtils.isBlank(group) || StringUtils.isBlank(code)) {
            return;
        }
        Set<String> codeSet = this.stockMap.get(group);
        if (CollectionUtils.isNotEmpty(codeSet)) {
            codeSet.remove(code);
        }
    }


    public void addStock(String group, String stockCode) {
        if (StringUtils.isEmpty(group) || StringUtils.isEmpty(stockCode)) {
            return;
        }
        Set<String> codeSet = this.stockMap.get(group);
        if (Objects.isNull(codeSet)) {
            codeSet = new HashSet<>();
            this.stockMap.put(group, codeSet);
        }
        codeSet.add(stockCode);
    }

    public static StockGroupPersistentState getInstance() {
        return ApplicationManager.getApplication().getService(StockGroupPersistentState.class);
    }

    @Override
    public @Nullable StockGroupPersistentState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull StockGroupPersistentState stockGroupPersistentState) {
        XmlSerializerUtil.copyBean(stockGroupPersistentState, this);
    }
}

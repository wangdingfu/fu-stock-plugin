package cn.fudoc.trade.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;


@State(
        name = "fuTradeGroup",
        storages = @Storage("fu-trade-group.xml")
)
@Getter
@Setter
public class StockGroupPersistentState implements PersistentStateComponent<StockGroupPersistentState> {

    /**
     * 是否每隔3秒刷新股票
     */
    private boolean autoRefresh;
    /**
     * key：分组 value：股票代码
     */
    private Map<String, Set<String>> stockMap;

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

package cn.fudoc.trade.core.state;

import cn.fudoc.trade.core.common.enumtype.StockTabEnum;
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


@State(
        name = "fuStockGroup",
        storages = @Storage("fu-stock-group.xml")
)
@Getter
@Setter
public class StockGroupState implements PersistentStateComponent<StockGroupState> {


    private Map<String, StockTabEnum> stockTabEnumMap = new LinkedHashMap<>();

    public void add(String group, StockTabEnum stockTabEnum) {
        if (StringUtils.isBlank(group) || Objects.isNull(stockTabEnum)) {
            return;
        }
        stockTabEnumMap.put(group, stockTabEnum);
    }




    public static StockGroupState getInstance() {
        return ApplicationManager.getApplication().getService(StockGroupState.class);
    }

    @Override
    public @Nullable StockGroupState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull StockGroupState stockGroupPersistentState) {
        XmlSerializerUtil.copyBean(stockGroupPersistentState, this);
    }
}

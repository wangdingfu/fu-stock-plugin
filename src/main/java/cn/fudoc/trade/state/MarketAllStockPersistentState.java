package cn.fudoc.trade.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "fuAllStock",
        storages = @Storage("fu-all-stock.xml")
)
@Getter
@Setter
public class MarketAllStockPersistentState implements PersistentStateComponent<MarketAllStockPersistentState> {

    //code,首字母,中文

    @Override
    public @Nullable MarketAllStockPersistentState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull MarketAllStockPersistentState stockGroupPersistentState) {
        XmlSerializerUtil.copyBean(stockGroupPersistentState, this);
    }
}

package cn.fudoc.trade.core.state;

import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.core.state.pojo.TradeRateInfo;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * 股票持仓数据
 */
@State(
        name = "fuStockSettings",
        storages = @Storage("fu-stock-settings.xml")
)
@Getter
@Setter
public class FuStockSettingState implements PersistentStateComponent<FuStockSettingState> {

    /**
     * key：持仓tab名称
     * value：手续费
     */
    private Map<String, TradeRateInfo> rateInfoMap = new HashMap<>();


    public void addRate(String group,TradeRateInfo tradeRateInfo){
        rateInfoMap.put(group,tradeRateInfo);
    }

    public TradeRateInfo getRate(String group){
        return rateInfoMap.get(group);
    }

    public static FuStockSettingState getInstance() {
        return ApplicationManager.getApplication().getService(FuStockSettingState.class);
    }

    @Override
    public @Nullable FuStockSettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull FuStockSettingState holdingsStockState) {
        XmlSerializerUtil.copyBean(holdingsStockState, this);
    }
}

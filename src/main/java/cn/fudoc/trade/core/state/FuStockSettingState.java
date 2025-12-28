package cn.fudoc.trade.core.state;

import cn.fudoc.trade.core.state.pojo.TradeRateInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


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


    public void addRate(String group, TradeRateInfo tradeRateInfo) {
        rateInfoMap.put(group, tradeRateInfo);
    }

    public TradeRateInfo getRate(String group) {
        return rateInfoMap.get(group);
    }

    public TradeRateInfo getRateAndCreate(String group) {
        TradeRateInfo rate = getRate(group);
        if (Objects.isNull(rate)) {
            rate = createDefaultTradeRateInfo();
            rateInfoMap.put(group, rate);
        }
        return rate;
    }

    public TradeRateInfo createDefaultTradeRateInfo() {
        TradeRateInfo rate = new TradeRateInfo();
        rate.setMin5(true);
        rate.setCommissionRate("0.00025");
        rate.setStampDutyRate("0.0005");
        rate.setTransferRate("0");
        rate.setOtherRate("0");
        rate.setOtherFee("0");
        return rate;
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

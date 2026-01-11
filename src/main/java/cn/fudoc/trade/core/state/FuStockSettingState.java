package cn.fudoc.trade.core.state;

import cn.fudoc.trade.core.state.pojo.TradeRateInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * key：应用的地方 例如 分组名称 股票名称
     * value： key：中文。value：英文/自定义隐蔽标识
     */
    private Map<String, Map<String, String>> cnMappingMap = new HashMap<>();

    public void clearCnMapping() {
        cnMappingMap.clear();
    }


    public void add(String group, String cn, String en) {
        if (StringUtils.isBlank(group) || StringUtils.isBlank(cn) || StringUtils.isBlank(en)) {
            return;
        }
        Map<String, String> mappingMap = cnMappingMap.get(group);
        if (Objects.isNull(mappingMap)) {
            mappingMap = new HashMap<>();
            cnMappingMap.put(group, mappingMap);
        }
        mappingMap.put(cn, en);
    }


    public String mapping(String group, String cn) {
        if (StringUtils.isBlank(group) || StringUtils.isBlank(cn)) {
            return null;
        }
        Map<String, String> mappingMap = cnMappingMap.get(group);
        if (MapUtils.isEmpty(mappingMap)) {
            return null;
        }
        return mappingMap.get(cn);
    }


    public void addRate(String group, TradeRateInfo tradeRateInfo) {
        rateInfoMap.put(group, tradeRateInfo);
    }

    public TradeRateInfo getRate(String group) {
        return rateInfoMap.get(group);
    }


    public TradeRateInfo createDefaultTradeRateInfo() {
        TradeRateInfo rate = new TradeRateInfo();
        rate.setMinFee("5");
        rate.setCommissionRate("0");
        rate.setStampDutyRate("0.0005");
        rate.setTransferSHRate("0.00001");
        rate.setTransferSZRate("0.00001");
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

package cn.fudoc.trade.core.state;

import cn.fudoc.trade.core.common.enumtype.GroupTypeEnum;
import cn.fudoc.trade.core.state.pojo.StockGroupInfo;
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
        name = "fuStockGroup",
        storages = @Storage("fu-stock-group.xml")
)
@Getter
@Setter
public class StockGroupState implements PersistentStateComponent<StockGroupState> {


    private Map<String, GroupTypeEnum> stockTabEnumMap = new LinkedHashMap<>();


    /**
     * 股票分组集合
     */
    private List<StockGroupInfo> groupInfoList = new ArrayList<>();


    public void add(StockGroupInfo stockGroupInfo) {
        if (StringUtils.isBlank(stockGroupInfo.getGroupName()) || StringUtils.isBlank(stockGroupInfo.getHideGroupName()) || Objects.isNull(stockGroupInfo.getGroupType())) {
            return;
        }
        groupInfoList.add(stockGroupInfo);
    }

    /**
     * 持仓分组集合
     */
    public Set<String> holdingsGroups() {
        return groupInfoList.stream().filter(f -> GroupTypeEnum.STOCK_HOLD.equals(f.getGroupType())).map(StockGroupInfo::getGroupName).collect(Collectors.toSet());
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

package cn.fudoc.trade.state;

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


/**
 * 股票持仓数据
 */
@State(
        name = "fuCommonSetting",
        storages = @Storage("fu-common-setting.xml")
)
@Getter
@Setter
public class FuCommonState implements PersistentStateComponent<FuCommonState> {

    private Map<String, Boolean> flagMap = new HashMap<>();


    public Boolean is(String flag) {
        if (StringUtils.isBlank(flag)) {
            return false;
        }
        return flagMap.getOrDefault(flag, false);
    }


    public Boolean set(String flag, Boolean value) {
        if (StringUtils.isBlank(flag)) {
            return false;
        }
        return flagMap.put(flag, value);
    }

    public static FuCommonState getInstance() {
        return ApplicationManager.getApplication().getService(FuCommonState.class);
    }

    @Override
    public @Nullable FuCommonState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull FuCommonState fuCommonState) {
        XmlSerializerUtil.copyBean(fuCommonState, this);
    }
}

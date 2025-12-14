package cn.fudoc.trade.core.startup;

import cn.fudoc.trade.api.ZTApiService;
import cn.fudoc.trade.core.state.MarketAllStockPersistentState;
import cn.fudoc.trade.core.state.index.StockIndex;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * IDE 启动后执行的初始化逻辑（注册各类监听器）
 */
public class FuStockStartupActivity implements ProjectActivity {
    // 标记：确保监听器只注册一次（避免重复注册）
    private static boolean isInitialized = false;
    private static final Long ONE_DAY = 24 * 60 * 60 * 1000L;


    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        // 1. 仅初始化一次（避免多项目场景下重复注册）
        if (isInitialized) return CompletableFuture.completedFuture(null);
        isInitialized = true;
        return CompletableFuture.runAsync(() -> {
            MarketAllStockPersistentState instance = MarketAllStockPersistentState.getInstance();
            Long updateTime = instance.getUpdateTime();
            if (Objects.isNull(updateTime) || (updateTime + ONE_DAY) < System.currentTimeMillis()) {
                //触发更新
                ZTApiService ztApiService = ApplicationManager.getApplication().getService(ZTApiService.class);
                instance.setA(new StockIndex(ztApiService.marketA(), false));
                instance.setHK(new StockIndex(ztApiService.marketHK(), true));
                instance.setUpdateTime(System.currentTimeMillis());
            }
        });
    }
}
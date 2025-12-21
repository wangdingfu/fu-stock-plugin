package cn.fudoc.trade.toolwindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class FuStockToolWindowFactory implements ToolWindowFactory, DumbAware {

    // 缓存窗口上一次的可见状态，避免重复触发
    private final AtomicBoolean lastVisibleState = new AtomicBoolean(false);
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        FuStockWindow fuStockWindow = new FuStockWindow(project);
        // 将面板添加到工具窗口
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content content = contentFactory.createContent(fuStockWindow, "", false);
        toolWindow.getContentManager().addContent(content);

        project.getMessageBus().connect().subscribe(ToolWindowManagerListener.TOPIC, new ToolWindowManagerListener() {

            @Override
            public void toolWindowShown(@NotNull ToolWindow toolWindow) {
                if ("FuStock".equals(toolWindow.getId()) && toolWindow.isVisible()) {
                    fuStockWindow.showWindow();
                }
            }

            @Override
            public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {
                // 获取目标 ToolWindow（通过 id 匹配）
                ToolWindow myToolWindow = toolWindowManager.getToolWindow("FuStock");
                if (myToolWindow == null) return;
                // 获取当前可见状态
                boolean currentVisible = myToolWindow.isVisible();
                // 对比上一次状态，只有状态变化时才触发逻辑（防抖）
                if (currentVisible != lastVisibleState.get()) {
                    lastVisibleState.set(currentVisible);
                    if (!currentVisible) {
                        // 窗口从显示→隐藏（这是你要的核心逻辑）
                        fuStockWindow.stopTask();
                    }
                }
            }
        });
        // 初始化时检查一次状态（防止窗口默认显示但未触发监听）
        if (toolWindow.isVisible()) {
            lastVisibleState.set(true);
        }
    }
}

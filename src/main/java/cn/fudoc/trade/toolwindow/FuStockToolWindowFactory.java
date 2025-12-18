package cn.fudoc.trade.toolwindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class FuStockToolWindowFactory implements ToolWindowFactory, DumbAware {
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
                //当前窗口被影藏
                fuStockWindow.stopTask();
            }
        });
    }
}

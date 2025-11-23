package cn.fudoc.trade.view.toolwindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class FuTradeToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        FuTradeWindow fuTradeWindow = new FuTradeWindow(project, toolWindow);
        // 将面板添加到工具窗口
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(fuTradeWindow, "", false);
        toolWindow.getContentManager().addContent(content);
        project.getMessageBus().connect().subscribe(ToolWindowManagerListener.TOPIC, new ToolWindowManagerListener() {

            @Override
            public void toolWindowShown(@NotNull ToolWindow toolWindow) {
                System.out.println("toolWindowShown==========="+toolWindow.getId());
                if("Fu Trade".equals(toolWindow.getId()) && toolWindow.isVisible() && fuTradeWindow.getIsExecute().get()) {
                    fuTradeWindow.autoSelectedTab();
                }
            }

            @Override
            public void stateChanged(@NotNull ToolWindowManager toolWindowManager, @NotNull ToolWindowManagerEventType changeType) {
                if (ToolWindowManagerEventType.HideToolWindow != changeType) {
                    return;
                }
                // 获取目标 ToolWindow（通过 id 匹配）
                ToolWindow myToolWindow = toolWindowManager.getToolWindow("Fu Trade");
                if (myToolWindow == null) return;
                //当前窗口被影藏
                System.out.println("stateChanged==========="+changeType);
                fuTradeWindow.stopTask();
            }
        });
    }
}

package cn.fudoc.trade.util;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;

import javax.swing.*;

/**
 * @author wangdingfu
 * @date 2022-09-17 18:57:30
 */
public class ToolBarUtils {



    /**
     * 将一组动作添加到工具栏中展示
     *
     * @param toolBarPanel 工具栏面板
     * @param place        工具栏面板别名
     * @param actionGroup  工具栏动作分组
     * @param layout       工具栏面板布局
     */
    public static void addActionToToolBar(JPanel toolBarPanel, String place, ActionGroup actionGroup, String layout) {
        toolBarPanel.add(addActionToToolBar(toolBarPanel, place, actionGroup), layout);
    }


    public static JComponent addActionToToolBar(JComponent targetComponent, String place, ActionGroup actionGroup) {
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar(place, actionGroup, true);
        toolbar.setTargetComponent(targetComponent);
        toolbar.getComponent().setBackground(targetComponent.getBackground());
        return toolbar.getComponent();
    }
}

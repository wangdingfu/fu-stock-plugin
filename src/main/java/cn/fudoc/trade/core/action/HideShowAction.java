package cn.fudoc.trade.core.action;

import cn.fudoc.trade.core.state.FuCommonState;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import icons.FuIcons;
import org.jetbrains.annotations.NotNull;

/**
 * @author wangdingfu
 * @date 2022-09-17 18:50:14
 */
public class HideShowAction extends AnAction {

    private final FuCommonState instance;
    private final HideShowCallback hideShowCallback;

    public HideShowAction(HideShowCallback hideShowCallback) {
        super(hideShowCallback.getShowText(), hideShowCallback.getShowText(), AllIcons.General.Pin_tab);
        this.hideShowCallback = hideShowCallback;
        this.instance = FuCommonState.getInstance();
    }


    @Override
    public boolean isDumbAware() {
        return true;
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        // 更新图标和描述
        boolean isVisible = !instance.is(hideShowCallback.getKey());
        presentation.setIcon(isVisible ? AllIcons.General.InspectionsEye : FuIcons.FU_UNSHARE);
        presentation.setText(isVisible ? hideShowCallback.getShowText() : hideShowCallback.getHideText());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        // 更新图标和描述
        boolean isVisible = instance.is(hideShowCallback.getKey());
        presentation.setIcon(isVisible ? AllIcons.General.InspectionsEye : FuIcons.FU_UNSHARE);
        presentation.setText(isVisible ? hideShowCallback.getShowText() : hideShowCallback.getHideText());
        instance.set(hideShowCallback.getKey(), !isVisible);

        hideShowCallback.callback(!isVisible);
    }

}

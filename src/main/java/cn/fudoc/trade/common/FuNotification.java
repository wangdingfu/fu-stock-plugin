package cn.fudoc.trade.common;

import cn.fudoc.trade.util.ProjectUtils;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class FuNotification {

    private static final NotificationGroup NOTIFICATION_GROUP = NotificationGroupManager.getInstance().getNotificationGroup(FuTradeConstants.FU_TRADE_NOTIFICATION_GROUP);


    public static void notifyWarning(String content, Project project) {
        NOTIFICATION_GROUP.createNotification(FuTradeConstants.FU_TRADE, content, NotificationType.WARNING).notify(project);
    }

    public static void notifyWarning(String content) {
        NOTIFICATION_GROUP.createNotification(FuTradeConstants.FU_TRADE, content, NotificationType.WARNING).notify(ProjectUtils.getCurrProject());
    }
}

package cn.fudoc.trade.toolwindow;

import cn.fudoc.trade.core.common.FuBundle;
import cn.fudoc.trade.core.common.FuNotification;
import cn.fudoc.trade.core.common.FuTradeConstants;
import cn.fudoc.trade.core.common.enumtype.StockTabEnum;
import cn.fudoc.trade.core.common.enumtype.UpdateTipTagEnum;
import cn.fudoc.trade.core.state.FuCommonState;
import cn.fudoc.trade.core.state.StockGroupState;
import cn.fudoc.trade.core.timer.ScheduledTaskManager;
import cn.fudoc.trade.util.ToolBarUtils;
import cn.fudoc.trade.view.FuIndexView;
import cn.fudoc.trade.view.FuStockTabView;
import cn.fudoc.trade.view.GroupAddDialog;
import cn.fudoc.trade.view.FuStockSearchPopupView;
import cn.fudoc.trade.view.settings.FuStockSettingDialog;
import cn.fudoc.trade.view.table.StockTableView;
import cn.hutool.core.date.DateUtil;
import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.util.ui.HTMLEditorKitBuilder;
import com.intellij.util.ui.UIUtil;
import icons.FuIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class FuStockWindow extends SimpleToolWindowPanel implements DataProvider {

    private static final String ADD_STOCK_GROUP_TITLE = FuBundle.message("add.stock.group.title");
    private static final String ADD_STOCK_TITLE = FuBundle.message("add.stock.title");
    private static final String STOCK_AUTO_LOAD_TITLE = FuBundle.message("stock.auto.load.title");
    private static final String STOCK_AUTO_LOAD_TIME_TITLE = FuBundle.message("stock.auto.load.time.tip");


    /**
     * 指数栏视图
     */
    private final FuIndexView indexView;
    /**
     * 股票栏视图
     */
    private final FuStockTabView stockView;

    /**
     * 消息栏视图
     */
//    private final JEditorPane messagePane;

    /**
     * 任务调度管理 主要保证每隔固定时间刷新股票实时信息
     */
    private final ScheduledTaskManager scheduledTaskManager = new ScheduledTaskManager();
    /**
     * 上一次实时加载时间
     */
    private Long lastReloadTime = 0L;
    /**
     * 股票信息是否实时刷新
     */
    private final AtomicBoolean isAutoLoad = new AtomicBoolean(false);

    /**
     * 股票分组持久化
     */
    private final StockGroupState stockGroupState = StockGroupState.getInstance();
    /**
     * 公共持久化数据
     */
    private final FuCommonState fuCommonState = FuCommonState.getInstance();

    /**
     * 股票刷新时间
     */
    private final Map<String, Date> refreshTimeMap = new ConcurrentHashMap<>();

    /**
     * 窗体由四部分组成
     * 第一部分：动作栏，主要展示一些操作图标，用于添加股票分组，添加股票，刷新股票，启动/定制任务实时获取股票等操作
     * 第二部分：指数栏，主要展示上证指数，创业板指数信息
     * 第三部分：股票栏，主要展示自选股票实时信息以及自定义分组管理股票
     * 第四部分：收益栏，主要展示我今日的收益信息
     * 第五部分：消息栏，主要展示一些炒股组训，炒股纪律等文案
     *
     * @param project 当前项目
     */
    public FuStockWindow(Project project) {
        super(true, true);
        JPanel rootPanel = new JPanel(new BorderLayout());
        //1、动作栏
        rootPanel.add(initToolBarUI(project), BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new BorderLayout());
        //2、指数面板
        this.indexView = new FuIndexView();
        contentPanel.add(this.indexView, BorderLayout.NORTH);
        //3、股票面板
        this.stockView = new FuStockTabView(project);
        contentPanel.add(this.stockView.getComponent(), BorderLayout.CENTER);
        rootPanel.add(contentPanel, BorderLayout.CENTER);

//        //5、消息栏
//        this.messagePane = initPanel();
//        rootPanel.add(ScrollPaneFactory.createScrollPane(this.messagePane), BorderLayout.SOUTH);

        //设置当前面板到窗口
        setContent(rootPanel);
        //随机展示炒股心灵鸡汤文案
        showTips();
        //初始化持仓
        initGroup();
    }


    private void initGroup() {
        stockView.add(FuTradeConstants.MY_SELECTED_GROUP, StockTabEnum.STOCK_INFO);
        stockView.add(FuTradeConstants.MY_POSITIONS_GROUP, StockTabEnum.STOCK_HOLD);

        //加载持久化的分组
        stockGroupState.getStockTabEnumMap().forEach(stockView::add);

        //是否自动刷新
        isAutoLoad.set(fuCommonState.is(FuTradeConstants.CommonStateKey.STOCK_AUTO_REFRESH));

        //默认选中我的自选
        stockView.selectMySelected(FuTradeConstants.MY_SELECTED_GROUP);
    }


    /**
     * 窗口展示时触发事件
     */
    public void showWindow() {
        if (isAutoLoad.get() && System.currentTimeMillis() - lastReloadTime > 3000) {
            //代表是启用自动刷新  但实际未自动刷新
            startTimeTask();
        }
    }


    /**
     * 关闭定时器
     */
    public void stopTask() {
        if (scheduledTaskManager.isRunning()) {
            scheduledTaskManager.stopTask();
        }
    }


    /**
     * 随机展示消息
     */
    private void showTips() {
//        this.messagePane.setText("");
    }


    /**
     * 状态栏面板初始化
     */
    private JPanel initToolBarUI(Project project) {
        JPanel toolBarPanel = new JPanel(new BorderLayout());
        Utils.setSmallerFontForChildren(toolBarPanel);
        //创建及初始化工具栏
        ToolBarUtils.addActionToToolBar(toolBarPanel, "add.stock.toolBar", initActionGroup(project), BorderLayout.WEST);
        return toolBarPanel;
    }

    /**
     * 状态栏动作初始化
     */
    private DefaultActionGroup initActionGroup(Project project) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new AnAction(ADD_STOCK_GROUP_TITLE, "", FuIcons.FU_ADD_GROUP) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                GroupAddDialog groupAddDialog = new GroupAddDialog(project);
                if (groupAddDialog.showAndGet()) {
                    stockView.add(groupAddDialog.getGroupName(), groupAddDialog.getStockTabEnum());
                    stockGroupState.add(groupAddDialog.getGroupName(), groupAddDialog.getStockTabEnum());
                }
            }
        });
        actionGroup.add(new AnAction(ADD_STOCK_TITLE, "", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                StockTableView stockTableView = stockView.getSelected();
                if (Objects.isNull(stockTableView)) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    FuStockSearchPopupView fuStockSearchPopupView = new FuStockSearchPopupView(stockTableView);
                    fuStockSearchPopupView.showPopup(project);
                });

            }
        });
        //启动定时刷新股票
        actionGroup.add(new DumbAwareAction(STOCK_AUTO_LOAD_TITLE, "", AllIcons.Actions.Execute) {
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                Presentation presentation = e.getPresentation();
                presentation.setEnabled(!isAutoLoad.get());
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                boolean isStart = startTimeTask();
                if (!isStart) {
                    //没有启动成功 则提示
                    updateTag(UpdateTipTagEnum.OPEN_AFTER_AUTO_REFRESH.getTag());
                    FuNotification.notifyWarning(STOCK_AUTO_LOAD_TIME_TITLE, project);
                }
                isAutoLoad.set(true);
                //是否自动刷新 持久化
                fuCommonState.set(FuTradeConstants.CommonStateKey.STOCK_AUTO_REFRESH, true);
            }
        });
        //停止定时刷新股票
        actionGroup.addAction(new DumbAwareAction("停止刷新", "", AllIcons.Actions.Suspend) {
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                Presentation presentation = e.getPresentation();
                presentation.setEnabled(isAutoLoad.get());
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                stopTask();
                isAutoLoad.set(false);
                updateTag(UpdateTipTagEnum.CLOSE_AUTO_REFRESH.getTag());
                //是否自动刷新 持久化
                fuCommonState.set(FuTradeConstants.CommonStateKey.STOCK_AUTO_REFRESH, false);
            }
        });

        //手动刷新股票
        actionGroup.addAction(new DumbAwareAction("刷新", "", AllIcons.Actions.Refresh) {
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }


            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                reloadStock(false);
                updateTag(UpdateTipTagEnum.MANUAL_REFRESH.getTag());
            }
        });
        //摸鱼模式 TODO 下一版本开发
//        actionGroup.add(new HideShowAction(new DefaultHideShowCallback() {
//            @Override
//            public void callback(boolean isShow) {
//                System.out.println(isShow);
//            }
//        }));

        //基础信息设置
        actionGroup.addAction(new DumbAwareAction("设置", "", AllIcons.General.Settings) {
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                FuStockSettingDialog fuStockSettingDialog = new FuStockSettingDialog(e.getProject());
                fuStockSettingDialog.showAndGet();
            }
        });
        return actionGroup;
    }


    private boolean startTimeTask() {
        if (isCanStart(new Date())) {
            scheduledTaskManager.startTask(() -> reloadStock(true));
            lastReloadTime = System.currentTimeMillis();
            return true;
        }
        //开启侧边栏时 即使超出开盘时间 也默认刷新一次
        reloadStock(true);
        return false;
    }


    /**
     * 重新加载股票信息
     */
    private void reloadStock(boolean isAutoLoad) {
        StockTableView selected = this.stockView.getSelected();
        if (Objects.isNull(selected)) {
            return;
        }

        if (isAutoLoad) {
            //自动加载才判断 手动刷新无需判断
            Date refreshTime = refreshTimeMap.get(selected.getTabName());
            if (Objects.isNull(refreshTime) || !isCanStart(refreshTime)) {
                //上一次刷新时间不在盘中 则不需要刷新
                return;
            }
        }
        //刷新股票
        selected.reloadAllStock();
        refreshTimeMap.put(selected.getTabName(), new Date());
    }


    private void updateTag(String tag) {
        StockTableView stockSelected = this.stockView.getSelected();
        if (Objects.nonNull(stockSelected)) {
            stockSelected.updateTipTag(tag);
        }
    }

    private boolean isCanStart(Date date) {
        //判断当前时间是否开盘时间  不是则不提交任务 自动刷新时间段定位9:15 ~ 11:30 13:00~15:00
        int hour = DateUtil.hour(date, true);
        if (hour < 9 || hour > 15 || hour == 12) {
            return false;
        }
        int minute = DateUtil.minute(date);
        if (hour == 9 && minute <= 15) {
            return false;
        }
        if (hour == 11 && minute > 30) {
            return false;
        }
        if (hour == 15 && minute > 0) {
            return false;
        }
        //TODO 节假日判断
        return !DateUtil.isWeekend(date);
    }


    private JEditorPane initPanel() {
        JEditorPane messagePanel = new JEditorPane();
        messagePanel.setContentType("text/html");
        messagePanel.setEditable(false);
        messagePanel.setEditorKit(HTMLEditorKitBuilder.simple());
        messagePanel.addHyperlinkListener(BrowserHyperlinkListener.INSTANCE);
        UIUtil.doNotScrollToCaret(messagePanel);
        return messagePanel;
    }

}

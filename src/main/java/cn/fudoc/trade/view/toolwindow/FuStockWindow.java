package cn.fudoc.trade.view.toolwindow;

import cn.fudoc.trade.api.TencentApiService;
import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.common.FuBundle;
import cn.fudoc.trade.common.FuNotification;
import cn.fudoc.trade.common.FuTradeConstants;
import cn.fudoc.trade.common.StockTabEnum;
import cn.fudoc.trade.state.StockGroupPersistentState;
import cn.fudoc.trade.util.ToolBarUtils;
import cn.fudoc.trade.view.ScheduledTaskManager;
import cn.fudoc.trade.view.search.FuStockSearchPopupView;
import cn.fudoc.trade.view.stock.StockTabView;
import cn.fudoc.trade.view.FuIndexView;
import cn.fudoc.trade.view.FuStockInfoView;
import cn.hutool.core.date.DateUtil;
import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.HTMLEditorKitBuilder;
import com.intellij.util.ui.UIUtil;
import icons.FuIcons;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class FuStockWindow extends SimpleToolWindowPanel implements DataProvider {

    private static final String ADD_STOCK_GROUP_TITLE = FuBundle.message("add.stock.group.title");
    private static final String ADD_STOCK_GROUP_MESSAGE = FuBundle.message("add.stock.group.message");
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
    private final FuStockInfoView stockView;
    /**
     * 交易栏视图
     */
    private final FuStockInfoView tradeView;
    /**
     * 消息栏视图
     */
    private final JEditorPane messagePane;

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
     * 获取股票实时信息接口实现
     */
    private final TencentApiService tencentApiService = ApplicationManager.getApplication().getService(TencentApiService.class);

    /**
     * 窗体由四部分组成
     * 第一部分：动作栏，主要展示一些操作图标，用于添加股票分组，添加股票，刷新股票，启动/定制任务实时获取股票等操作
     * 第二部分：指数栏，主要展示上证指数，创业板指数信息
     * 第三部分：股票栏，主要展示自选股票实时信息以及自定义分组管理股票
     * 第四部分：交易栏，主要展示我的持仓交易信息
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
        Splitter splitter = new Splitter(true, 0.6F);
        this.stockView = new FuStockInfoView(project, StockTabEnum.STOCK_INFO);
        splitter.setFirstComponent(this.stockView.getComponent());
        //4、交易栏
        this.tradeView = new FuStockInfoView(project, StockTabEnum.STOCK_HOLD);
        splitter.setSecondComponent(this.tradeView.getComponent());
        contentPanel.add(splitter, BorderLayout.CENTER);
        rootPanel.add(contentPanel, BorderLayout.CENTER);
        //5、消息栏
        this.messagePane = initPanel();
        rootPanel.add(ScrollPaneFactory.createScrollPane(this.messagePane), BorderLayout.SOUTH);
        //设置当前面板到窗口
        setContent(rootPanel);
        //随机展示炒股心灵鸡汤文案
        showTips();
        //初始化持仓
        initGroup();
    }


    private void initGroup() {
        stockView.add(FuTradeConstants.MY_SELECTED_GROUP);
        tradeView.add(FuTradeConstants.MY_POSITIONS_GROUP);
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
        this.messagePane.setText("");
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
                String groupName = Messages.showInputDialog(project, ADD_STOCK_GROUP_MESSAGE, ADD_STOCK_GROUP_TITLE, IconUtil.getAddIcon(), "我的分组", null);
                if (StringUtils.isBlank(groupName)) {
                    return;
                }
                stockView.add(groupName);

            }
        });
        actionGroup.add(new AnAction(ADD_STOCK_TITLE, "", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                StockTabView stockTabView = stockView.getSelected();
                if (Objects.isNull(stockTabView)) {
                    return;
                }
                FuStockSearchPopupView fuStockSearchPopupView = new FuStockSearchPopupView(stockTabView);
                fuStockSearchPopupView.showPopup(project);
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
                    FuNotification.notifyWarning(STOCK_AUTO_LOAD_TIME_TITLE, project);
                }
                isAutoLoad.set(true);
                StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
                instance.setAutoRefresh(isAutoLoad.get());
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
                StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
                instance.setAutoRefresh(isAutoLoad.get());
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
                reloadStock();
            }
        });
        return actionGroup;
    }


    private boolean startTimeTask() {
        if (isCanStart()) {
            scheduledTaskManager.startTask(this::reloadStock);
            lastReloadTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }


    /**
     * 重新加载股票信息
     */
    private void reloadStock() {
        //指数栏单独加载
        indexView.reload();

        //股票信息合并加载
        StockTabView stockSelected = this.stockView.getSelected();
        StockTabView tradeSelected = this.tradeView.getSelected();
        Set<String> stockCodes = Objects.isNull(stockSelected) ? Collections.emptySet() : stockSelected.getStockCodes();
        Set<String> tradeCodes = Objects.isNull(tradeSelected) ? Collections.emptySet() : tradeSelected.getStockCodes();

        Set<String> allCodes = new HashSet<>();
        allCodes.addAll(stockCodes);
        allCodes.addAll(tradeCodes);

        List<RealStockInfo> realStockInfos = tencentApiService.stockList(allCodes);
        Map<String, RealStockInfo> map = new HashMap<>(realStockInfos.size());
        realStockInfos.forEach(stockInfo -> map.put(stockInfo.getStockCode(), stockInfo));
        if (CollectionUtils.isNotEmpty(stockCodes) && Objects.nonNull(stockSelected)) {
            stockSelected.initStockList(stockCodes.stream().map(map::get).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(tradeCodes) && Objects.nonNull(tradeSelected)) {
            tradeSelected.initStockList(tradeCodes.stream().map(map::get).filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }


    private boolean isCanStart() {
        //判断当前时间是否开盘时间  不是则不提交任务 自动刷新时间段定位9:15 ~ 11:30 13:00~15:00
        Date now = new Date();
        int hour = DateUtil.hour(now, true);
        if (hour < 9 || hour > 15 || hour == 12) {
            return false;
        }
        int minute = DateUtil.minute(now);
        if (hour == 9 && minute < 15) {
            return false;
        }
        if (hour == 11 && minute > 30) {
            return false;
        }
        //TODO 节假日判断
        return !DateUtil.isWeekend(now);
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

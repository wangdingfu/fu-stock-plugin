package cn.fudoc.trade.view.toolwindow;

import cn.fudoc.trade.common.FuBundle;
import cn.fudoc.trade.common.FuNotification;
import cn.fudoc.trade.common.StockTabEnum;
import cn.fudoc.trade.state.StockGroupPersistentState;
import cn.fudoc.trade.util.ToolBarUtils;
import cn.fudoc.trade.view.search.FuStockSearchPopupView;
import cn.fudoc.trade.view.stock.StockTabFactory;
import cn.fudoc.trade.view.stock.StockTabView;
import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.HTMLEditorKitBuilder;
import com.intellij.util.ui.UIUtil;
import icons.FuIcons;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class FuStockWindow extends SimpleToolWindowPanel implements DataProvider {

    private static final String ADD_STOCK_GROUP_TITLE = FuBundle.message("add.stock.group.title");
    private static final String ADD_STOCK_GROUP_MESSAGE = FuBundle.message("add.stock.group.message");
    private static final String ADD_STOCK_TITLE = FuBundle.message("add.stock.title");
    private static final String ADD_STOCK_MESSAGE = FuBundle.message("add.stock.message");
    private static final String STOCK_AUTO_LOAD_TITLE = FuBundle.message("stock.auto.load.title");
    private static final String STOCK_AUTO_LOAD_TIME_TITLE = FuBundle.message("stock.auto.load.time.tip");
    private static final String REMOVE_STOCK_GROUP_TITLE = FuBundle.message("remove.stock.group.title");

    /**
     * 股票tab
     */
    private JBTabs stockTabs;
    /**
     * 持仓交易tab
     */
    private JBTabs tradeTabs;

    /**
     * 提示栏
     */
    private JEditorPane messagePane;

    private Long lastRefreshTime = 0L;
    /**
     * 股票信息是否实时刷新
     */
    private final AtomicBoolean isAutoLoad = new AtomicBoolean(false);
    /**
     * 股票栏 key：股票栏名称 value：股票栏操作对象
     */
    private final Map<String, StockTabView> stockTabViewMap = new HashMap<>();

    /**
     * 当前选中的tab视图
     */
    private StockTabView currentSelected;

    /**
     * 窗体由四部分组成
     * 第一部分：动作栏，主要展示一些操作图标，用于添加股票分组，添加股票，刷新股票，启动/定制任务实时获取股票等操作
     * 第二部分：指数栏，主要展示上证指数，创业板指数信息
     * 第三部分：股票栏，主要展示自选股票实时信息以及自定义分组管理股票
     * 第四部分：交易栏，主要展示我的持仓交易信息
     * 第五部分：提示栏，主要展示一些炒股组训，炒股纪律等文案
     *
     * @param project 当前项目
     */
    public FuStockWindow(Project project) {
        super(Boolean.TRUE, Boolean.TRUE);
        //初始化窗口UI
        initUI(project);
        //初始化监听器
        initListener();
        //初始化持久化数据
        initData();
    }


    private void initUI(Project project) {
        JPanel rootPanel = new JPanel(new BorderLayout());
        //1、动作栏
        rootPanel.add(initToolBarUI(project), BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new BorderLayout());
        //2、指数面板
        JPanel indexPanel = new JPanel(new FlowLayout());
        contentPanel.add(indexPanel, BorderLayout.NORTH);
        //3、股票面板
        Splitter splitter = new Splitter(true, 0.6F);
        stockTabs = JBTabsFactory.createTabs(project);
        splitter.setFirstComponent(stockTabs.getComponent());
        //4、交易栏
        tradeTabs = JBTabsFactory.createTabs(project);
        splitter.setSecondComponent(tradeTabs.getComponent());
        contentPanel.add(splitter, BorderLayout.CENTER);
        rootPanel.add(contentPanel, BorderLayout.CENTER);
        //5、提示栏
        this.messagePane = initPanel();
        rootPanel.add(ScrollPaneFactory.createScrollPane(this.messagePane), BorderLayout.SOUTH);
        //设置当前面板到窗口
        setContent(rootPanel);
    }

    private void initListener() {
        stockTabs.addListener(new TabsListener() {
            @Override
            public void selectionChanged(TabInfo oldSelection, TabInfo newSelection) {
                if (Objects.nonNull(oldSelection)) {
                    StockTabView stockTabView = stockTabViewMap.get(oldSelection.getText());
                    if (Objects.nonNull(stockTabView)) {
                        stockTabView.stopTask();
                    }
                }
                //切换新窗口时 判断当前是否开启自动刷新 开启时才刷新股票数据
                if (Objects.nonNull(newSelection)) {
                    currentSelected = stockTabViewMap.get(newSelection.getText());
                    if (Objects.nonNull(currentSelected) && isAutoLoad.get()) {
                        //添加新窗口时 默认启动刷新
                        currentSelected.startTask();
                        lastRefreshTime = System.currentTimeMillis();
                    }
                }
            }

            @Override
            public void tabRemoved(@NotNull TabInfo tabToRemove) {
                int result = Messages.showYesNoDialog(REMOVE_STOCK_GROUP_TITLE, "确认移除", Messages.getQuestionIcon());
                if (result == Messages.YES) {
                    //持久化数据更新
                }
            }
        }, () -> stockTabViewMap.forEach((key, value) -> value.shutdownTask()));
    }

    private void initData() {
        StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
        //新增我的自选tab
        //新增自定义分组


    }


    /**
     * 窗口展示时触发事件
     */
    public void showWindow() {
        if (isAutoLoad.get() && System.currentTimeMillis() - lastRefreshTime > 3000) {
            //代表是启用自动刷新  但实际未自动刷新
            getSelected().ifPresent(f -> {
                if (f.startTask()) {
                    lastRefreshTime = System.currentTimeMillis();
                } else {
                    stockTabs.select(stockTabs.getTabAt(0), true);
                }
            });
        }
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

    /**
     * 窗口关闭时触发事件
     */
    public void hideWindow() {
        getSelected().ifPresent(StockTabView::stopTask);
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
                String userInput = Messages.showInputDialog(project, ADD_STOCK_GROUP_MESSAGE, ADD_STOCK_GROUP_TITLE, IconUtil.getAddIcon(), "我的分组", null);
                StockTabView stockTabView = addGroup(userInput, StockTabEnum.STOCK_INFO);
                if (Objects.isNull(stockTabView)) {
                    return;
                }
                StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
                instance.addGroup(userInput);
            }
        });
        actionGroup.add(new AnAction(ADD_STOCK_TITLE, "", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                getSelected().ifPresent(stock -> {
                    FuStockSearchPopupView fuStockSearchPopupView = new FuStockSearchPopupView(stock);
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
                getSelected().ifPresent(stockInfoView -> {
                    boolean isStart = stockInfoView.startTask();
                    if (!isStart) {
                        //没有启动成功 则提示
                        FuNotification.notifyWarning(STOCK_AUTO_LOAD_TIME_TITLE, project);
                    }
                    lastRefreshTime = System.currentTimeMillis();
                    isAutoLoad.set(true);
                    StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
                    instance.setAutoRefresh(isAutoLoad.get());
                });
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
                getSelected().ifPresent(stockInfoView -> {
                    stockInfoView.stopTask();
                    isAutoLoad.set(false);
                    StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
                    instance.setAutoRefresh(isAutoLoad.get());
                });
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
                getSelected().ifPresent(StockTabView::reloadStock);
            }
        });
        return actionGroup;
    }


    private StockTabView addGroup(String group, StockTabEnum stockTabEnum) {
        if (StringUtils.isEmpty(group)) {
            return null;
        }
        if (stockTabViewMap.containsKey(group)) {
            return stockTabViewMap.get(group);
        }
        StockTabView stockTabView = StockTabFactory.create(group, stockTabEnum);
        TabInfo tabInfo = new TabInfo(stockTabView.getTabComponent());
        tabInfo.setText(group);
        stockTabs.addTab(tabInfo);
        // 可选：切换到新添加的标签
        stockTabs.select(tabInfo, true);
        stockTabViewMap.put(group, stockTabView);
        return stockTabView;
    }

    public Optional<StockTabView> getSelected() {
        return Optional.ofNullable(this.currentSelected);
    }


}

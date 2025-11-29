package cn.fudoc.trade.view.toolwindow;

import cn.fudoc.trade.api.data.StockInfo;
import cn.fudoc.trade.common.FuBundle;
import cn.fudoc.trade.common.FuNotification;
import cn.fudoc.trade.state.StockGroupPersistentState;
import cn.fudoc.trade.util.ToolBarUtils;
import cn.fudoc.trade.view.StockView;
import cn.fudoc.trade.view.search.FuStockSearchPopupView;
import cn.fudoc.trade.view.search.StockSearchDialog;
import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.util.IconUtil;
import icons.FuIcons;
import lombok.Getter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FuTradeWindow extends SimpleToolWindowPanel implements DataProvider {

    private final Project project;
    private final DefaultActionGroup actionGroup;
    private final JBTabs tabs;
    private static final String ADD_STOCK_GROUP_TITLE = FuBundle.message("add.stock.group.title");
    private static final String ADD_STOCK_GROUP_MESSAGE = FuBundle.message("add.stock.group.message");
    private static final String ADD_STOCK_TITLE = FuBundle.message("add.stock.title");
    private static final String ADD_STOCK_MESSAGE = FuBundle.message("add.stock.message");
    private static final String STOCK_AUTO_LOAD_TITLE = FuBundle.message("stock.auto.load.title");
    private static final String STOCK_AUTO_LOAD_TIME_TITLE = FuBundle.message("stock.auto.load.time.tip");
    private static final String REMOVE_STOCK_GROUP_TITLE = FuBundle.message("remove.stock.group.title");

    private Long refreshTime = 0L;

    @Getter
    private final AtomicBoolean isExecute = new AtomicBoolean(false);

    private final Map<String, StockView> stockViewMap = new HashMap<>();

    public FuTradeWindow(@NotNull Project project, ToolWindow toolWindow) {
        super(Boolean.TRUE, Boolean.TRUE);
        this.project = project;
        JPanel rootPanel = new JPanel(new BorderLayout());
        this.actionGroup = new DefaultActionGroup();
        tabs = JBTabsFactory.createTabs(project);
        StockGroupPersistentState instance = StockGroupPersistentState.getInstance();

        tabs.addListener(new TabsListener() {
            @Override
            public void selectionChanged(TabInfo oldSelection, TabInfo newSelection) {
                if (Objects.nonNull(oldSelection)) {
                    StockView stockView = stockViewMap.get(oldSelection.getText());
                    if (Objects.nonNull(stockView)) {
                        stockView.stopTask();
                    }
                }
                //切换新窗口时 判断当前是否开启自动刷新 开启时才刷新股票数据
                if (isExecute.get() && Objects.nonNull(newSelection)) {
                    StockView newStockView = stockViewMap.get(newSelection.getText());
                    if (Objects.nonNull(newStockView)) {
                        //添加新窗口时 默认启动刷新
                        newStockView.startTask();
                        refreshTime = System.currentTimeMillis();
                    }
                }
            }

            @Override
            public void tabRemoved(@NotNull TabInfo tabToRemove) {
                int result = Messages.showYesNoDialog(REMOVE_STOCK_GROUP_TITLE, "确认移除", Messages.getQuestionIcon());
                if (result == Messages.YES) {
                    //持久化数据更新
                    instance.removeGroup(tabToRemove.getText());
                }
            }
        }, () -> stockViewMap.forEach((key, value) -> value.shutdownTask()));
        setContent(rootPanel);
        //工具栏
        rootPanel.add(initToolBarUI(), BorderLayout.NORTH);
        //股票分组
        rootPanel.add(tabs.getComponent(), BorderLayout.CENTER);
        //todo 我的持仓
        //初始化工具栏事件
        initActionGroup();

        isExecute.set(instance.isAutoRefresh());
        Map<String, Set<String>> stockMap = instance.getStockMap();
        if (MapUtils.isNotEmpty(stockMap)) {
            stockMap.forEach((key, value) -> {
                StockView stockView = addGroup(key);
                if (Objects.isNull(stockView)) {
                    return;
                }
                stockView.initStock(value);
            });
        }
    }

    public void autoSelectedTab() {
        if (isExecute.get() && System.currentTimeMillis() - refreshTime > 3000) {
            //代表是启用自动刷新  但实际未自动刷新
            if (startTask()) {
                refreshTime = System.currentTimeMillis();
            } else {
                tabs.select(tabs.getTabAt(0), true);
            }
        }
    }

    public void stopTask() {
        TabInfo selectedInfo = tabs.getSelectedInfo();
        if (Objects.isNull(selectedInfo)) {
            return;
        }
        StockView stockView = stockViewMap.get(selectedInfo.getText());
        if (Objects.isNull(stockView)) {
            return;
        }
        stockView.stopTask();
    }


    private boolean startTask() {
        TabInfo selectedInfo = tabs.getSelectedInfo();
        if (Objects.isNull(selectedInfo)) {
            return false;
        }
        StockView stockView = stockViewMap.get(selectedInfo.getText());
        if (Objects.isNull(stockView)) {
            return false;
        }
        stockView.startTask();
        return true;
    }


    private JPanel initToolBarUI() {
        JPanel toolBarPanel = new JPanel(new BorderLayout());
        Utils.setSmallerFontForChildren(toolBarPanel);
        //创建及初始化工具栏
        ToolBarUtils.addActionToToolBar(toolBarPanel, "add.stock.toolBar", actionGroup, BorderLayout.WEST);
        return toolBarPanel;
    }

    private void initActionGroup() {
        this.actionGroup.add(new AnAction("添加股票分组", "", FuIcons.FU_ADD_GROUP) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                String userInput = Messages.showInputDialog(project, ADD_STOCK_GROUP_MESSAGE, ADD_STOCK_GROUP_TITLE, IconUtil.getAddIcon(), "我的分组", null);
                addGroup(userInput);
                StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
                instance.addGroup(userInput);
            }
        });
        this.actionGroup.add(new AnAction(ADD_STOCK_TITLE, "", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                getSelected().ifPresent(stock -> {
//                    StockSearchDialog stockSearchDialog = new StockSearchDialog(stock);
//                    stockSearchDialog.showAndGet();
                    FuStockSearchPopupView fuStockSearchPopupView = new FuStockSearchPopupView(stock);
                    fuStockSearchPopupView.showPopup(project);
                });
            }
        });
        //启动定时刷新股票
        this.actionGroup.add(new DumbAwareAction(STOCK_AUTO_LOAD_TITLE, "", AllIcons.Actions.Execute) {
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                Presentation presentation = e.getPresentation();
                presentation.setEnabled(!isExecute.get());
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                getSelected().ifPresent(stockView -> {
                    boolean isStart = stockView.startTask(null, "[ 将于开盘后自动刷新 ]");
                    if (!isStart) {
                        //没有启动成功 则提示
                        FuNotification.notifyWarning(STOCK_AUTO_LOAD_TIME_TITLE, project);
                    }
                    refreshTime = System.currentTimeMillis();
                    isExecute.set(true);
                    StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
                    instance.setAutoRefresh(isExecute.get());
                });
            }
        });
        //停止定时刷新股票
        this.actionGroup.addAction(new DumbAwareAction("停止刷新", "", AllIcons.Actions.Suspend) {
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                Presentation presentation = e.getPresentation();
                presentation.setEnabled(isExecute.get());
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                getSelected().ifPresent(stockView -> {
                    stockView.stopTask();
                    isExecute.set(false);
                    StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
                    instance.setAutoRefresh(isExecute.get());
                });
            }
        });

        //手动刷新股票
        this.actionGroup.addAction(new DumbAwareAction("刷新", "", AllIcons.Actions.ForceRefresh) {
            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }


            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                getSelected().ifPresent(StockView::manualUpdate);
            }
        });
    }


    private StockView addGroup(String group) {
        if (StringUtils.isEmpty(group)) {
            return null;
        }
        if (stockViewMap.containsKey(group)) {
            return stockViewMap.get(group);
        }
        StockView stockView = new StockView(project, group);
        TabInfo tabInfo = new TabInfo(stockView.getRootPanel());
        tabInfo.setText(group);
        tabs.addTab(tabInfo);
        // 可选：切换到新添加的标签
        tabs.select(tabInfo, true);
        stockViewMap.put(group, stockView);
        return stockView;
    }


    public Optional<StockView> getSelected() {
        TabInfo selectedInfo = tabs.getSelectedInfo();
        if (Objects.isNull(selectedInfo)) {
            return Optional.empty();
        }
        StockView stockView = stockViewMap.get(selectedInfo.getText());
        if (Objects.isNull(stockView)) {
            return Optional.empty();
        }
        return Optional.of(stockView);
    }


}

package cn.fudoc.trade.toolwindow;

import cn.fudoc.trade.state.StockGroupPersistentState;
import com.intellij.find.editorHeaderActions.Utils;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.util.IconUtil;
import cn.fudoc.trade.common.FuBundle;
import cn.fudoc.trade.util.ToolBarUtils;
import cn.fudoc.trade.view.StockView;
import icons.FuIcons;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FuTradeWindow extends SimpleToolWindowPanel implements DataProvider {

    private final Project project;
    private final JPanel rootPanel;
    private final DefaultActionGroup actionGroup;
    private final JBTabsImpl tabs;
    private static final String ADD_STOCK_GROUP_TITLE = FuBundle.message("add.stock.group.title");
    private static final String ADD_STOCK_GROUP_MESSAGE = FuBundle.message("add.stock.group.message");
    private static final String ADD_STOCK_TITLE = FuBundle.message("add.stock.title");
    private static final String ADD_STOCK_MESSAGE = FuBundle.message("add.stock.message");
    private static final String STOCK_AUTO_LOAD_TITLE = FuBundle.message("stock.auto.load.title");

    private final AtomicBoolean isExecute = new AtomicBoolean(false);

    private final Map<String, StockView> stockViewMap = new HashMap<>();

    public FuTradeWindow(@NotNull Project project, ToolWindow toolWindow) {
        super(Boolean.TRUE, Boolean.TRUE);
        this.project = project;
        this.rootPanel = new JPanel(new BorderLayout());
        this.actionGroup = new DefaultActionGroup();
        tabs = new JBTabsImpl(project);
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
                    }
                }

            }
        }, () -> stockViewMap.forEach((key, value) -> value.shutdownTask()));
        setContent(this.rootPanel);
        this.rootPanel.add(initToolBarUI(), BorderLayout.NORTH);
        this.rootPanel.add(tabs, BorderLayout.CENTER);
        //初始化工具栏事件
        initActionGroup();

        StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
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

    private JPanel initToolBarUI() {
        JPanel toolBarPanel = new JPanel(new BorderLayout());
        Utils.setSmallerFontForChildren(toolBarPanel);
        //创建及初始化工具栏
        ToolBarUtils.addActionToToolBar(toolBarPanel, "add.stock.toolBar", actionGroup, BorderLayout.WEST);
        return toolBarPanel;
    }

    private void initActionGroup() {
        this.actionGroup.add(new AnAction("添加股票分组", "", AllIcons.Actions.AddDirectory) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                String userInput = Messages.showInputDialog(project, ADD_STOCK_GROUP_MESSAGE, ADD_STOCK_GROUP_TITLE, IconUtil.getAddIcon(), "我的分组", null);
                addGroup(userInput);
            }
        });
        this.actionGroup.add(new AnAction(ADD_STOCK_TITLE, "", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                String userInput = Messages.showInputDialog(project, ADD_STOCK_MESSAGE, ADD_STOCK_TITLE, IconUtil.getAddIcon(), "sz300037", null);
                // 处理用户输入（点击“确定”返回输入内容，点击“取消”返回 null）
                if (userInput != null && !userInput.isEmpty()) {
                    getSelected().ifPresent(stockView -> {
                        stockView.addStock(userInput);
                    });
                }
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
                    stockView.startTask();
                    isExecute.set(true);
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

package cn.fudoc.trade.view;

import cn.fudoc.trade.common.FuBundle;
import cn.fudoc.trade.common.StockTabEnum;
import cn.fudoc.trade.view.stock.HoldingsStockTabView;
import cn.fudoc.trade.view.stock.StockTabView;
import cn.fudoc.trade.view.stock.WatchListStockTabView;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FuStockInfoView {

    private static final String REMOVE_STOCK_GROUP_TITLE = FuBundle.message("remove.stock.group.title");


    private StockTabView currentSelected;
    private final Map<String, StockTabView> stockTabViewMap = new ConcurrentHashMap<>();
    private final JBTabs tabs;
    private final StockTabEnum stockTabEnum;

    public FuStockInfoView(Project project, StockTabEnum stockTabEnum) {
        this.stockTabEnum = stockTabEnum;
        this.tabs = JBTabsFactory.createTabs(project);
        registerListener();
    }


    public JComponent getComponent() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(tabs.getComponent());
        rootPanel.setFont(JBUI.Fonts.label(11));
        return rootPanel;
    }


    public StockTabView getSelected() {
        return currentSelected;
    }


    public void add(String tab) {
        if (stockTabViewMap.containsKey(tab) || Objects.isNull(stockTabEnum)) {
            return;
        }
        StockTabView stockTabView = createStockTabView(tab);
        TabInfo tabInfo = new TabInfo(stockTabView.getComponent());
        tabInfo.setText(tab);
        this.tabs.addTab(tabInfo);
        stockTabViewMap.put(tab, stockTabView);
        // 可选：切换到新添加的标签
        tabs.select(tabInfo, true);
    }


    /**
     * 注册tab监听器
     */
    protected void registerListener() {
        tabs.addListener(new TabsListener() {
            @Override
            public void selectionChanged(TabInfo oldSelection, TabInfo newSelection) {
                //切换新窗口时 判断当前是否开启自动刷新 开启时才刷新股票数据
                if (Objects.nonNull(newSelection)) {
                    currentSelected = stockTabViewMap.get(newSelection.getText());
                }
            }

            @Override
            public void tabRemoved(@NotNull TabInfo tabToRemove) {
                int result = Messages.showYesNoDialog(REMOVE_STOCK_GROUP_TITLE, "确认移除", Messages.getQuestionIcon());
                if (result == Messages.YES) {
                    //持久化数据更新
                    stockTabViewMap.remove(tabToRemove.getText());
                }
            }
        });
    }


    private StockTabView createStockTabView(String tab) {
        return switch (stockTabEnum) {
            case STOCK_INFO -> new WatchListStockTabView(tab, Sets.newHashSet());
            case STOCK_HOLD -> new HoldingsStockTabView(tab, Sets.newHashSet());
        };
    }
}

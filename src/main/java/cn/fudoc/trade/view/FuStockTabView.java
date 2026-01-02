package cn.fudoc.trade.view;

import cn.fudoc.trade.core.common.FuBundle;
import cn.fudoc.trade.core.common.enumtype.GroupTypeEnum;
import cn.fudoc.trade.core.state.pojo.StockGroupInfo;
import cn.fudoc.trade.view.table.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.util.ui.JBUI;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FuStockTabView {

    private static final String REMOVE_STOCK_GROUP_TITLE = FuBundle.message("remove.stock.group.title");


    private StockTableView currentSelected;
    private final Map<String, StockTableView> stockTabViewMap = new ConcurrentHashMap<>();
    private final JBTabs tabs;
    @Setter
    @Getter
    private boolean isHide;
    private boolean isSwitch;

    public FuStockTabView(Project project,boolean isHide) {
        this.isHide = isHide;
        this.tabs = JBTabsFactory.createTabs(project);
        registerListener();
    }


    public JComponent getComponent() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(tabs.getComponent());
        rootPanel.setFont(JBUI.Fonts.label(11));
        return rootPanel;
    }


    public void removeAllTab() {
        isSwitch = true;
        tabs.removeAllTabs();
        stockTabViewMap.clear();
        isSwitch = false;
    }


    public StockTableView getSelected() {
        return currentSelected;
    }


    public void add(StockGroupInfo stockGroupInfo) {
        if (Objects.isNull(stockGroupInfo)) {
            return;
        }
        GroupTypeEnum groupType = stockGroupInfo.getGroupType();
        String tabName = getTabName(stockGroupInfo);
        StockTableView stockTableView = createStockTabView(stockGroupInfo);
        stockTabViewMap.put(tabName, stockTableView);
        TabInfo tabInfo = new TabInfo(stockTableView.getComponent());
        tabInfo.setText(tabName);
        tabInfo.setIcon(groupType.getIcon());
        this.tabs.addTab(tabInfo);
    }


    public void selected(StockGroupInfo stockGroupInfo) {
        for (TabInfo tabsTab : tabs.getTabs()) {
            if (tabsTab.getText().equals(getTabName(stockGroupInfo))) {
                tabs.select(tabsTab, true);
                return;
            }
        }
    }


    /**
     * 注册 tab监听器
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
                if(isSwitch){
                    return;
                }
                int result = Messages.showYesNoDialog(REMOVE_STOCK_GROUP_TITLE, "确认移除", Messages.getQuestionIcon());
                if (result == Messages.YES) {
                    //持久化数据更新
                    stockTabViewMap.remove(tabToRemove.getText());
                }
            }
        });
    }


    private StockTableView createStockTabView(StockGroupInfo stockGroupInfo) {
        GroupTypeEnum groupType = stockGroupInfo.getGroupType();
        return switch (groupType) {
            case STOCK_INFO -> this.isHide ? new StockGroupHideTableView(stockGroupInfo) :  new StockGroupTableView(stockGroupInfo);
            case STOCK_HOLD -> this.isHide ? new HoldStockGroupHideTableView(stockGroupInfo) : new HoldStockGroupTableView(stockGroupInfo);
        };
    }

    private String getTabName(StockGroupInfo stockGroupInfo){
        return this.isHide ? stockGroupInfo.getHideGroupName() : stockGroupInfo.getGroupName();
    }

}

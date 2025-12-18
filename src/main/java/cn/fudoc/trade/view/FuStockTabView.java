package cn.fudoc.trade.view;

import cn.fudoc.trade.core.common.FuBundle;
import cn.fudoc.trade.core.common.enumtype.StockTabEnum;
import cn.fudoc.trade.view.table.HoldStockGroupTableView;
import cn.fudoc.trade.view.table.StockGroupTableView;
import cn.fudoc.trade.view.table.StockTableView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang3.StringUtils;
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

    public FuStockTabView(Project project) {
        this.tabs = JBTabsFactory.createTabs(project);
        registerListener();
    }


    public JComponent getComponent() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(tabs.getComponent());
        rootPanel.setFont(JBUI.Fonts.label(11));
        return rootPanel;
    }


    public StockTableView getSelected() {
        return currentSelected;
    }


    public void add(String tab, StockTabEnum stockTabEnum) {
        if (StringUtils.isBlank(tab) || Objects.isNull(stockTabEnum) || stockTabViewMap.containsKey(tab)) {
            return;
        }
        StockTableView stockTableView = createStockTabView(tab, stockTabEnum);
        stockTabViewMap.put(tab, stockTableView);
        TabInfo tabInfo = new TabInfo(stockTableView.getComponent());
        tabInfo.setText(tab);
        tabInfo.setIcon(stockTabEnum.getIcon());
        this.tabs.addTab(tabInfo);
        // 切换到新添加的标签
        tabs.select(tabInfo, true);

    }


    public void selectMySelected(String tab) {
        for (TabInfo tabsTab : tabs.getTabs()) {
            if (tabsTab.getText().equals(tab)) {
                tabs.select(tabsTab, true);
                return;
            }
        }
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


    private StockTableView createStockTabView(String tab, StockTabEnum stockTabEnum) {
        switch (stockTabEnum) {
            case STOCK_INFO:
                return new StockGroupTableView(tab);
            case STOCK_HOLD:
                return new HoldStockGroupTableView(tab);
        }
        return null;
    }
}

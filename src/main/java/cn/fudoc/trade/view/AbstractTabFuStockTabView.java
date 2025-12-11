package cn.fudoc.trade.view;

import cn.fudoc.trade.common.FuBundle;
import cn.fudoc.trade.view.stock.StockTabView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractTabFuStockTabView implements FuStockTabView {


    private static final String REMOVE_STOCK_GROUP_TITLE = FuBundle.message("remove.stock.group.title");

    private StockTabView currentSelected;
    private final Map<String, StockTabView> stockTabViewMap = new ConcurrentHashMap<>();
    private final JBTabs tabs;


    protected abstract StockTabView createStockTabView(String tab);

    public AbstractTabFuStockTabView(Project project) {
        this.tabs = JBTabsFactory.createTabs(project);
        registerListener();
    }

    @Override
    public JComponent getComponent() {
        return this.tabs.getComponent();
    }


    @Override
    public StockTabView getSelected() {
        return currentSelected;
    }


    @Override
    public void add(String tab) {
        if (stockTabViewMap.containsKey(tab)) {
            return;
        }
        StockTabView stockTabView = createStockTabView(tab);
        this.tabs.addTab(new TabInfo(stockTabView.getComponent()));
        stockTabViewMap.put(tab, stockTabView);
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
}

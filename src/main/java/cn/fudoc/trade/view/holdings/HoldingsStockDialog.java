package cn.fudoc.trade.view.holdings;

import cn.fudoc.trade.core.state.HoldingsStockState;
import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.view.dto.StockInfoDTO;
import cn.fudoc.trade.view.holdings.tab.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
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

/**
 * 设置持仓信息窗口
 */
public class HoldingsStockDialog extends DialogWrapper {


    private final JBTabs tabs;

    /**
     * 当前选中 tab
     */
    private HoldingsTabView currentTab;

    /**
     * 股票信息
     */
    private final StockInfoDTO stockInfoDTO;

    /**
     * 持仓信息 tab
     */
    private final Map<String, HoldingsTabView> holdingsTabViewMap = new ConcurrentHashMap<>();

    /**
     * 持仓持久化信息
     */
    private HoldingsInfo holdingsInfo;

    public HoldingsStockDialog(Project project, String group, String stockCode, String stockName) {
        super(project, true);
        this.stockInfoDTO = new StockInfoDTO(group, stockCode, stockName);
        this.tabs = JBTabsFactory.createTabs(project);
        this.holdingsInfo = HoldingsStockState.getInstance().getHoldingsInfo(this.stockInfoDTO.group(), this.stockInfoDTO.stockCode());

        // 弹框标题
        setTitle("设置持仓信息");
        // 初始化 DialogWrapper（必须调用）
        init();
        //维护持仓成本 tab
        TabInfo tabInfo = addTab(new HoldingsCostTabView(this.stockInfoDTO, this.holdingsInfo));
        addTab(new HoldingsBuyTabView(this.stockInfoDTO, this.holdingsInfo));
        addTab(new HoldingsSellTabView(this.stockInfoDTO, this.holdingsInfo));
        addTab(new HoldingsDividendTabView(this.stockInfoDTO, this.holdingsInfo));
        addTab(new HoldingsTaxTabView(this.stockInfoDTO, this.holdingsInfo));
        addTab(new HoldingsTradeLogTabView(this.stockInfoDTO, this.holdingsInfo));
        registerListener();

        //默认选中持仓成本 tab
        this.tabs.select(tabInfo, true);
        this.currentTab = holdingsTabViewMap.get(tabInfo.getText());
    }

    @Override
    protected void doOKAction() {
        //保存数据
        if (Objects.isNull(currentTab)) {
            return;
        }
        if (Objects.isNull(this.holdingsInfo)) {
            this.holdingsInfo = new HoldingsInfo();
            HoldingsStockState.getInstance().add(this.stockInfoDTO.group(), this.stockInfoDTO.stockCode(), this.holdingsInfo);
        }
        this.currentTab.submit(this.holdingsInfo);
        super.doOKAction();
    }


    @Override
    protected JComponent createCenterPanel() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(tabs.getComponent());
        rootPanel.setFont(JBUI.Fonts.label(11));
        return rootPanel;
    }


    // 输入校验（保持不变）
    @Override
    protected ValidationInfo doValidate() {
        TabInfo selectedInfo = tabs.getSelectedInfo();
        if (Objects.isNull(selectedInfo)) {
            return null;
        }
        if (selectedInfo.getComponent() instanceof HoldingsTabView holdingsTabView) {
            return holdingsTabView.doValidate();
        }
        return null;
    }

    // 自定义按钮（保持不变）
    @Override
    protected Action @NotNull [] createActions() {
        getOKAction().putValue(Action.NAME, "确定");
        getCancelAction().putValue(Action.NAME, "取消");
        return new Action[]{getOKAction(), getCancelAction()};
    }


    /**
     * 注册 tab 监听器
     */
    protected void registerListener() {
        tabs.addListener(new TabsListener() {
            @Override
            public void selectionChanged(TabInfo oldSelection, TabInfo newSelection) {
                //切换新窗口时 判断当前是否开启自动刷新 开启时才刷新股票数据
                if (Objects.nonNull(newSelection)) {
                    currentTab = holdingsTabViewMap.get(newSelection.getText());
                }
            }

        });
    }


    private TabInfo addTab(HoldingsTabView holdingsTabView) {
        TabInfo tabInfo = new TabInfo(holdingsTabView.getPanel());
        tabInfo.setText(holdingsTabView.getTabName());
        this.tabs.addTab(tabInfo);
        holdingsTabViewMap.put(holdingsTabView.getTabName(), holdingsTabView);
        return tabInfo;
    }
}
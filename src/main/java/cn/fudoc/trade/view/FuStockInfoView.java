package cn.fudoc.trade.view;

import cn.fudoc.trade.view.stock.StockTabView;
import cn.fudoc.trade.view.stock.WatchListStockTabView;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;

public class FuStockInfoView extends AbstractTabFuStockTabView {


    @Override
    protected StockTabView createStockTabView(String tab) {
        return new WatchListStockTabView(tab, Sets.newHashSet());
    }

    public FuStockInfoView(Project project) {
        super(project);
    }

}

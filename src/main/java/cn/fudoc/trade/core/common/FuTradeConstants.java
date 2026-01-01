package cn.fudoc.trade.core.common;

public interface FuTradeConstants {

    String FU_TRADE = "FU TRADE";
    String FU_TRADE_NOTIFICATION_GROUP = "cn.fudoc.trade.notification.group";


    String MY_SELECTED_GROUP = "我的自选";
    String MY_POSITIONS_GROUP = "我的持仓";
    String LINK_RATE_LABEL = "点我设置交易费率";

    String ADD_STOCK = FuBundle.message("add.stock.title");

    String STOCK_VALID_FORMAT_ERROR = "stock.valid.formatError";

    interface CommonStateKey{
        String STOCK_AUTO_REFRESH = "fu-stock-auto-refresh";
    }


    interface TabName{
        String HOLDINGS_COST_TAB = "成本信息";
        String HOLDINGS_BUY_TAB = "买入";
        String HOLDINGS_SELL_TAB = "卖出";
        String HOLDINGS_DIVIDEND_TAB = "分红";
        String HOLDINGS_TAX_TAB = "股息红利税补缴";
        String HOLDINGS_LOG_TAB = "今日交易记录";
    }
}

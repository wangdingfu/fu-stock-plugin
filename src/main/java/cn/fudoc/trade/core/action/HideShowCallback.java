package cn.fudoc.trade.core.action;

public interface HideShowCallback {

    String getKey();

    String getShowText();

    String getHideText();

    void callback(boolean isShow);
}

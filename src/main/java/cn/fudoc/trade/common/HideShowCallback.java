package cn.fudoc.trade.common;

public interface HideShowCallback {

    String getKey();

    String getShowText();

    String getHideText();

    void callback(boolean isShow);
}

package cn.fudoc.trade.common;

public abstract class DefaultHideShowCallback implements HideShowCallback {
    @Override
    public String getKey() {
        return "fu-trade";
    }

    @Override
    public String getShowText() {
        return "摸鱼模式";
    }

    @Override
    public String getHideText() {
        return "工作模式";
    }

}

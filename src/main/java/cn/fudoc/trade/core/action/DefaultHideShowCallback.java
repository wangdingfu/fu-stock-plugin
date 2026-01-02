package cn.fudoc.trade.core.action;

import cn.fudoc.trade.core.common.FuTradeConstants;

public abstract class DefaultHideShowCallback implements HideShowCallback {
    @Override
    public String getKey() {
        return FuTradeConstants.CommonStateKey.FU_HIDE_MODE;
    }

    @Override
    public String getShowText() {
        return "正常模式";
    }

    @Override
    public String getHideText() {
        return "隐蔽模式";
    }

}

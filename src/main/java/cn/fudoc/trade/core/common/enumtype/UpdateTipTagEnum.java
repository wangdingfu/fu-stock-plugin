package cn.fudoc.trade.core.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UpdateTipTagEnum {

    OPEN_AFTER_AUTO_REFRESH("将于开盘后自动刷新"),
    CLOSE_AUTO_REFRESH("已停止自动刷新"),
    MANUAL_REFRESH("手动刷新中..."),

    ;


    private final String tag;
}

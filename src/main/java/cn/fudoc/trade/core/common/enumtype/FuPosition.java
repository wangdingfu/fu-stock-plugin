package cn.fudoc.trade.core.common.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FuPosition {

    TABLE_TITLE("title","表格标题",12f),
    TABLE_CONTENT("content","表格单元格正文本",11.5f),
    TABLE_CONTENT_SMALL("content_small","表格单元格副文本",11f),
    ;

    private final String code;
    private final String name;
    private final float defaultSize;
}

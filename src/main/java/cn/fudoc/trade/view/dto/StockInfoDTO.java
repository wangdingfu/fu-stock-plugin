package cn.fudoc.trade.view.dto;


import cn.fudoc.trade.core.common.enumtype.JYSEnum;

public record StockInfoDTO(String group, String stockCode, String stockName) {

    public JYSEnum jys() {
        if (this.stockCode.startsWith(JYSEnum.SH.getCode())) {
            return JYSEnum.SH;
        } else if (this.stockCode.startsWith(JYSEnum.SZ.getCode())) {
            return JYSEnum.SZ;
        } else if (this.stockCode.startsWith(JYSEnum.HK.getCode())) {
            return JYSEnum.HK;
        }
        return JYSEnum.DEFAULT;
    }
}

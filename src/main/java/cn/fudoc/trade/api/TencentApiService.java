package cn.fudoc.trade.api;


import cn.fudoc.trade.api.data.RealStockInfo;

import java.util.List;
import java.util.Set;

/**
 * 腾讯金融API
 */
public interface TencentApiService {

    /**
     * 股票实时数据列表（包含A股、港股股票实时数据）
     *
     * @param codeSet 股票代码
     * @return 股票实时数据列表
     */
    List<RealStockInfo> stockList(Set<String> codeSet);
}

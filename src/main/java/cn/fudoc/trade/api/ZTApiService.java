package cn.fudoc.trade.api;

import cn.fudoc.trade.api.data.StockInfo;
import cn.fudoc.trade.api.dto.ZTStockDTO;

import java.util.List;

/**
 * 智兔数服金融API
 */
public interface ZTApiService {

    /**
     * A 股所有股票集合
     */
    List<StockInfo> marketA();

    /**
     * 港股所有股票集合
     */
    List<StockInfo> marketHK();

}

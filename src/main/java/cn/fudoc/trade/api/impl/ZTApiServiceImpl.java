package cn.fudoc.trade.api.impl;

import cn.fudoc.trade.api.ZTApiService;
import cn.fudoc.trade.api.data.StockInfo;
import cn.fudoc.trade.api.dto.ZTStockDTO;
import cn.fudoc.trade.api.helper.ZTTokenHelper;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 智兔数服金融API
 */
public class ZTApiServiceImpl implements ZTApiService {

    private static final String marketAUrl = "https://api.zhituapi.com/hs/list/all?token=";
    private static final String marketHKUrl = "https://api.zhituapi.com/hk/list/all?token=";


    @Override
    public List<StockInfo> marketA() {
        return getStockListByMarket(marketAUrl);
    }

    @Override
    public List<StockInfo> marketHK() {
        return getStockListByMarket(marketHKUrl);
    }

    private List<StockInfo> getStockListByMarket(String marketUrl) {
        try {
            String result = HttpUtil.get(marketUrl + ZTTokenHelper.getToken());
            if (StringUtils.isEmpty(result) || !JSONUtil.isTypeJSONArray(result)) {
                return Lists.newArrayList();
            }
            List<ZTStockDTO> list = JSONUtil.toList(result, ZTStockDTO.class);
            return list.stream().map(this::buildStockInfo).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }


    private StockInfo buildStockInfo(ZTStockDTO ztStockDTO) {
        if (Objects.isNull(ztStockDTO)) {
            return null;
        }
        String dm = ztStockDTO.getDm();
        String mc = ztStockDTO.getMc();
        String jys = ztStockDTO.getJys();
        if (StringUtils.isEmpty(dm) || StringUtils.isEmpty(mc) || StringUtils.isEmpty(jys)) {
            return null;
        }
        dm = StringUtils.substringBefore(dm, ".");
        StockInfo stockInfo = new StockInfo();
        stockInfo.setCode(dm);
        stockInfo.setName(mc);
        stockInfo.setJys(jys);
        return stockInfo;
    }
}

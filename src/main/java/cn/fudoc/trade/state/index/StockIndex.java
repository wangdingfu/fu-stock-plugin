package cn.fudoc.trade.state.index;

import cn.fudoc.trade.api.data.StockInfo;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class StockIndex {

    private final boolean isHK;
    /**
     * 代码映射 key:代码 value：股票信息
     */
    private final Map<String, StockInfo> codeMap = new HashMap<>(8000);
    /**
     * 首字母映射 key：首字母 value：代码
     */
    private final Map<String, String> firstMap = new HashMap<>(8000);
    /**
     * 名称映射 key：名称 value：代码
     */
    private final Map<String, String> nameMap = new HashMap<>(8000);

    public StockIndex(boolean isHK) {
        this.isHK = isHK;
    }

    public StockIndex(List<StockInfo> stockInfoList, boolean isHK) {
        this.isHK = isHK;
        for (StockInfo stockInfo : stockInfoList) {
            String code = stockInfo.getCode();
            String name = stockInfo.getName();
            if (StringUtils.isBlank(code) || StringUtils.isBlank(name)) {
                continue;
            }
            codeMap.put(code, stockInfo);
            //首字母
            firstMap.put(getFirstPinyin(name), code);
            //名称
            nameMap.put(name.trim(), code);
        }
    }


    public List<MatchResult> match(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return null;
        }
        List<MatchResult> matchResultList = new ArrayList<>();
        if (StringUtils.isNumeric(keyword)) {
            int length = isHK ? 5 : 6;
            if (keyword.length() == length) {
                StockInfo stockInfo = codeMap.get(keyword);
                if (stockInfo != null) {
                    //完全匹配
                    return Lists.newArrayList(new MatchResult(stockInfo, 1D));
                }
            }
            codeMap.forEach((s, stockInfo) -> {
                if (s.contains(keyword)) {
                    matchResultList.add(new MatchResult(stockInfo, NumberUtil.div(keyword.length(), s.length())));
                }
            });
        } else if (isAlpha(keyword)) {
            //首字母匹配
            return match(keyword, firstMap);
        } else {
            //名称匹配
            return match(keyword, nameMap);
        }
        return matchResultList;
    }

    private List<MatchResult> match(String keyword, Map<String, String> map) {
        String code = map.get(keyword);
        if (StringUtils.isNotBlank(code)) {
            StockInfo stockInfo = codeMap.get(keyword);
            if (stockInfo != null) {
                //完全匹配
                return Lists.newArrayList(new MatchResult(stockInfo, 1D));
            }
        }
        int keyWordLength = keyword.length();
        List<MatchResult> matchResultList = Lists.newArrayList();
        map.forEach((n, c) -> {
            if (n.contains(keyword)) {
                StockInfo stockInfo = codeMap.get(c);
                if (Objects.nonNull(stockInfo)) {
                    matchResultList.add(new MatchResult(stockInfo, NumberUtil.div(keyWordLength, n.length())));
                }
            }
        });
        return matchResultList;
    }


    private boolean isAlpha(String str) {
        for (char c : str.toCharArray()) {
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                return false;
            }
        }
        return true;
    }

    private String getFirstPinyin(String name) {
        name = name.trim().replace("*", "").replace("ST", "");
        return PinyinUtil.getFirstLetter(name, null);
    }

}

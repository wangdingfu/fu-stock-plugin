package cn.fudoc.trade.core.state.index;

import cn.fudoc.trade.api.data.StockInfo;
import cn.fudoc.trade.util.PinyinUtil;
import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Getter
@Setter
public class StockIndex {

    private boolean isHK;
    /**
     * 代码映射 key:代码 value：股票信息
     */
    private Map<String, StockInfo> codeMap;
    /**
     * 首字母映射 key：首字母 value：代码
     */
    private Map<String, Set<String>> firstMap;
    /**
     * 名称映射 key：名称 value：代码
     */
    private Map<String, String> nameMap;

    public StockIndex() {
    }

    public StockIndex(boolean isHK) {
        this.isHK = isHK;
        this.codeMap = new HashMap<>();
        this.firstMap = new HashMap<>();
        this.nameMap = new HashMap<>();
    }

    public StockIndex(List<StockInfo> stockInfoList, boolean isHK) {
        this.isHK = isHK;
        if (CollectionUtils.isNotEmpty(stockInfoList)) {
            this.codeMap = new HashMap<>(stockInfoList.size());
            this.firstMap = new HashMap<>(stockInfoList.size() * 2);
            this.nameMap = new HashMap<>(stockInfoList.size());
        }
        for (StockInfo stockInfo : stockInfoList) {
            String code = stockInfo.getCode();
            String name = stockInfo.getName();
            if (StringUtils.isBlank(code) || StringUtils.isBlank(name)) {
                continue;
            }
            codeMap.put(code, stockInfo);
            //首字母
            Set<String> firstPinyin = getFirstPinyin(name);
            if (firstPinyin != null && !firstPinyin.isEmpty()) {
                firstPinyin.forEach(s -> {
                    Set<String> codeSet = firstMap.get(s);
                    if (Objects.isNull(codeSet)) {
                        codeSet = new HashSet<>();
                        firstMap.put(s, codeSet);
                    }
                    codeSet.add(code);
                });
            }
            //名称
            nameMap.put(name.trim(), code);
        }
    }


    public List<MatchResult> match(String keyword) {
        if (StringUtils.isBlank(keyword) || MapUtils.isEmpty(codeMap)) {
            return Lists.newArrayList();
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
            if(MapUtils.isEmpty(firstMap)) {
                return Lists.newArrayList();
            }
            //首字母匹配
            Set<String> codeSet = firstMap.get(keyword);
            if (CollectionUtils.isNotEmpty(codeSet)) {
                matchFirstMap(codeSet, matchResultList, 1D);
                return matchResultList;
            }
            int keyWordLength = keyword.length();
            firstMap.forEach((n, c) -> {
                if (n.contains(keyword)) {
                    double similarity = NumberUtil.div(keyWordLength, n.length());
                    matchFirstMap(c, matchResultList, similarity);
                }
            });
            return matchResultList;
        } else {
            if(MapUtils.isEmpty(nameMap)) {
                return Lists.newArrayList();
            }
            //名称匹配
            return match(keyword, nameMap);
        }
        return matchResultList;
    }


    private void matchFirstMap(Set<String> codeSet, List<MatchResult> matchResultList, double similarity) {
        if (CollectionUtils.isNotEmpty(codeSet)) {
            for (String code : codeSet) {
                StockInfo stockInfo = codeMap.get(code);
                if (stockInfo != null) {
                    //完全匹配
                    matchResultList.add(new MatchResult(stockInfo, similarity));
                }
            }
        }
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

    private Set<String> getFirstPinyin(String name) {
        name = name.trim().replace("*", "").replace("ST", "");
        return PinyinUtil.getFirstLetter(name);
    }

}

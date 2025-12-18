package cn.fudoc.trade.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.*;

public class PinyinUtil {
    // 固定词语-首字母映射表（key：固定词语，value：对应首字母，支持多字词语）
    private static final Map<String, String> FIXED_WORD_MAP = new HashMap<>();
    // 设置汉字拼音输出的格式
    private static final HanyuPinyinOutputFormat format;
    // 缓存最长词语长度（优化匹配效率，避免重复计算）
    private static int MAX_FIXED_WORD_LENGTH = 0;

    static {
        // 1. 初始化固定词语映射（按需扩展）
        FIXED_WORD_MAP.put("银行", "yh");
        FIXED_WORD_MAP.put("股份", "gf");
        FIXED_WORD_MAP.put("控股", "kg");
        FIXED_WORD_MAP.put("万", "w");
        FIXED_WORD_MAP.put("虹", "h");
        FIXED_WORD_MAP.put("红", "h");
        FIXED_WORD_MAP.put("家", "j");
        FIXED_WORD_MAP.put("叶", "y");
        FIXED_WORD_MAP.put("不", "b");
        FIXED_WORD_MAP.put("信", "x");
        FIXED_WORD_MAP.put("石", "s");
        FIXED_WORD_MAP.put("车", "c");
        FIXED_WORD_MAP.put("广", "g");
        FIXED_WORD_MAP.put("长城", "cc");
        FIXED_WORD_MAP.put("中国", "zg");
        FIXED_WORD_MAP.put("信息", "xx");
        FIXED_WORD_MAP.put("中信", "zx");
        FIXED_WORD_MAP.put("通信", "tx");
        FIXED_WORD_MAP.put("传媒", "cm");
        FIXED_WORD_MAP.put("长虹", "ch");
        FIXED_WORD_MAP.put("沈阳", "sy");
        FIXED_WORD_MAP.put("合百", "hb");
        FIXED_WORD_MAP.put("汽车", "qc");
        FIXED_WORD_MAP.put("石油", "sy");
        FIXED_WORD_MAP.put("长安", "ca");
        FIXED_WORD_MAP.put("食品", "sp");
        FIXED_WORD_MAP.put("饮食", "ys");
        FIXED_WORD_MAP.put("长春", "cc");
        FIXED_WORD_MAP.put("广电", "gd");
        FIXED_WORD_MAP.put("证券", "zq");
        FIXED_WORD_MAP.put("厦门", "xm");
        FIXED_WORD_MAP.put("科技", "kj");
        FIXED_WORD_MAP.put("石化", "sh");
        FIXED_WORD_MAP.put("西藏", "xz");
        FIXED_WORD_MAP.put("广发", "gf");
        FIXED_WORD_MAP.put("长江", "cj");
        FIXED_WORD_MAP.put("东莞", "dg");
        FIXED_WORD_MAP.put("期货", "qh");
        FIXED_WORD_MAP.put("联合", "lh");
        FIXED_WORD_MAP.put("沪上阿姨", "hsay");
        FIXED_WORD_MAP.put("齐家", "qj");
        FIXED_WORD_MAP.put("华强", "hq");
        FIXED_WORD_MAP.put("商行", "sh");
        FIXED_WORD_MAP.put("技术", "js");
        FIXED_WORD_MAP.put("药厂", "yc");
        FIXED_WORD_MAP.put("合盛硅业", "hsgy");

        // 2. 初始化拼音格式
        format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE); // 小写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 不加声调
        format.setVCharType(HanyuPinyinVCharType.WITH_V); // 'ü' 用 "v" 代替

        // 3. 计算最长固定词语长度（优化匹配效率）
        for (String word : FIXED_WORD_MAP.keySet()) {
            if (word.length() > MAX_FIXED_WORD_LENGTH) {
                MAX_FIXED_WORD_LENGTH = word.length();
            }
        }
    }

    /**
     * 获取中文的所有可能首字母组合（优先匹配固定词语，未匹配部分降级处理）
     * 特性：
     * 1. 优先匹配 FIXED_WORD_MAP 中的固定词语（最长匹配优先）
     * 2. 未匹配部分：中文取拼音首字母（含多音字全组合），非中文直接保留
     * 3. 自动去重、过滤空结果，格式统一
     *
     * @param str 输入文本（可含中文、字母、数字、标点）
     * @return 所有首字母组合（如“重庆银行”→["cqyh"]，“银行测试”→["yhcw"]），空输入返回空字符串
     */
    public static Set<String> getFirstLetter(String str) {
        // 空输入直接返回空
        if (StrUtil.isBlank(str)) {
            return null;
        }

        // 步骤1：优先匹配固定词语（最长匹配优先）
        List<Set<String>> charFirstLetters = new ArrayList<>();
        int strLength = str.length();
        int i = 0;

        while (i < strLength) {
            char currentChar = str.charAt(i);
            // 非中文字符：直接保留，跳过当前字符
            if (!isChineseChar(currentChar)) {
                String charStr = String.valueOf(currentChar).toLowerCase();
                charFirstLetters.add(CollUtil.newHashSet(charStr));
                i++;
                continue;
            }

            // 中文字符：尝试匹配固定词语（最长匹配优先）
            boolean matched = false;
            // 匹配长度：从最长固定词语长度开始，逐步缩短到1
            int matchLength = Math.min(MAX_FIXED_WORD_LENGTH, strLength - i);

            for (int len = matchLength; len >= 1; len--) {
                String subStr = str.substring(i, i + len);
                // 匹配到固定词语：添加对应首字母，跳过已匹配字符
                if (FIXED_WORD_MAP.containsKey(subStr)) {
                    String fixedFirstLetter = FIXED_WORD_MAP.get(subStr);
                    charFirstLetters.add(CollUtil.newHashSet(fixedFirstLetter));
                    i += len; // 跳过当前匹配的 len 个字符
                    matched = true;
                    break;
                }
            }

            // 未匹配到固定词语：按原逻辑提取单个字符的首字母（含多音字）
            if (!matched) {
                Set<String> firstSet = getSingleCharFirstLetters(currentChar);
                if (CollUtil.isNotEmpty(firstSet)) {
                    charFirstLetters.add(firstSet);
                } else {
                    // 生僻字/转换失败：保留原字符
                    charFirstLetters.add(CollUtil.newHashSet(String.valueOf(currentChar)));
                }
                i++;
            }
        }

        // 步骤2：计算首字母组合的笛卡尔积（生成所有可能结果）
        return cartesianProduct(charFirstLetters);
    }

    /**
     * 计算多个集合的笛卡尔积（生成所有首字母组合）
     */
    private static Set<String> cartesianProduct(List<Set<String>> sets) {
        Set<String> result = new HashSet<>();
        if (CollUtil.isEmpty(sets)) {
            return result;
        }

        // 初始化：第一个元素的所有首字母作为初始组合
        result.addAll(sets.get(0));

        // 迭代拼接后续元素的首字母
        for (int i = 1; i < sets.size(); i++) {
            Set<String> currentSet = sets.get(i);
            Set<String> temp = new HashSet<>();
            for (String existing : result) {
                for (String current : currentSet) {
                    temp.add(existing + current);
                }
            }
            result = temp;
        }

        // 过滤空字符串
        result.remove("");
        return result;
    }

    /**
     * 判断字符是否为中文汉字（含常用+扩展区）
     */
    private static boolean isChineseChar(char c) {
        return (c >= 0x4E00 && c <= 0x9FA5) || (c >= 0x3400 && c <= 0x4DBF);
    }

    /**
     * 获取单个汉字的所有可能首字母（含多音字）
     */
    private static Set<String> getSingleCharFirstLetters(char c) {
        try {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
            if (ArrayUtil.isEmpty(pinyinArray)) {
                return null;
            }

            Set<String> firstSet = CollUtil.newHashSet();
            for (String pinyin : pinyinArray) {
                if (StrUtil.isNotBlank(pinyin)) {
                    firstSet.add(pinyin.charAt(0) + "");
                }
            }
            return firstSet;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取汉字全拼（多音字取第一个拼音，兼容原有功能）
     */
    public static String getPinyin(String str, String separator) {
        final StrBuilder result = StrUtil.strBuilder();
        boolean isFirst = true;
        final int strLen = str.length();
        try {
            for (int i = 0; i < strLen; i++) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    result.append(separator);
                }
                final String[] pinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i), format);
                if (ArrayUtil.isEmpty(pinyinStringArray)) {
                    result.append(str.charAt(i));
                } else {
                    result.append(pinyinStringArray[0]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            return str;
        }
        return result.toString();
    }

    // ------------------------------ 扩展方法（按需使用）------------------------------

    /**
     * 新增固定词语-首字母映射（运行时动态添加）
     *
     * @param word        固定词语（非空）
     * @param firstLetter 对应首字母（非空）
     */
    public static void addFixedWord(String word, String firstLetter) {
        if (StrUtil.isNotBlank(word) && StrUtil.isNotBlank(firstLetter)) {
            FIXED_WORD_MAP.put(word, firstLetter);
            // 更新最长词语长度
            if (word.length() > MAX_FIXED_WORD_LENGTH) {
                MAX_FIXED_WORD_LENGTH = word.length();
            }
        }
    }

    /**
     * 删除固定词语映射
     *
     * @param word 要删除的词语
     */
    public static void removeFixedWord(String word) {
        FIXED_WORD_MAP.remove(word);
        // 重新计算最长词语长度（仅当删除的是最长词语时）
        if (word.length() == MAX_FIXED_WORD_LENGTH) {
            MAX_FIXED_WORD_LENGTH = 0;
            for (String key : FIXED_WORD_MAP.keySet()) {
                if (key.length() > MAX_FIXED_WORD_LENGTH) {
                    MAX_FIXED_WORD_LENGTH = key.length();
                }
            }
        }
    }

    /**
     * 获取所有固定词语映射
     */
    public static Map<String, String> getFixedWordMap() {
        return new HashMap<>(FIXED_WORD_MAP); // 返回副本，避免外部修改原映射
    }
}
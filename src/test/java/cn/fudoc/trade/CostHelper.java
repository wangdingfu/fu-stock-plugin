package cn.fudoc.trade;

import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import cn.fudoc.trade.view.dto.HoldingsTodayInfo;
import cn.fudoc.trade.view.helper.CalculateCostHelper;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CostHelper {



    public static void main(String[] args) {
//        HoldingsTodayInfo calculate = CalculateCostHelper.calculate(TLYS());
//        System.out.println(JSONUtil.toJsonPrettyStr(calculate));
        String s1 = HttpUtil.get("http://qt.gtimg.cn/q=sz000001");
        System.out.println(s1);
//        System.out.println(new BigDecimal("4.8375").setScale(2, RoundingMode.HALF_UP));
    }

    private static HoldingsInfo TLYS(){
        HoldingsInfo holdingsInfo = new HoldingsInfo();
        holdingsInfo.add(1,2000,"4.36","5");
        holdingsInfo.add(2,2000,"4.45","9.45");
        holdingsInfo.add(1,2000,"4.44","5");
        holdingsInfo.add(1,2000,"4.69","5");
        holdingsInfo.add(1,2000,"4.38","5");
        holdingsInfo.add(2,3000,"4.71","12.07");
        holdingsInfo.add(1,2000,"4.98","5");
        holdingsInfo.add(2,2500,"5.9","12.38");
        holdingsInfo.add(1,2000,"5.99","5");
        holdingsInfo.add(1,1500,"5.57","5");
        holdingsInfo.add(1,1000,"5.98","5");
        holdingsInfo.add(1,1000,"5.7","5");
        holdingsInfo.add(1,1000,"5.25","5");
        holdingsInfo.add(2,1000,"5.39","7.7");
        holdingsInfo.add(1,1000,"5.36","5");
        holdingsInfo.add(1,2000,"5.27","5");
        holdingsInfo.add(3,11000,"0.05","0");

        holdingsInfo.add(2,2000,"5.11","10.11");
        holdingsInfo.add(1,2000,"5.09","5");
        holdingsInfo.add(1,1200,"5.08","5");
        return holdingsInfo;
    }

    private static HoldingsInfo HTFZ(){
        HoldingsInfo holdingsInfo = new HoldingsInfo();
        holdingsInfo.add(1,3000,"8.82","5.27");
        holdingsInfo.add(2,1500,"10.02","12.67");
        holdingsInfo.add(2,1000,"11.176","10.69");
        return holdingsInfo;
    }

    private static HoldingsInfo ZGLY(){
        HoldingsInfo holdingsInfo = new HoldingsInfo();
        holdingsInfo.add(1,3000,"7.83","5.23");
        holdingsInfo.add(3,3000,"0.123","0");
        holdingsInfo.add(1,500,"9.96","5.05");
        holdingsInfo.add(2,500,"10.47","7.67");
        holdingsInfo.add(4,0,"0","6.15");
        return holdingsInfo;
    }

}

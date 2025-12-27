package cn.fudoc.trade.view.holdings.tab;

import cn.fudoc.trade.core.state.pojo.HoldingsInfo;
import com.intellij.openapi.ui.ValidationInfo;

import javax.swing.*;

public interface HoldingsTabView {

    /**
     * tab 名称
     */
    String getTabName();

    /**
     * 获取展示的视图
     */
    JPanel getPanel();

    /**
     * 表单提交
     *
     * @param holdingsInfo 当前持仓信息
     */
    void submit(HoldingsInfo holdingsInfo);

    /**
     * 表单校验
     */
    ValidationInfo doValidate();
}

package cn.fudoc.trade.view;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class TodayProfitView {

    public JPanel rootPanel;

    public TodayProfitView() {
        this.rootPanel = new JPanel(new BorderLayout());
    }
}

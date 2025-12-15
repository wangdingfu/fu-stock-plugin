package cn.fudoc.trade.view.table.profit;

import cn.fudoc.trade.view.dto.HoldStockDataDto;
import cn.fudoc.trade.view.render.MultiLineTableCellRenderer;
import cn.fudoc.trade.view.render.StockColorTableCellRenderer;
import com.google.common.collect.Lists;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProfitRankingPanel extends JPanel {

    private final ProfitTableModel tableModel;

    public ProfitRankingPanel() {
        setLayout(new BorderLayout());

        // 1. 标题栏（今日收益贡献度排名 + 今日收益）
        JLabel rankLabel = new JLabel("今日收益贡献度排名");
        JLabel profitLabel = new JLabel("今日收益");
        rankLabel.setForeground(JBColor.GRAY);
        rankLabel.setFont(rankLabel.getFont().deriveFont(13.0f));
        profitLabel.setForeground(JBColor.GRAY);
        profitLabel.setFont(rankLabel.getFont().deriveFont(13.0f));
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(rankLabel);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(profitLabel);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.setPreferredSize(new Dimension(titlePanel.getWidth(), 50));
        titlePanel.setSize(new Dimension(titlePanel.getWidth(), 50));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(titlePanel, BorderLayout.NORTH);

        // 2. 创建表格模型
        this.tableModel = new ProfitTableModel();
        JBTable table = new JBTable(tableModel);
        table.setTableHeader(null);
        // 3. 应用自定义渲染器
        table.getColumn("股票信息").setCellRenderer(new MultiLineTableCellRenderer(Lists.newArrayList(1), Lists.newArrayList(1)));
        table.getColumn("今日收益").setCellRenderer(new StockColorTableCellRenderer(50));
        table.setFont(table.getFont().deriveFont(15.0f));
        // 4. 调整表格样式
        table.setShowGrid(false); // 隐藏网格线（对应截图）
        table.setRowHeight(50); // 行高（适配换行显示）

        // 5. 包裹滚动条（IDEA规范）
        JBScrollPane scrollPane = new JBScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }


    public void initDataList(List<HoldStockDataDto> tableDataList) {
        tableModel.setData(tableDataList);
    }
}
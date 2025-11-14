package cn.fudoc.trade.view;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import cn.fudoc.trade.strategy.FetchStockStrategy;
import cn.fudoc.trade.strategy.StockInfo;
import cn.fudoc.trade.strategy.TencentFetchStockStrategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Slf4j
public class StockView {

    private final Project project;

    @Getter
    private final JPanel rootPanel;

    private final JLabel showTextLabel;

    private final DefaultTableModel tableModel;
    /**
     * 定义表格列名
     */
    private static final String[] columnNames = {"股票代码", "股票名称", "当前价格", "涨跌幅(%)", "成交量(万手)"};

    /**
     * 股票分组名称
     */
    private final String group;

    private final ScheduledTaskManager scheduledTaskManager;

    public StockView(Project project, String group) {
        this.project = project;
        this.group = group;
        this.rootPanel = new JPanel(new BorderLayout());
        this.showTextLabel = new JLabel("最后更新时间：--");
        // 添加上下左右边距
        showTextLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        this.rootPanel.add(showTextLabel, BorderLayout.PAGE_END);
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            // 设置单元格不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        initStockTable();
        scheduledTaskManager = new ScheduledTaskManager();
    }


    public void startTask() {
        scheduledTaskManager.startTask(this::loadStockData);
    }

    public void stopTask() {
        scheduledTaskManager.stopTask();
    }

    public void shutdownTask() {
        scheduledTaskManager.shutdownExecutor();
    }


    private void initStockTable() {
        JBTable stockTable = new JBTable(tableModel);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(stockTable);
        this.rootPanel.add(decorator.createPanel(), BorderLayout.CENTER);
    }

    /**
     * 加载列表中的股票
     */
    private void loadStockData() {
        log.info("分组【{}】刷新股票数据", group);
        // 实际场景：调用股票接口获取数据
        List<StockInfo> stockInfoList = fetchLatestStockData();
        // 在EDT线程中更新UI（Swing线程安全）
        SwingUtilities.invokeLater(() -> updateStockData(stockInfoList));
    }


    private List<StockInfo> fetchLatestStockData() {
        return fetchStockData(getCodeList());
    }


    private Set<String> getCodeList(){
        Vector<Vector> dataVector = tableModel.getDataVector();
        Set<String> codeSet = new HashSet<>();
        dataVector.forEach(data -> {
            Object o = data.get(0);
            if (Objects.nonNull(o)) {
                codeSet.add(o.toString());
            }
        });
        return codeSet;
    }

    /**
     * 添加股票到表格中
     *
     * @param code 股票代码
     */
    public void addStock(String code) {
        List<StockInfo> stockInfos = fetchStockData(Lists.newArrayList(code));
        if (CollectionUtils.isEmpty(stockInfos)) {
            return;
        }
        stockInfos.forEach(f -> tableModel.addRow(toTableData(f)));

    }

    private List<StockInfo> fetchStockData(Set<String> codeList) {
        if (CollectionUtils.isEmpty(codeList)) {
            return Lists.newArrayList();
        }
        FetchStockStrategy fetchStockStrategy = new TencentFetchStockStrategy();
        return fetchStockStrategy.fetch(codeList);
    }


    // 提供方法更新数据（实际可通过接口实时刷新）
    public void updateStockData(List<StockInfo> stockInfoList) {
        tableModel.setRowCount(0); // 清空现有数据
        stockInfoList.forEach(f -> tableModel.addRow(toTableData(f)));
        // 2. 更新时间标签（格式化当前时间）
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        showTextLabel.setText("最后更新时间：" + currentTime);
    }

    private Vector<Object> toTableData(StockInfo stockInfo) {
        Vector<Object> vector = new Vector<>();
        vector.add(stockInfo.getStockCode());
        vector.add(stockInfo.getStockName());
        vector.add(stockInfo.getCurrentPrice());
        vector.add(stockInfo.getIncreaseRate());
        vector.add(stockInfo.getVolume());
        return vector;
    }

}

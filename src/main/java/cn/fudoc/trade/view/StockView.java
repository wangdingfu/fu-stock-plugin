package cn.fudoc.trade.view;

import cn.fudoc.trade.api.TencentApiService;
import cn.fudoc.trade.api.data.RealStockInfo;
import cn.fudoc.trade.common.FuBundle;
import cn.fudoc.trade.common.FuNotification;
import cn.fudoc.trade.state.StockGroupPersistentState;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
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
    private static final String[] columnNames = {"股票代码", "股票名称", "当前价格", "涨跌幅(%)", "成交额"};
    private static final String STOCK_UN_SELECTED_TITLE = FuBundle.message("stock.un_selected.title");
    private static final String STOCK_UN_SELECTED_REFRESH_TITLE = FuBundle.message("stock.un_selected.refresh.title");

    /**
     * 股票分组名称
     */
    @Getter
    private final String group;

    private final JBTable stockTable;

    private final ScheduledTaskManager scheduledTaskManager;
    private final TencentApiService tencentApiService;

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
        stockTable = initStockTable();
        scheduledTaskManager = new ScheduledTaskManager();
        tencentApiService = ApplicationManager.getApplication().getService(TencentApiService.class);
    }

    public boolean startTask() {
        return startTask(null, null);
    }

    public boolean startTask(String tag, String notStartTag) {
        if (isCanStart()) {
            scheduledTaskManager.startTask(() -> loadStockData(tag));
            return true;
        }
        loadStockData(notStartTag);
        return false;
    }

    private boolean isCanStart() {
        //判断当前时间是否开盘时间  不是则不提交任务 自动刷新时间段定位9:15 ~ 11:30 13:00~15:00
        Date now = new Date();
        int hour = DateUtil.hour(now, true);
        if (hour < 9 || hour > 15 || hour == 12) {
            return false;
        }
        int minute = DateUtil.minute(now);
        if (hour == 9 && minute < 15) {
            return false;
        }
        if (hour == 11 && minute > 30) {
            return false;
        }
        if (DateUtil.isWeekend(now)) {
            //周六周日不开盘
            return false;
        }
        //TODO 节假日判断
        return true;
    }

    public void stopTask() {
        scheduledTaskManager.stopTask();
        updateTime("[ 已停止刷新 ]");
    }

    public void shutdownTask() {
        scheduledTaskManager.shutdownExecutor();
        updateTime("[ 已停止刷新 ]");
    }


    private JBTable initStockTable() {
        JBTable stockTable = new JBTable(tableModel);
        // 2. 创建右键菜单
        JPopupMenu popupMenu = createPopupMenu();
        stockTable.setComponentPopupMenu(popupMenu);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(stockTable);
        stockTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.rootPanel.add(decorator.createPanel(), BorderLayout.CENTER);
        return stockTable;
    }

    // 创建右键菜单（包含“删除”选项）
    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        // 添加“删除”菜单项
        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(e -> deleteSelectedRow()); // 绑定删除逻辑

        menu.add(deleteItem);
        return menu;
    }

    // 删除选中行的核心逻辑
    private void deleteSelectedRow() {
        int[] selectedRows = stockTable.getSelectedRows();
        if (selectedRows == null || selectedRows.length == 0) {
            String message = scheduledTaskManager.isRunning() ? STOCK_UN_SELECTED_REFRESH_TITLE : STOCK_UN_SELECTED_TITLE;
            FuNotification.notifyWarning(message, project);
            return;
        }
        StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
        for (int i = selectedRows.length - 1; i >= 0; i--) {
            int modelRow = stockTable.convertRowIndexToModel(selectedRows[i]);
            tableModel.removeRow(modelRow);
            //持久化移除
            Object valueAt = tableModel.getValueAt(modelRow, 0);
            String code = Objects.isNull(valueAt) ? "" : valueAt.toString();
            instance.removeStock(group, code);
        }
    }

    public void removeStock(String code) {
        StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object valueAt = tableModel.getValueAt(i, 0);
            if (code.equals(valueAt.toString())) {
                int modelRow = stockTable.convertRowIndexToModel(i);
                tableModel.removeRow(modelRow);
                instance.removeStock(group, code);
            }
        }
    }

    /**
     * 加载列表中的股票
     */
    public void loadStockData(String tag) {
        // 实际场景：调用股票接口获取数据
        List<RealStockInfo> realStockInfoList = fetchLatestStockData();
        // 在EDT线程中更新UI（Swing线程安全）
        SwingUtilities.invokeLater(() -> updateStockData(realStockInfoList, tag));
    }


    private List<RealStockInfo> fetchLatestStockData() {
        return fetchStockData(getCodeList());
    }

    public boolean isConstants(String code) {
        return getCodeList().contains(code);
    }

    private Set<String> getCodeList() {
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
        Set<String> codeList = getCodeList();
        codeList.add(code);
        initStock(codeList);
        StockGroupPersistentState instance = StockGroupPersistentState.getInstance();
        instance.addStock(group, code);
    }

    public void initStock(Set<String> codeList) {
        if (CollectionUtils.isEmpty(codeList)) {
            return;
        }
        updateStockData(fetchStockData(codeList), null);
    }

    private List<RealStockInfo> fetchStockData(Set<String> codeList) {
        if (CollectionUtils.isEmpty(codeList)) {
            return Lists.newArrayList();
        }
        return tencentApiService.stockList(codeList);
    }


    // 提供方法更新数据（实际可通过接口实时刷新）
    public void updateStockData(List<RealStockInfo> realStockInfoList, String tag) {
        tableModel.setRowCount(0); // 清空现有数据
        realStockInfoList.forEach(f -> tableModel.addRow(toTableData(f)));
        // 2. 更新时间标签（格式化当前时间）
        updateTime(tag);
    }


    private void updateTime(String tag) {
        // 2. 更新时间标签（格式化当前时间）
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        showTextLabel.setText("最后更新时间：" + currentTime + (StringUtils.isEmpty(tag) ? "" : (" " + tag)));
    }

    public void manualUpdate() {
        updateStockData(fetchLatestStockData(), "[ 手动刷新中... ]");
    }

    private Vector<Object> toTableData(RealStockInfo realStockInfo) {
        Vector<Object> vector = new Vector<>();
        vector.add(realStockInfo.getStockCode());
        vector.add(realStockInfo.getStockName());
        vector.add(realStockInfo.getCurrentPrice());
        vector.add(realStockInfo.getIncreaseRate());
        vector.add(realStockInfo.getVolume());
        return vector;
    }

}

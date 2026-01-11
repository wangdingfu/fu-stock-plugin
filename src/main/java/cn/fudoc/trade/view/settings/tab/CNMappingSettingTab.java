package cn.fudoc.trade.view.settings.tab;

import cn.fudoc.trade.core.common.enumtype.CNMappingGroupEnum;
import cn.fudoc.trade.core.components.TableModel;
import cn.fudoc.trade.core.helper.TableHelper;
import cn.fudoc.trade.core.helper.TableListener;
import cn.fudoc.trade.core.state.FuStockSettingState;
import cn.fudoc.trade.core.state.StockGroupState;
import cn.fudoc.trade.core.state.pojo.StockGroupInfo;
import cn.fudoc.trade.util.ProjectUtils;
import cn.fudoc.trade.view.helper.HideTextHelper;
import cn.fudoc.trade.view.settings.CNMappingAddDialog;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.table.JBTable;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

/**
 * 中文映射
 */
public class CNMappingSettingTab implements SettingTab, TableListener {
    private static final String[] columnNames = {"中文", "英文", "类型"};


    protected final JBTable stockTable;
    protected final DefaultTableModel tableModel;
    private final TableHelper tableHelper;

    public CNMappingSettingTab() {
        this.tableModel = new TableModel(columnNames, this);
        stockTable = new JBTable(tableModel);
        this.tableHelper = new TableHelper(this.stockTable, this.tableModel, this);
        initData();
    }


    @Override
    public String getTabName() {
        return "中文映射";
    }

    @Override
    public JPanel createPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(tableHelper.createTablePanel());
        return mainPanel;
    }

    @Override
    public ValidationInfo doValidate() {
        return null;
    }

    @Override
    public Object[] addRow() {
        CNMappingAddDialog cnMappingAddDialog = new CNMappingAddDialog(ProjectUtils.getCurrProject());
        if (cnMappingAddDialog.showAndGet()) {
            return cnMappingAddDialog.getData();
        }
        return new Object[]{"", "", CNMappingGroupEnum.STOCK_GROUP.getGroupName()};
    }

    @Override
    public void submit() {
        FuStockSettingState instance = FuStockSettingState.getInstance();
        instance.clearCnMapping();
        Vector<Vector> dataVector = tableModel.getDataVector();
        dataVector.forEach(f -> instance.add(f.get(2).toString(), f.getFirst().toString(), f.get(1).toString()));
        for (StockGroupInfo stockGroupInfo : StockGroupState.getInstance().getGroupInfoList()) {
            String mapping = instance.mapping(CNMappingGroupEnum.STOCK_GROUP.getGroupName(), stockGroupInfo.getGroupName());
            if (StringUtils.isNoneBlank(mapping)) {
                stockGroupInfo.setHideGroupName(mapping);
            }
        }
    }

    @Override
    public void removeRow(int modelRow) {
        tableModel.removeRow(modelRow);
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return i1 != 2;
    }

    private void initData() {
        Map<String, Map<String, String>> cnMappingMap = FuStockSettingState.getInstance().getCnMappingMap();
        initTableTitle(cnMappingMap);
        cnMappingMap.forEach((key, value) -> value.forEach((itemKey, itemValue) -> tableModel.addRow(new Object[]{itemKey, itemValue, key})));
    }


    private void initTableTitle(Map<String, Map<String, String>> cnMappingMap) {
        Map<String, String> titleMapping = getTableTitleMap(cnMappingMap,CNMappingGroupEnum.TABLE_TITLE);
        Map<String, String> groupMapping = getTableTitleMap(cnMappingMap,CNMappingGroupEnum.STOCK_GROUP);
        HideTextHelper.getTableTitleMapping().forEach(titleMapping::putIfAbsent);
        HideTextHelper.getGroupMapping().forEach(groupMapping::putIfAbsent);
    }

    private Map<String, String> getTableTitleMap(Map<String, Map<String, String>> cnMappingMap,CNMappingGroupEnum cnMappingGroupEnum) {
        if (Objects.isNull(cnMappingMap)) {
            cnMappingMap = new HashMap<>();
        }
        Map<String, String> mapping = cnMappingMap.get(cnMappingGroupEnum.getGroupName());
        if (Objects.isNull(mapping)) {
            mapping = new HashMap<>();
            cnMappingMap.put(cnMappingGroupEnum.getGroupName(), mapping);
        }
        return mapping;
    }
}

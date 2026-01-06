package cn.fudoc.trade.core.state.pojo;


import cn.fudoc.trade.core.common.enumtype.GroupTypeEnum;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockGroupInfo {

    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 隐蔽分组名称
     */
    private String hideGroupName;

    /**
     * 分组类型
     */
    private GroupTypeEnum groupType;


}

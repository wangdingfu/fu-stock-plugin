package cn.fudoc.trade.view;

import cn.fudoc.trade.common.StockTabEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StockGroupInfo {

    private String groupName;

    private StockTabEnum stockTabEnum;
}

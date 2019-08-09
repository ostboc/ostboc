package cn.itcast.dao.stat;

import java.util.List;
import java.util.Map;

public interface StatDao {
    /**
     * 需求1：根据生产厂家统计销售
     */
    List<Map<String,Object>> getFactoryData(String companyId);

    /**
     * 需求2：根据产品销售排行
     */
    List<Map<String,Object>> getProductSell(String companyId);

    /**
     * 需求3：系统访问压力图
     */
    List<Map<String,Object>> getOnlineInfo(String companyId);

}

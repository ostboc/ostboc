package cn.itcast.service.stat.impl;


import cn.itcast.dao.stat.StatDao;
import cn.itcast.service.stat.StatService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

// 注意导入dubbo包：import com.alibaba.dubbo.config.annotation.Service;
@Service
public class StatServiceImpl implements StatService {

    // 注入dao
    @Autowired
    private StatDao statDao;

    @Override
    public List<Map<String, Object>> getFactoryData(String companyId) {
        return statDao.getFactoryData(companyId);
    }

    @Override
    public List<Map<String, Object>> getProductSell(String companyId) {
        return statDao.getProductSell(companyId);
    }

    @Override
    public List<Map<String, Object>> getOnlineInfo(String companyId) {
        return statDao.getOnlineInfo(companyId);
    }
}

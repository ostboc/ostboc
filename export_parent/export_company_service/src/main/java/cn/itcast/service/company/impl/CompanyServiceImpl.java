package cn.itcast.service.company.impl;

import cn.itcast.dao.company.CompanyDao;
import cn.itcast.domain.company.Company;
import cn.itcast.service.company.CompanyService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.UUID;

// 导入阿里巴巴的包：import com.alibaba.dubbo.config.annotation.Service;
@Service(timeout = 1000000)
public class CompanyServiceImpl implements CompanyService {

    // 注入dao
    @Autowired
    private CompanyDao companyDao;

    @Override
    public List<Company> findAll() {
        return companyDao.findAll();
    }

    @Override
    public PageInfo<Company> findByPage(int pageNum, int pageSize) {
        // 开启分页查询 (自动对其后的第一条查询进行分页)
        PageHelper.startPage(pageNum,pageSize);
        // 调用dao查询
        List<Company> list = companyDao.findAll();
        // 封装分页参数
        PageInfo<Company> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public void save(Company company) {
        // UUID作为主键
        company.setId(UUID.randomUUID().toString());
        // 调用dao保存
        companyDao.save(company);
    }

    @Override
    public void update(Company company) {
        companyDao.update(company);
    }

    @Override
    public Company findById(String id) {
        return companyDao.findById(id);
    }

    @Override
    public void delete(String id) {
        companyDao.delete(id);
    }
}

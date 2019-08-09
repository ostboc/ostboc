package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;
import cn.itcast.service.cargo.ContractService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.UUID;

// import com.alibaba.dubbo.config.annotation.Service;
@Service
public class ContractServiceImpl implements ContractService {

    // 注入dao
    @Autowired
    private ContractDao contractDao;

    @Override
    public PageInfo<Contract> findByPage(
            ContractExample contractExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(contractDao.selectByExample(contractExample));
    }

    @Override
    public PageInfo<Contract> selectByDeptId(String deptId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(contractDao.findByDept(deptId));
    }

    @Override
    public List<Contract> findAll(ContractExample contractExample) {
        return contractDao.selectByExample(contractExample);
    }

    @Override
    public Contract findById(String id) {
        return contractDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(Contract contract) {
        // 设置uuid作为主键
        contract.setId(UUID.randomUUID().toString());
        contract.setCreateTime(new Date());
        contract.setState(0);

        // 设置总金额为0
        contract.setTotalAmount(0D);

        // 设置货物附件数量默认都为0
        contract.setProNum(0);
        contract.setExtNum(0);

        // 保存购销合同
        contractDao.insertSelective(contract);
    }

    @Override
    public void update(Contract contract) {
        contractDao.updateByPrimaryKeySelective(contract);
    }

    @Override
    public void delete(String id) {
        contractDao.deleteByPrimaryKey(id);
    }
}

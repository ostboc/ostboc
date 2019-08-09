package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.dao.cargo.ExtCproductDao;
import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractProduct;
import cn.itcast.domain.cargo.ExtCproduct;
import cn.itcast.domain.cargo.ExtCproductExample;
import cn.itcast.service.cargo.ExtCproductService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Service(timeout = 1000000)
public class ExtCproductServiceImpl implements ExtCproductService {

    @Autowired
    private ContractDao contractDao;
    @Autowired
    private ExtCproductDao extCproductDao;

    @Override
    public PageInfo<ExtCproduct> findByPage(
            ExtCproductExample extCproductExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(extCproductDao.selectByExample(extCproductExample));
    }

    @Override
    public List<ExtCproduct> findAll(ExtCproductExample extCproductExample) {
        return extCproductDao.selectByExample(extCproductExample);
    }

    @Override
    public ExtCproduct findById(String id) {
        return extCproductDao.selectByPrimaryKey(id);
    }

    // 添加
    @Override
    public void save(ExtCproduct extCproduct) {
        extCproduct.setId(UUID.randomUUID().toString());

        //1. 计算附件金额
        Double amount = 0d;
        if (extCproduct.getCnumber()!=null && extCproduct.getPrice() != null) {
            amount = extCproduct.getCnumber() * extCproduct.getPrice();
        }
        extCproduct.setAmount(amount);

        //2. 根据购销合同id，查询购销合同
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());

        //3. 修改购销合同： 总金额、附件数
        //3.1 修改总金额
        contract.setTotalAmount(contract.getTotalAmount()+amount);
        //3.2 修改附件数
        contract.setExtNum(contract.getExtNum()+1);
        //3.3 修改保存
        contractDao.updateByPrimaryKeySelective(contract);

        //4. 添加附件
        extCproductDao.insertSelective(extCproduct);
    }

    // 修改
    @Override
    public void update(ExtCproduct extCproduct) {
        //1. 计算修改后的附件金额
        Double amount = 0d;
        if (extCproduct.getPrice() != null && extCproduct.getCnumber() != null) {
            amount = extCproduct.getPrice() * extCproduct.getCnumber();
        }
        extCproduct.setAmount(amount);


        //2. 获取修改前的附件金额
        //2.1 根据货物id查询数据库
        ExtCproduct extc =
                extCproductDao.selectByPrimaryKey(extCproduct.getId());
        //2.2 修改前的附件金额
        Double oldAmount = extc.getAmount();

        //3. 修改购销合同的总金额
        //3.1 先根据购销合同id查询
        Contract contract =
                contractDao.selectByPrimaryKey(extCproduct.getContractId());
        //3.2 修改购销合同的总金额 = 总金额 + 修改后 - 修改前
        contract.setTotalAmount(contract.getTotalAmount() + amount - oldAmount);
        //3.3 修改保存
        contractDao.updateByPrimaryKeySelective(contract);

        //4. 修改附件
        extCproductDao.updateByPrimaryKeySelective(extCproduct);
    }

    // 删除
    @Override
    public void delete(String id) {

        // 1. 先根据附件id查询
        ExtCproduct extCproduct = extCproductDao.selectByPrimaryKey(id);

        // 2. 获取附件金额
        Double amount = extCproduct.getAmount();

        // 3. 修改购销合同：总金额-附件金额、附件数
        Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
        // 3.1 购销合同总金额 = 总金额-附件金额
        contract.setTotalAmount(contract.getTotalAmount() - amount);
        // 3.2 附件数量减1
        contract.setExtNum(contract.getExtNum() - 1);
        // 3.3 修改保存
        contractDao.updateByPrimaryKeySelective(contract);

        // 4. 删除附件
        extCproductDao.deleteByPrimaryKey(id);
    }
}






















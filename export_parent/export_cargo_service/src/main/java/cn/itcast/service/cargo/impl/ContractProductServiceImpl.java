package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.dao.cargo.ContractProductDao;
import cn.itcast.dao.cargo.ExtCproductDao;
import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.vo.ContractProductVo;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

// import com.alibaba.dubbo.config.annotation.Service;
@Service(timeout = 1000000)  // 设置超时时间1000秒
public class ContractProductServiceImpl implements ContractProductService {

    @Autowired
    private ContractProductDao contractProductDao;
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private ExtCproductDao extCproductDao;

    @Override
    public PageInfo<ContractProduct> findByPage(
            ContractProductExample contractProductExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(contractProductDao.selectByExample(contractProductExample));
    }

    @Override
    public List<ContractProduct> findAll(ContractProductExample contractProductExample) {
        return contractProductDao.selectByExample(contractProductExample);
    }

    @Override
    public ContractProduct findById(String id) {
        return contractProductDao.selectByPrimaryKey(id);
    }

    // 添加货物
    @Override
    public void save(ContractProduct contractProduct) {
        //1. 设置uuid作为主键
        contractProduct.setId(UUID.randomUUID().toString());

        //2. 计算货物金额 = 单价 * 数量
        Double amount = 0d;
        if (contractProduct.getPrice() != null && contractProduct.getCnumber() != null) {
            amount = contractProduct.getPrice() * contractProduct.getCnumber();
        }
        contractProduct.setAmount(amount);

        //3. 修改购销合同的总金额
        //3.1 先根据购销合同id查询
        Contract contract =
                contractDao.selectByPrimaryKey(contractProduct.getContractId());

        //3.2 设置购销合同总金额 = 总金额  +  货物金额
        contract.setTotalAmount(contract.getTotalAmount() + amount);
        // 设置货物数量
        contract.setProNum(contract.getProNum() + 1);

        //3.3 修改购销合同
        contractDao.updateByPrimaryKeySelective(contract);

        //4. 保存货物
        contractProductDao.insertSelective(contractProduct);
    }

    // 修改货物
    @Override
    public void update(ContractProduct contractProduct) {
        //1. 计算修改后的货物金额
        Double amount = 0d;
        if (contractProduct.getPrice() != null && contractProduct.getCnumber() != null) {
            amount = contractProduct.getPrice() * contractProduct.getCnumber();
        }
        contractProduct.setAmount(amount);


        //2. 获取修改前的货物金额
        //2.1 根据货物id查询数据库
        ContractProduct cp =
                contractProductDao.selectByPrimaryKey(contractProduct.getId());
        //2.2 数据中的货物金额就是修改前的货物金额
        Double oldAmount = cp.getAmount();

        //3. 修改购销合同的总金额
        //3.1 先根据购销合同id查询
        Contract contract =
                contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //3.2 修改购销合同的总金额 = 总金额 + 修改后 - 修改前
        contract.setTotalAmount(contract.getTotalAmount() + amount - oldAmount);
        //3.3 修改保存
        contractDao.updateByPrimaryKeySelective(contract);

        //4. 修改货物
        contractProductDao.updateByPrimaryKeySelective(contractProduct);
    }

    // 删除货物
    @Override
    public void delete(String id) {
        //1. 获取货物的金额
        //1.1 根据货物id查询
        ContractProduct contractProduct = contractProductDao.selectByPrimaryKey(id);
        //1.2 获取金额
        Double productAmount = contractProduct.getAmount();

        //2. 查询货物下的附件
        ExtCproductExample example = new ExtCproductExample();
        example.createCriteria().andContractProductIdEqualTo(id);
        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(example);

        //3. 累加货物附件金额、删除附件
        //3.1 保存货物附件金额
        Double extcAmount = 0d;
        //3.2 遍历货物下的所有附件
        if (extCproductList != null && extCproductList.size()>0) {
            for (ExtCproduct extCproduct : extCproductList) {
                //3.3 累加附件金额
                extcAmount += extCproduct.getAmount();
                //3.4 删除附件
                extCproductDao.deleteByPrimaryKey(extCproduct.getId());
            }
        }

        //4. 修改购销合同
        //4.1 根据购销合同id查询
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //4.2 设置总金额
        contract.setTotalAmount(contract.getTotalAmount() - productAmount - extcAmount);
        //4.3 设置数量
        contract.setProNum(contract.getProNum()-1);
        contract.setExtNum(contract.getExtNum() - extCproductList.size());
        //4.4 更新
        contractDao.updateByPrimaryKeySelective(contract);

        //5. 删除货物
        contractProductDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<ContractProductVo> findByShipTime(String companyId, String shipTime) {
        return contractProductDao.findByShipTime(companyId,shipTime);
    }
}

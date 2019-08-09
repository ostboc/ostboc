package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.*;
import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.vo.ExportProductResult;
import cn.itcast.vo.ExportResult;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleGotoStatement;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

// import com.alibaba.dubbo.config.annotation.Service;
@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private ExportDao exportDao;
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private ContractProductDao contractProductDao;
    @Autowired
    private ExportProductDao exportProductDao;
    @Autowired
    private ExtCproductDao extCproductDao;
    @Autowired
    private ExtEproductDao extEproductDao;

    @Override
    public PageInfo<Export> findByPage(ExportExample exportExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return new PageInfo<>(exportDao.selectByExample(exportExample));
    }

    @Override
    public void updateExport(ExportResult exportResult) {
        //1. 根据报运单id查询
        Export export = exportDao.selectByPrimaryKey(exportResult.getExportId());
        //1.1 根据电子报运结果，设置：报运状态
        export.setState(exportResult.getState());
        //1.2 根据电子报运结果，设置：备注
        export.setRemark(exportResult.getRemark());

        //2. 修改报运单
        exportDao.updateByPrimaryKeySelective(export);

        //3. 修改电子报运，报运商品的交税金额
        //3.1 获取电子报运中返回的报运商品信息
        Set<ExportProductResult> products = exportResult.getProducts();
        if (products != null && products.size()>0){
            //3.2 遍历电子报运返回的商品
            for (ExportProductResult exportProductResult : products) {
                //3.3 创建商品
                ExportProduct exportProduct = new ExportProduct();
                //3.3.1 根据电子报运结果，设置：商品id
                exportProduct.setId(exportProductResult.getExportProductId());
                //3.3.2 根据电子报运结果，设置：交税金额
                exportProduct.setTax(exportProductResult.getTax());
                //3.3.3 修改商品
                exportProductDao.updateByPrimaryKeySelective(exportProduct);
            }
        }
    }

    @Override
    public Export findById(String id) {
        return exportDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(Export export) {

        //1. 报运单信息：设置uuid主键、制单日期
        export.setId(UUID.randomUUID().toString());
        export.setInputDate(new Date());
        export.setCreateTime(new Date());//创建时间
        export.setState(0);

        //2. 获取购销合同id
        String contractIds = export.getContractIds();

        //需求：对购销合同进行报运后，要修改购销合同的状态为2

        //3. 修改购销合同状态、获取合同号（多个合同号用空格隔开）
        //3.0 记录合同号
        String contractNos = "";
        //3.1 分割字符串
        String[] array = contractIds.split(",");
        if (array != null && array.length>0){
            for (String contractId : array) {
                //3.2 根据购销合同id查询
                Contract contract =
                        contractDao.selectByPrimaryKey(contractId);
                //3.3 设置状态
                contract.setState(2);
                // 修改购销合同
                contractDao.updateByPrimaryKeySelective(contract);
                //3.4 保存合同号
                contractNos += contract.getContractNo() + " ";
            }
        }
        //3.5 设置合同号
        export.setCustomerContract(contractNos);

        //4. 查询购销合同下的货物
        ContractProductExample cpExample = new ContractProductExample();
        cpExample.createCriteria().andContractIdIn(Arrays.asList(array));
        List<ContractProduct> cpList = contractProductDao.selectByExample(cpExample);

        /**
         * 需求：
         *       定义一个map集合，存储货物id及商品id
         * 为什么：
         *       因为在保存商品附件时候要拿到商品id
         * 结构：
         *      key   获取id
         *      value 商品id
         */
        Map<String,String> map = new HashMap<>();

        //5. 数据搬家： 把货物数据拷贝商品表中
        //5.1 判断，遍历货物
        if (cpList != null && cpList.size() > 0){
            for (ContractProduct cp : cpList) {

                //5.2 创建商品对象
                ExportProduct ep = new ExportProduct();
                //5.2 cp-->ep   把货物数据拷贝到商品中
                // org.springframework.beans.BeanUtils  导出spring包
                BeanUtils.copyProperties(cp,ep);

                //5.3 设置商品主键
                ep.setId(UUID.randomUUID().toString());
                //5.4 设置商品对应的报运单
                ep.setExportId(export.getId());

                //【保存商品】
                exportProductDao.insertSelective(ep);

                // 保存货物id对应的商品id
                map.put(cp.getId(),ep.getId());
            }
        }

        //6. 查询购销合同下的附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria().andContractIdIn(Arrays.asList(array));
        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(extCproductExample);

        //7. 数据搬家： 把购销合同附件数据,拷贝报运单的附件表中
        if (extCproductList != null && extCproductList.size()>0){
            // 遍历货物附件：  (货物id，购销合同id)
            for (ExtCproduct extCproduct : extCproductList) {
                //7.1 创建商品附件对象
                ExtEproduct extEproduct = new ExtEproduct();

                //7.2 封装商品附件
                //A. 拷贝
                BeanUtils.copyProperties(extCproduct,extEproduct);
                //B. 商品附件主键id
                extEproduct.setId(UUID.randomUUID().toString());
                //C. 设置附件关联的报运单id
                extEproduct.setExportId(export.getId());
                //D. 设置附件的商品id
                extEproduct.setExportProductId(map.get(extCproduct.getContractProductId()));

                //7.3 【保存商品附件】
                extEproductDao.insertSelective(extEproduct);
            }
        }

        // 设置报运单的商品数量、附件数
        export.setProNum(cpList.size());
        export.setExtNum(extCproductList.size());
        //8. 【新增】报运单
        exportDao.insertSelective(export);
    }

    @Override
    public void update(Export export) {
        //1. 先修改报运单
        exportDao.updateByPrimaryKeySelective(export);

        //2. 再修改商品
        List<ExportProduct> epList = export.getExportProducts();
        if (epList != null && epList.size() > 0){
            for (ExportProduct exportProduct : epList) {
                exportProductDao.updateByPrimaryKeySelective(exportProduct);
            }
        }
    }

    @Override
    public void delete(String id) {
        exportDao.deleteByPrimaryKey(id);
    }


}

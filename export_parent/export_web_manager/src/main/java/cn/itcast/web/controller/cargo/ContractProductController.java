package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.FactoryService;
import cn.itcast.web.controller.BaseController;
import cn.itcast.web.utils.FileUploadUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/cargo/contractProduct")
public class ContractProductController extends BaseController {


    @Reference
    private FactoryService factoryService;
    @Reference
    private ContractProductService contractProductService;
    // 注入文件上传工具类
    @Autowired
    private FileUploadUtil fileUploadUtil;

    /**
     * 1. 货物列表
     * 功能入口： 购销合同列表，点击货物
     * 请求地址：http://localhost:8080/cargo/contractProduct/list.do?contractId=5
     * 操作：
     *    A. 查询货物工厂
     *    B. 根据购销合同id，查询货物
     *    C. 保存查询结果
     * 响应地址：
     *    /WEB-INF/pages/cargo/product/product-list.jsp
     */
    @RequestMapping("/list")
    public String list(String contractId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize){
        //A. 查询货物工厂
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);

        //B. 根据购销合同id，查询货物
        ContractProductExample cpExample = new ContractProductExample();
        cpExample.createCriteria().andContractIdEqualTo(contractId);
        PageInfo<ContractProduct> pageInfo =
                contractProductService.findByPage(cpExample, pageNum, pageSize);

        //C. 保存查询结果
        request.setAttribute("factoryList",factoryList);
        request.setAttribute("pageInfo",pageInfo);
        request.setAttribute("contractId",contractId);

        return "cargo/product/product-list";
    }

    /**
     * 2. 添加或修改 货物
     * 注意：
     *   一旦表单类型是multipart/form-data,后台要求：
     *   A. 控制器要有参数MultipartFile
     *   B. 配置文件上传解析器
     * 文件上传：
     *   <input type="file" name="productPhoto" > 页面
     */
    @RequestMapping("/edit")
    public String edit(ContractProduct contractProduct, MultipartFile productPhoto) throws Exception {
        contractProduct.setCompanyId(getLoginCompanyId());
        contractProduct.setCompanyName(getLoginCompanyName());

        // 判断
        if (StringUtils.isEmpty(contractProduct.getId())){
            // 处理货物图片上传
            if (productPhoto != null){
                //String url = "http://" + fileUploadUtil.upload(productPhoto);
                //contractProduct.setProductImage(url);
            }
            // 添加
            contractProductService.save(contractProduct);
        } else {
            // 修改
            contractProductService.update(contractProduct);
        }

        // 添加或修改成功，去到货物的列表
        return "redirect:/cargo/contractProduct/list.do?contractId="+contractProduct.getContractId();
    }

    /**
     * 3. 修改货物（1）进入修改页面
     * 请求地址：http://localhost:8080/cargo/contractProduct/toUpdate.do?id=1
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        // 回显货物数据：根据货物ｉｄ查询
        ContractProduct contractProduct = contractProductService.findById(id);
        request.setAttribute("contractProduct",contractProduct);

        // 查询货物的厂家
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList",factoryList);

        return "cargo/product/product-update";
    }

    /**
     * 4. 货物删除
     * 请求地址：http://localhost:8080/cargo/contractProduct/delete.do?id=1&contractId=5
     */
    @RequestMapping("/delete")
    public String delete(String id,String contractId){
        contractProductService.delete(id);
        return "redirect:/cargo/contractProduct/list.do?contractId="+contractId;
    }

    /**
     * 5. 上传货物，给购销合同批量添加货物 (1) 进入上传页面
     * 请求地址：http://localhost:8080/cargo/contractProduct/toImport.do?contractId=2
     */
    @RequestMapping("/toImport")
    public String toImport(String contractId){
        request.setAttribute("contractId",contractId);
        return "cargo/product/product-import";
    }
    /**
     * 6. 上传货物，给购销合同批量添加货物 (1) 上传货物，批量保存
     * 请求地址：http://localhost:8080/cargo/contractProduct/toImport.do?contractId=2
     */
    @RequestMapping("/import")
    public String importExcel(String contractId,MultipartFile file) throws Exception {

        //1. 根据excel文件流，创建工作簿
        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        //2. 获取工作表
        Sheet sheet = workbook.getSheetAt(0);

        //3. 获取总行数
        int totalRows = sheet.getPhysicalNumberOfRows();

        //4. 遍历每一行, 去掉表头，从第二行开始读取
        for (int i=1; i<totalRows; i++){
            //4.1 获取每一行
            Row row = sheet.getRow(i);

            //4.2 创建对象, 读取行数据设置到对象属性中
            ContractProduct contractProduct = new ContractProduct();
            // 获取excel中录入的数据
            contractProduct.setFactoryName(row.getCell(1).getStringCellValue());
            contractProduct.setProductNo(row.getCell(2).getStringCellValue());
            contractProduct.setCnumber((int) row.getCell(3).getNumericCellValue());
            contractProduct.setPackingUnit(row.getCell(4).getStringCellValue());
            contractProduct.setLoadingRate(row.getCell(5).getNumericCellValue() + "");
            contractProduct.setBoxNum((int) row.getCell(6).getNumericCellValue());
            contractProduct.setPrice(row.getCell(7).getNumericCellValue());
            contractProduct.setProductDesc(row.getCell(8).getStringCellValue());
            contractProduct.setProductRequest(row.getCell(9).getStringCellValue());
            // 设置其他数据 - 购销合同id
            contractProduct.setContractId(contractId);
            // 设置其他数据 - 根据工厂名称查询工厂id、再设置到货物对象中
            Factory factory = factoryService.findByName(contractProduct.getFactoryName());
            if (factory != null) {
                contractProduct.setFactoryId(factory.getId());
            }

            //4.3 给购销合同添加货物，新增货物
            contractProductService.save(contractProduct);

        }


        // 上传成功，跳转到购销合同列表
        return "redirect:/cargo/contract/list.do";
    }

}















package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.ExtCproductService;
import cn.itcast.service.cargo.FactoryService;
import cn.itcast.web.controller.BaseController;
import cn.itcast.web.utils.FileUploadUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/cargo/extCproduct")
public class ExtCproductController extends BaseController {


    @Reference
    private FactoryService factoryService;
    @Reference
    private ExtCproductService extCproductService;


    /**
     * 1. 附件列表(添加)
     * 请求地址：http://localhost:8080/cargo/extCproduct/list.do
     * 请求参数：contractId=2&contractProductId=2
     * 响应地址：extc-list.jsp
     */
    @RequestMapping("/list")
    public String list(String contractId,String contractProductId,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "5") Integer pageSize) {
        //A. 查询货物工厂
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);

        //B. 根据货物id，查询附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria().andContractProductIdEqualTo(contractProductId);
        PageInfo<ExtCproduct> pageInfo =
                extCproductService.findByPage(extCproductExample, pageNum, pageSize);

        //C. 保存查询结果
        request.setAttribute("factoryList", factoryList);
        request.setAttribute("pageInfo", pageInfo);
        request.setAttribute("contractId", contractId);
        request.setAttribute("contractProductId", contractProductId);

        return "cargo/extc/extc-list";
    }

    /**
     * 2. 添加或修改附件
     */
    @RequestMapping("/edit")
    public String edit(ExtCproduct extCproduct) throws Exception {
        extCproduct.setCompanyId(getLoginCompanyId());
        extCproduct.setCompanyName(getLoginCompanyName());

        // 判断
        if (StringUtils.isEmpty(extCproduct.getId())){
            // 添加
            extCproductService.save(extCproduct);
        } else {
            // 修改
            extCproductService.update(extCproduct);
        }

        // 添加或修改成功，去到附件的列表
        return "redirect:/cargo/extCproduct/list.do?contractId="
                +extCproduct.getContractId()+"&contractProductId="+extCproduct.getContractProductId();
    }

    /**
     * 3. 进入修改页面
     * 请求地址：http://localhost:8080/cargo/extCproduct/toUpdate.do
     * 请求参数：id=6&contractId=3&contractProductId=0
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id,String contractId,String contractProductId){
        // 回显附件数据：根据附件ｉｄ查询
        ExtCproduct extCproduct = extCproductService.findById(id);
        request.setAttribute("extCproduct",extCproduct);

        // 查询附件的厂家
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList",factoryList);

        // 保存购销合同id、货物id
        request.setAttribute("contractId",contractId);
        request.setAttribute("contractProductId",contractProductId);

        return "cargo/extc/extc-update";
    }

    /**
     * 4. 附件删除
     * 请求地址：http://localhost:8080/cargo/extCproduct/delete.do
     * 请求参数：id=1&contractId=2&contractProductId=3
     */
    @RequestMapping("/delete")
    public String delete(String id,String contractId,String contractProductId){
        extCproductService.delete(id);
        return "redirect:/cargo/extCproduct/list.do?contractId="
                +contractId+"&contractProductId="+contractProductId;
    }
}
package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.service.cargo.ExportProductService;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.vo.ExportProductVo;
import cn.itcast.vo.ExportResult;
import cn.itcast.vo.ExportVo;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/cargo/export")
public class ExportController extends BaseController {

    // 注入购销合同service服务接口
    @Reference
    private ContractService contractService;
    @Reference
    private ExportService exportService;
    @Reference
    private ExportProductService exportProductService;

    /**
     * 1. 合同管理
     * 需求：显示已上报的购销合同，状态为1
     * 请求：http://localhost:8080/cargo/export/contractList.do
     */
    @RequestMapping("/contractList")
    public String contractList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        // 构造条件
        ContractExample example = new ContractExample();
        ContractExample.Criteria criteria = example.createCriteria();
        criteria.andCompanyIdEqualTo(getLoginCompanyId());
        criteria.andStateEqualTo(1);
        // 分页查询
        PageInfo<Contract> pageInfo =
                contractService.findByPage(example, pageNum, pageSize);
        // 保存
        request.setAttribute("pageInfo",pageInfo);
        return "cargo/export/export-contractList";

    }

    /**
     * 2. 出口报运单列表
     * 地址：http://localhost:8080/cargo/export/list.do
     */
    @RequestMapping("/list")
    public String list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        // 构造条件
        ExportExample example = new ExportExample();
        ExportExample.Criteria criteria = example.createCriteria();
        criteria.andCompanyIdEqualTo(getLoginCompanyId());

        // 分页查询
        PageInfo<Export> pageInfo =
                exportService.findByPage(example, pageNum, pageSize);
        request.setAttribute("pageInfo",pageInfo);

        return "cargo/export/export-list";
    }

    /**
     * 3. 报运单新增（1）进入添加页面
     * 请求地址：http://localhost:8080/cargo/export/toExport.do
     * 参数：   id
     */
    @RequestMapping("/toExport")
    public String toExport(String id){
        request.setAttribute("id",id);
        return "cargo/export/export-toExport";
    }

    /**
     * 4. 添加或修改报运单
     * 添加提交的关键数据：contractIds  购销合同的id,多个用逗号隔开
     */
    @RequestMapping("/edit")
    public String edit(Export export) throws Exception {
        export.setCompanyId(getLoginCompanyId());
        export.setCompanyName(getLoginCompanyName());

        // 判断
        if (StringUtils.isEmpty(export.getId())){
            exportService.save(export);
        } else {
            // 修改
            exportService.update(export);
        }

        // 添加或修改成功，去到货物的列表
        return "redirect:/cargo/export/list.do";
    }

    /**
     * 5. 进入报运单修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id) {
        //1. 根据报运单id查询
        Export export = exportService.findById(id);
        request.setAttribute("export",export);

        //2. 查询报运单的商品，查询条件：报运单id
        ExportProductExample epExample = new ExportProductExample();
        epExample.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> list = exportProductService.findAll(epExample);
        request.setAttribute("eps",list);

        return "cargo/export/export-update";
    }

    /**
     * 6. 提交、取消
     * http://localhost:8080/cargo/export/submit.do?id=0
     * http://localhost:8080/cargo/export/cancel.do?id=0
     */
    @RequestMapping("/submit")
    public String submit(String id){
        Export export = new Export();
        export.setId(id);
        export.setState(1);
        // 修改
        exportService.update(export);
        return "redirect:/cargo/export/list.do";
    }

    @RequestMapping("/cancel")
    public String cancel(String id){
        Export export = new Export();
        export.setId(id);
        export.setState(0);
        // 修改
        exportService.update(export);
        return "redirect:/cargo/export/list.do";
    }

    /**
     * 7. 电子报运
     * http://localhost:8080/cargo/export/exportE.do?id=2
     */
    @RequestMapping("/exportE")
    public String exportE(String id){
        //7.1 创建ExportVo对象，封装webservice请求参数
        ExportVo exportVo = new ExportVo();
        //a. 根据报运单id查询
        Export export = exportService.findById(id);
        //b. 对象拷贝
        BeanUtils.copyProperties(export,exportVo);
        //c. 设置报运单id
        exportVo.setExportId(id);

        //d. 封装exportVo中的报运单的商品
        List<ExportProductVo> products = exportVo.getProducts();
        //e. 根据报运单id查询商品
        ExportProductExample exportProductExample = new ExportProductExample();
        exportProductExample.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> list = exportProductService.findAll(exportProductExample);
        //f. 把报运单的商品信息拷贝到电子报运的商品集合中： list--->products
        if (list != null && list.size()>0){
            for (ExportProduct exportProduct : list) {
                //f1. 创建vo对象
                ExportProductVo epVo = new ExportProductVo();
                //f2. 封装epVo
                BeanUtils.copyProperties(exportProduct,epVo);
                epVo.setExportId(id);
                epVo.setExportProductId(exportProduct.getId());

                //f3. epVo添加到集合
                products.add(epVo);
            }
        }

        //7.2 电子报运（1）电子报运：远程访问海关平台进行报运
        WebClient
                .create("http://192.168.88.32:8082/ws/export/user")
                .post(exportVo);
        //7.3 电子报运（2）远程访问海关平台，获取报运结果
        ExportResult exportResult =
                WebClient
                    .create("http://192.168.88.32:8082/ws/export/user/"+id)
                    .get(ExportResult.class);

        //7.4 调用service，根据报运结果（exportResult）修改报运单信息
        exportService.updateExport(exportResult);

        return "redirect:/cargo/export/list.do";
    }

}
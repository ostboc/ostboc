package cn.itcast.web.controller.company;

import cn.itcast.domain.company.Company;
import cn.itcast.service.company.CompanyService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/company")
public class CompanyController {

    // 注入service
    @Reference
    private CompanyService companyService;

    /**
     * 1. 查询全部
     * 请求路径： http://localhost:8080/company/list.do
     * 响应路径： /WEB-INF/pages/company/company-list.jsp
     */
    @RequestMapping("/list")
    public String list(Model model){
        // 调用serivce
        List<Company> list = companyService.findAll();
        // 保存查询结果
        model.addAttribute("list",list);
        // 跳转
        return "company/company-list";
    }

    /**
     * 2. 进入添加页面
     * 请求地址：http://localhost:8080/company/toAdd.do
     * 响应地址：/WEB-INF/pages/company/company-add.jsp
     */
    @RequestMapping("/toAdd")
    public String toAdd(){
        return "company/company-add";
    }

    /**
     * 3. 添加/修改企业
     * 请求地址：http://localhost:8080/company/edit.do
     */
    @RequestMapping("/edit")
    public String edit(Company company){
        // 根据id判断添加还是修改
        if (StringUtils.isEmpty(company.getId())){
            // 添加
            companyService.save(company);
        } else {
            // 修改
            companyService.update(company);
        }
        // 操作成功，去到列表重新查询
        return "redirect:/company/list.do";
    }

    /**
     * 4. 进入修改页面
     * 请求地址：http://localhost:8080/company/toUpdate.do
     * 请求参数：id=1
     * 响应地址：/WEB-INF/pages/company/company-update.jsp
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id){
        // 调用service，根据id查询
        Company company = companyService.findById(id);
        // 保存结果到request域
        ModelAndView mv = new ModelAndView();
        mv.addObject("company",company);
        mv.setViewName("company/company-update");
        return mv;
    }

    /**
     * 5. 删除
     * 请求地址：http://localhost:8080/company/delete.do
     * 请求参数：id=2
     * 响应地址：/company/list.do
     */
    @RequestMapping("/delete")
    public String delete(String id){
        // 调用service删除
        companyService.delete(id);
        return "redirect:/company/list.do";
    }












    /**
     * 测试： 类型转换
     * 请求路径：
     *      http://localhost:8080/company/save.do?birth=1998-09-09
     * 页面访问结果：
     *      HTTP Status 400 – Bad Request
     * 后台日志：
     *      Failed to convert value of type 'java.lang.String' to required type 'java.util.Date';
     * 原因：
     *      SpringMVC不能自动把页面String转换为后台的Date类型
     * 解决：
     *      1. 写一个类，实现Converter接口
     *      2. springmvc.xml配置转换器的工厂
     */
    @RequestMapping("/save")
    public String save(Date birth){
        System.out.println(birth);
        int i = 1/0;
        return "company/company-list";
    }
}

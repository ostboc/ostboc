package cn.itcast.web;

import cn.itcast.domain.company.Company;
import cn.itcast.service.company.CompanyService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApplyController {

    /**
     * 注入dubbo的服务类（代理对象）
     * 注意：导入的包
     */
    @Reference(retries = 0)
    private CompanyService companyService;

    /**
     * 企业入驻
     */
    @RequestMapping("/apply")
    @ResponseBody   // 返回json字符串
    public String apply(Company company){
        String result = "";
        try {
            // 远程调用service
            companyService.save(company);
            result = "1";  // 操作成功
        } catch (Exception e) {
            e.printStackTrace();
            result = "0";  // 操作失败
        }
        return result;
    }
}

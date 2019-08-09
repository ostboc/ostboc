package cn.itcast.web.stat;

import cn.itcast.service.stat.StatService;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stat")
public class StatController extends BaseController{

    @Reference
    private StatService statService;

    /**
     * 进入到统计分析页面
     * http://localhost:8080/stat/toCharts.do?chartsType=factory
     * http://localhost:8080/stat/toCharts.do?chartsType=sell
     * http://localhost:8080/stat/toCharts.do?chartsType=online
     */
    @RequestMapping("/toCharts")
    public String toCharts(String chartsType){
        return "/stat/stat-" + chartsType;
    }
    /**
     * 需求1：生产厂家销售统计
     */
    @RequestMapping("/getFactoryData")
    @ResponseBody   // 自动把方法返回的对象转换为json格式
    public List<Map<String, Object>> getFactoryData(){
        List<Map<String, Object>> list =
                statService.getFactoryData(getLoginCompanyId());
        return list;
    }

    /**
     * 需求2：产品销售排行前5
     */
    @RequestMapping("/getProductSell")
    @ResponseBody
    public List<Map<String, Object>> getProductSell(){
        List<Map<String, Object>> list =
                statService.getProductSell(getLoginCompanyId());
        return list;
    }


    /**
     * 需求3：系统访问压力图
     */
    @RequestMapping("/getOnlineInfo")
    @ResponseBody
    public List<Map<String, Object>> getOnlineInfo(){
        List<Map<String, Object>> list =
                statService.getOnlineInfo(getLoginCompanyId());
        return list;
    }
}

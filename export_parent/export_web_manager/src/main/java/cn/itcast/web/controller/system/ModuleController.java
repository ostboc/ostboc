package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Module;
import cn.itcast.service.system.ModuleService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/system/module")
public class ModuleController extends BaseController{

    // 注入service
    @Autowired
    private ModuleService moduleService;

    /**
     * 1. 分页列表
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize){
        // 当前登陆者的所属企业id，后面实现完登陆后从当前登陆用户中获取。
        String companyId = getLoginCompanyId();

        // 调用service分页查询
        PageInfo<Module> pageInfo =
                moduleService.findByPage(pageNum,pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("system/module/module-list");

        return mv;
    }

    /**
     * 2. 进入添加页面
     * 请求地址：http://localhost:8080/system/module/toAdd.do
     */
    @RequestMapping("/toAdd")
    public String toAdd(){
        List<Module> moduleList = moduleService.findAll();
        request.setAttribute("menus",moduleList);
        return "system/module/module-add";
    }

    /**
     * 3. 添加或修改
     */
    @RequestMapping("/edit")
    public String edit(Module module){

        // 判断
        if (StringUtils.isEmpty(module.getId())){
            // 添加
            moduleService.save(module);
        } else {
            // 修改
            moduleService.update(module);
        }


        return "redirect:/system/module/list.do";
    }

    /**
     * 4. 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        List<Module> moduleList = moduleService.findAll();
        request.setAttribute("menus",moduleList);

        // 回显用户数据
        Module module = moduleService.findById(id);
        request.setAttribute("module",module);
        return "system/module/module-update";
    }

    /**
     * 5. 删除
     */
    @RequestMapping("/delete")
    public String delete(String id){
        moduleService.delete(id);
        return "redirect:/system/module/list.do";
    }

}

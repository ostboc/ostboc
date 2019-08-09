package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.service.system.DeptService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/dept")
public class DeptController extends BaseController{

    // 注入service
    @Autowired
    private DeptService deptService;

    /**
     * 1. 部门分页列表
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize){
        // 当前登陆者的所属企业id，后面实现完登陆后从当前登陆用户中获取。
        String companyId = getLoginCompanyId();

        // 调用service分页查询
        PageInfo<Dept> pageInfo =
                deptService.findByPage(companyId,pageNum,pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("system/dept/dept-list");

        return mv;
    }

    /**
     * 2. 进入添加页面
     * 请求地址：http://localhost:8080/system/dept/toAdd.do
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(){
        String companyId = getLoginCompanyId();
        // 查询所有部门
        List<Dept> deptList = deptService.findAll(companyId);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.setViewName("system/dept/dept-add");
        mv.addObject("deptList",deptList);
        //request.setAttribute("deptList",deptList);
        return mv;
    }

    /**
     * 3. 添加或修改
     */
    @RequestMapping("/edit")
    public String edit(Dept dept){
        // 准备当前登陆用户的企业信息（写死）
        dept.setCompanyId(getLoginCompanyId());
        dept.setCompanyName(getLoginCompanyName());

        // 判断
        if (StringUtils.isEmpty(dept.getId())){
            // 添加
            deptService.save(dept);
        } else {
            // 修改
            deptService.update(dept);
        }


        return "redirect:/system/dept/list.do";
    }

    /**
     * 4. 进入修改页面
     * 请求地址：http://localhost:8080/system/dept/toUpdate.do?id=0
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id){
        // 调用service，根据id查询
        Dept dept = deptService.findById(id);
        // 查询所有部门
        List<Dept> deptList = deptService.findAll("1");

        // 保存结果到request域
        ModelAndView mv = new ModelAndView();
        mv.addObject("dept",dept);
        mv.addObject("deptList",deptList);
        mv.setViewName("system/dept/dept-update");
        return mv;
    }

    /**
     * 5. 删除
     */
    @RequestMapping("/delete")
    @ResponseBody  // 引入jackson后自动把方法返回的对象转json输出。
    public Map<String,Object> delete(String id){
        // 定义方法返回结果
        Map<String,Object> result = new HashMap<>();

        // 调用service
        boolean flag = deptService.delete(id);

        if (flag){
            // 删除成功
            result.put("message","删除成功！");//{"message":"..."}
        } else {
            result.put("message","删除失败，删除的部门有被外键引用！");
        }

        return result;
    }

}

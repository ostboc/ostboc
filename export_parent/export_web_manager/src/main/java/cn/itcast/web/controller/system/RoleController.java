package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.Role;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.RoleService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/role")
public class RoleController extends BaseController{

    // 注入service
    @Autowired
    private RoleService roleService;
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
        PageInfo<Role> pageInfo =
                roleService.findByPage(companyId,pageNum,pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("system/role/role-list");

        return mv;
    }

    /**
     * 2. 进入添加页面
     * 请求地址：http://localhost:8080/system/role/toAdd.do
     */
    @RequestMapping("/toAdd")
    public String toAdd(){
        return "system/role/role-add";
    }

    /**
     * 3. 添加或修改
     */
    @RequestMapping("/edit")
    public String edit(Role role){
        // 准备当前登陆用户的企业信息（写死）
        role.setCompanyId(getLoginCompanyId());
        role.setCompanyName(getLoginCompanyName());

        // 判断
        if (StringUtils.isEmpty(role.getId())){
            // 添加
            roleService.save(role);
        } else {
            // 修改
            roleService.update(role);
        }


        return "redirect:/system/role/list.do";
    }

    /**
     * 4. 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        // 回显用户数据
        Role role = roleService.findById(id);
        request.setAttribute("role",role);
        return "system/role/role-update";
    }

    /**
     * 5. 删除
     */
    @RequestMapping("/delete")
    public String delete(String id){
        roleService.delete(id);
        return "redirect:/system/role/list.do";
    }

    /**
     * 6. 角色分配权限（1）进入角色权限页面
     * 请求地址：http://localhost:8080/system/role/roleModule.do?roleid=4
     * 响应地址：/WEB-INF/pages/system/role/role-module.jsp
     */
    @RequestMapping("/roleModule")
    public String roleModule(String roleId){
        // 根据角色id查询
        Role role = roleService.findById(roleId);
        // 保存角色对象
        request.setAttribute("role",role);
        return "system/role/role-module";
    }

    /**
     * 6. 角色分配权限（2）role-module.jsp页面发送异步请求，返回ztree的json格式数据
     * ztreeJson = [
     *     { id:2, pId:0, name:"随意勾选 2", checked:true, open:true}
     * ]
     */
    @RequestMapping("/getZtreeNodes")
    @ResponseBody
    public List<Map<String,Object>> getZtreeNodes(String roleId){
        //1. 定义返回结果
        List<Map<String,Object>> result = new ArrayList<>();

        //2. 查询所有的权限
        List<Module> moduleList = moduleService.findAll();

        //3. 查询角色已经具有的权限
        List<Module> roleModuleList =
                moduleService.findRoleModulesByRoleId(roleId);

        //4. 遍历所有权限
        for(Module module : moduleList){
            //4.1 构造map
            Map<String,Object> map = new HashMap<>();
            //4.2 封装map:{ id:2, pId:0, name:"随意勾选 2", checked:true, open:true}
            map.put("id",module.getId());
            map.put("pId",module.getParentId());
            map.put("name",module.getName());
            map.put("open",true);
            if (roleModuleList.contains(module)){
                // 说明当然角色已经具有该权限，需要默认选中
                map.put("checked",true);
            }
            //4.3 把封装好的map添加到集合
            result.add(map);
        }
        return result;
    }

    /**
     * 6. 角色分配权限（3） 保存角色权限
     */
    @RequestMapping("/updateRoleModule")
    public String updateRoleModule(String roleId,String moduleIds){
        // 调用service保存
        roleService.updateRoleModule(roleId,moduleIds);
        return "redirect:/system/role/list.do";
    }

}

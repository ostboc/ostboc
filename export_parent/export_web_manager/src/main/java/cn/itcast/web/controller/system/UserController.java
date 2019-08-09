package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.domain.system.Role;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.RoleService;
import cn.itcast.service.system.UserService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
@RequestMapping("/system/user")
public class UserController extends BaseController{

    // 注入service
    @Autowired
    private UserService userService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private RoleService roleService;


    /**
     * 1. 分页列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("用户管理")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize){

        /**
         * 权限校验
        Subject subject = SecurityUtils.getSubject();
        // 参数：访问资源需要的权限
        subject.checkPermission("用户管理");
         */

        // 当前登陆者的所属企业id，后面实现完登陆后从当前登陆用户中获取。
        String companyId = getLoginCompanyId();

        // 调用service分页查询
        PageInfo<User> pageInfo =
                userService.findByPage(companyId,pageNum,pageSize);
        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("system/user/user-list");

        return mv;
    }

    /**
     * 2. 进入添加页面
     * 请求地址：http://localhost:8080/system/user/toAdd.do
     */
    @RequestMapping("/toAdd")
    public String toAdd(){
        // 进入用户添加页面，要显示所有部门
        List<Dept> deptList = deptService.findAll(getLoginCompanyId());
        // 保存
        request.setAttribute("deptList",deptList);
        return "system/user/user-add";
    }

    /**
     * 3. 添加或修改
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/edit")
    public String edit(User user){
        // 准备当前登陆用户的企业信息（写死）
        user.setCompanyId(getLoginCompanyId());
        user.setCompanyName(getLoginCompanyName());

        // 判断
        if (StringUtils.isEmpty(user.getId())){
            // 添加
            userService.save(user);
            // 获取邮箱
            String email = user.getEmail();
            if (email != null && !"".equals(email)){
                // 主题
                String subject = "新员工入职通知邮件！";
                // 内容
                String content = "欢迎来到SassExport大家庭，我们喜欢996哦！";
                // 发送消息到消息容器
                Map<String,String> map = new HashMap<>();
                map.put("email",email);
                map.put("subject",subject);
                map.put("content",content);
                rabbitTemplate.convertAndSend("msg.email",map);
            }
        } else {
            // 修改
            userService.update(user);
        }


        return "redirect:/system/user/list.do";
    }

    /**
     * 4. 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        // 进入用户添加页面，要显示所有部门
        List<Dept> deptList = deptService.findAll(getLoginCompanyId());
        // 保存
        request.setAttribute("deptList",deptList);

        // 回显用户数据
        User user = userService.findById(id);
        request.setAttribute("user",user);
        return "system/user/user-update";
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
        boolean flag = userService.delete(id);

        if (flag){
            // 删除成功
            result.put("message","删除成功！");//{"message":"..."}
        } else {
            result.put("message","删除失败，删除的部门有被外键引用！");
        }

        return result;
    }

    /**
     * 6.用户分配角色（1）进入用户角色页面
     * 功能入口： user-list.jsp
     * 访问地址： http://localhost:8080/system/user/roleList.do?id=0
     * 响应地址： /WEB-INF/pages/system/user/user-role.jsp
     */
    @RequestMapping("/roleList")
    public String roleList(String id){
        //1. 根据用户id查询用户
        User user = userService.findById(id);

        //2. 查询所有角色
        List<Role> roleList = roleService.findAll(getLoginCompanyId());

        //3. 查询用户已经具有的角色
        List<Role> userRoleList = roleService.findUserRoleByUserId(id);

        // 定义一个字符串保存用户的所有角色，用逗号隔开
        String userRole = "";  // "船运专员,报运经理,"
        if (userRoleList != null && userRoleList.size()>0){
            for (Role role : userRoleList) {
                userRole += role.getName() + ",";
            }
        }

        //4. 保存数据
        request.setAttribute("user",user);
        request.setAttribute("roleList",roleList);
        request.setAttribute("userRole",userRole);

        return "system/user/user-role";
    }


    /**
     * 7.用户分配角色（2）用户分配角色
     * 访问地址： http://localhost:8080/system/user/changeRole.do
     * 请求参数： userId, 多个roleIds
     */
    @RequestMapping("/changeRole")
    public String changeRole(String userId,String[] roleIds){
        userService.changeRole(userId,roleIds);
        return "redirect:/system/user/list.do";
    }
}

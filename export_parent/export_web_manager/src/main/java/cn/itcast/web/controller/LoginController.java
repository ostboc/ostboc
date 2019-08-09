package cn.itcast.web.controller;

import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class LoginController extends BaseController{

    // 注入serivce
    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;

    /**
     * 执行流程：
     * 1. 访问首页： http://localhost:8080/index.jsp
     * 2. index.jsp 页面内容： location.href = "login.do"
     * 3. 从session中获取登陆用户，没有登陆就跳转到登陆页面 。 （先不实现）
     * 4. 跳转到主页：/WEB-INF/pages/home/main.jsp
     *    main.jsp--> <iframe src="/home.do">

    @RequestMapping("/login")
    public String login(String email,String password){
        // A. 判断
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password)){
            // 转发到登陆页面
            return "forward:/login.jsp";
        }

        // B. 根据Email查询
        User user = userService.findByEmail(email);

        // C. 判断
        if (user != null){
            if (password.equals(user.getPassword())){
                // 登陆成功
                // 保存用户信息到session
                session.setAttribute("userInfo",user);

                // 查询用户已经拥有的"权限"，保存到session中
                List<Module> moduleList = moduleService.findModuleByUserId(user.getId());
                session.setAttribute("moduleList",moduleList);

                // 跳转到主页
                return "home/main";
            } else {
                request.setAttribute("error","账号或密码错误！");
                return "forward:/login.jsp";
            }
        } else {
            // email不存在
            request.setAttribute("error","账号或密码错误！");
            return "forward:/login.jsp";
        }
    }
     */

    @RequestMapping("/login")
    public String login(String email,String password) {
        // 判断
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            // 转发到登陆页面
            return "forward:/login.jsp";
        }

        try {
            // 获取subject对象，相当于"用户"
            Subject subject = SecurityUtils.getSubject();
            // 创建token，封装的是账号密码
            AuthenticationToken token = new UsernamePasswordToken(email,password);
            // shiro认证 (认证失败会报错。)
            subject.login(token); //--->realm中的认证方法

            // 获取认证对象. subject.getPrincipal() 获取的就是realm中认证方法返回值对象构造器的第一个参数。
            User user = (User) subject.getPrincipal();
            // 认证成功
            session.setAttribute("userInfo",user);

            // 查询用户已经拥有的"权限"，保存到session中
            List<Module> moduleList = moduleService.findModuleByUserId(user.getId());
            session.setAttribute("moduleList",moduleList);

            // 跳转到主页
            return "home/main";
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error","账号或密码错误！");
            return "forward:/login.jsp";
        }
    }



    /**
     * 5. 从main.jsp的iframe，进入home.jsp
     * 请求入口： main.jsp
     * 响应页面： /WEB-INF/pages/home/home.jsp
     */
    @RequestMapping("/home")
    public String home(){
        return "home/home";
    }

    /**
     * 注销
     */
    @RequestMapping("/logout")
    public String logout(){
        // 从session中移除登陆用户信息
        session.removeAttribute("userInfo");
        // 销毁session
        session.invalidate();
        // 注销后跳转到登陆页面
        return "forward:/login.jsp";
    }
}

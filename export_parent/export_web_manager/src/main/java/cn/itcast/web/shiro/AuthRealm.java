package cn.itcast.web.shiro;

import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Shiro的realm类，用来访问数据库数据，返回认证授权的安全数据。
 */
public class AuthRealm extends AuthorizingRealm{

    // 注入service
    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;


    /**
     * Shiro的认证方法 （登陆时候会自动来到realm的认证方法）
     * 认证失败的2个异常：
     *  1. UnknownAccountException  用户名错
     *  2. IncorrectCredentialsException 密码错
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        // 获取登陆的用户名，就是输入的email
        String email = (String) token.getPrincipal();

        // 调用service根据email查询
        User user = userService.findByEmail(email);

        // 判断
        if (user == null){
            // 只要认证方法返回NULL，就是用户名不存在的错误。
            // UnknownAccountException
            return null;
        }

        // 说明用户名存在 （通常接下来校验密码，但是不需要自己校验，交给shiro自动校验密码即可）
        // 数据库中正确的密码
        String pwd = user.getPassword();

        // 返回
        // 参数1： 存储的身份信息。通过subject.getPrincipal()获取的就是这里的参数1
        // 参数2： 数据库中正确的密码
        // 参数3： realm的名称，可以任意。保证唯一。getName()使用的是默认的名称。
        SimpleAuthenticationInfo sai =
                new SimpleAuthenticationInfo(user,pwd,getName());
        return sai;
    }

    /**
     * Shiro的授权方法（权限校验时候会自动来到shiro的授权方法）
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        /*查询用户具有的权限*/
        //1. 获取用户身份对象
        User user = (User) principals.getPrimaryPrincipal();

        //2. 根据用户id查询用户已经拥有的权限
        List<Module> moduleList = moduleService.findModuleByUserId(user.getId());

        //3. 返回
        SimpleAuthorizationInfo sai = new SimpleAuthorizationInfo();
        // 遍历用户的权限
        if (moduleList != null && moduleList.size()>0){
            for (Module module : moduleList) {
                // 返回用户的权限
                sai.addStringPermission(module.getName());
            }
        }
        return sai;
    }

}

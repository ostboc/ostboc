package cn.itcast.web.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * 自定义的凭证匹配器，对用户输入的密码进行按照md5加密，加盐。
 * 密码：
 *  1. md5加密
 *  2. 把邮箱作为salt盐
 */
public class CustomCredentialsMatcher extends SimpleCredentialsMatcher{

    /**
     * 自己对用户输入的密码进行加密，以及判断
     * @param token 封装用户输入的账号密码信息
     * @param info  数据中的正确的密码信息
     * @return
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //1. 获取用户输入的邮箱
        String email = (String) token.getPrincipal();

        //2. 获取用户输入的密码
        String password = new String((char[]) token.getCredentials());

        //3. 加密，加盐 【登陆时候，先对数据库的密码加密，怎么加密根据这里的算法决定：加盐加密】
        String encodePwd = new Md5Hash(password,email).toString();

        //4. 获取数据库中的密码
        String dbPwd = (String) info.getCredentials();

        // 判断
        return encodePwd.equals(dbPwd);
    }
}

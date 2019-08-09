package cn.itcast.shiro;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.Test;

public class App {
    /**
     * md5不去安全,因为每次加密结果固定
     *
     * 明文            密文       彩虹表，撞库
     *  1       c4ca4238a0b923820dcc509a6f75849b
     */
    @Test
    public void md5(){
        Md5Hash hash = new Md5Hash("1");
        //c4ca4238a0b923820dcc509a6f75849b
        System.out.println(hash.toString());
    }

    // 加密，加盐
    @Test
    public void md5Salt(){
        // 15bd5a41cd64e1f6d8ef02f11cfb5edb
        Md5Hash hash = new Md5Hash("1","admin@export.com");
        System.out.println(hash.toString());
    }

    // 加密，加盐, 加迭代次数
    @Test
    public void md5SaltIteration(){
        Md5Hash hash = new Md5Hash("1","admin@export.com",1000000);
        System.out.println(hash.toString());
    }

    // 加密，加随即盐
    @Test
    public void md5_2(){
        //a. md5加密
        System.out.println("加密：" + new Md5Hash("123456").toString());

        //b. 加盐
        // 创建安全随机数生成器对象
        SecureRandomNumberGenerator srn = new SecureRandomNumberGenerator();
        // 定义加密盐 (md5加密后的32位长度的字符串)
        String salt = srn.nextBytes().toHex();
        System.out.println("随机生成" + salt);
        Md5Hash md5Hash = new Md5Hash("123456",salt);
        System.out.println("加盐加密： " +md5Hash.toString());

    }


    @Test
    public void sha() {
        //a. md5加密
        System.out.println("加密：" + new Sha256Hash("123456").toString());
        System.out.println("加密：" + new Sha256Hash("123456").toString().length());
    }

}

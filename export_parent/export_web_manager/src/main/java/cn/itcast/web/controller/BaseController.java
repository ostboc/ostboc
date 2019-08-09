package cn.itcast.web.controller;

import cn.itcast.domain.system.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BaseController {

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected HttpSession session;

    /**
     * 获取当前登陆用户的所属企业id；从session中获取。
     * @return
     */
    public String getLoginCompanyId(){
        return getLoginUser().getCompanyId();
    }

    /**
     * 获取当前登陆用户的企业名称
     * @return
     */
    public String getLoginCompanyName(){
        return getLoginUser().getCompanyName();
    }

    //从session中获取登陆用户对象
    protected User getLoginUser(){
        return (User) session.getAttribute("userInfo");
    }
}

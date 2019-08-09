package cn.itcast.web.exception;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SpringMVC提供的异常处理类：
 * 1. 实现HandlerExceptionResolver接口
 * 2. 创建当前异常类，加入容器
 */
@Component
public class CustomerException implements HandlerExceptionResolver{
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler, Exception ex) {
        // 打印异常信息
        ex.printStackTrace();

        // 返回
        ModelAndView mv = new ModelAndView();
        mv.setViewName("error");
        mv.addObject("errorMsg","系统忙，稍后再试或联系管理员：120!" + ex.getMessage());
        return mv;
    }
}

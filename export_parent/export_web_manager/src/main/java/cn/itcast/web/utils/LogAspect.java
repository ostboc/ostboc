package cn.itcast.web.utils;
import java.util.Date;
import java.util.UUID;

import cn.itcast.domain.system.SysLog;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.SysLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 访问controller中方法，自动记录日志的切面类/工具类/通知类
 */
@Component
@Aspect
public class LogAspect {

    // 注入日志的service
    @Autowired
    private SysLogService logService;
    // 注入request
    @Autowired
    private HttpServletRequest request;

    /**
     * 访问controller中方法，自动记录日志
     * @param pjp 获取当前执行的方法信息、目标对象
     */
    @Around("execution(* cn.itcast.web.controller..*.*(..))")
    public Object insertLog(ProceedingJoinPoint pjp){
        SysLog log = new SysLog();
        log.setId(UUID.randomUUID().toString());
        log.setTime(new Date());
        log.setIp(request.getRemoteAddr());

        // 先获取session: 参数默认为true表示创建或者获取session；false表示只获取
        HttpSession session = request.getSession(false);
        if (session != null){
            User user = (User) session.getAttribute("userInfo");
            if (user != null){
                // 设置用户名称、用户企业
                log.setUserName(user.getUserName());
                log.setCompanyId(user.getCompanyId());
                log.setCompanyName(user.getCompanyName());
            }
        }

        // 获取当前执行的控制器类全名
        String className = pjp.getTarget().getClass().getName();
        // 获取当前执行的控制器方法名称
        String methodName = pjp.getSignature().getName();
        log.setAction(className);
        log.setMethod(methodName);

        try {
            // 调用service，记录日志
            logService.save(log);

            // 放行，执行控制器方法
            Object retV = pjp.proceed();
            return retV;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

}

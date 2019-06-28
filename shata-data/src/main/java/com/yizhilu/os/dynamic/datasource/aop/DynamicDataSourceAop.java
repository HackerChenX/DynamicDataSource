package com.yizhilu.os.dynamic.datasource.aop;

import com.yizhilu.os.dynamic.datasource.admin.DataSourceManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 数据源切换 AOP
 * 拦截Dubbo接口方法，获取参数列表中的数据源Key（第一个参数），并存放到ThreadLocal里
 */
@Component
@Aspect
public class DynamicDataSourceAop {

    @Pointcut("execution(public * com.yizhilu.os.service.*.dubbo.*ServiceImpl.*(..))")
    private void pointcut(){}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point){
        try {
            Object[] args = point.getArgs();
            if (!DataSourceManager.setCurrentLookupKey(args[0])) {
                throw new Exception("dubbo接口dbKey参数错误");
            }
            Object result = point.proceed();
            DataSourceManager.clean();
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }
}

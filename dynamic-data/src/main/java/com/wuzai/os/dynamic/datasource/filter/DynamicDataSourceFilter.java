package com.wuzai.os.dynamic.datasource.filter;

import com.wuzai.os.dynamic.constants.BaseCommonConstants;
import com.wuzai.os.dynamic.datasource.admin.DataSourceManager;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 数据源切换过滤器
 * 通过请求参数或Cookie中获取数据源Key，并存放到ThreadLocal里
 * 获取不到Key的时候返回403页面
 */
@WebFilter(urlPatterns = {"/app/*","/pc/*","/mobileApp/*","/web/*", "/admin/*"}, filterName = "dynamicDataSourceFilter")
public class DynamicDataSourceFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String dbKey = request.getParameter(BaseCommonConstants.dbKey);
        if (!DataSourceManager.setCurrentLookupKey(dbKey)) {
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.sendError(403);
            return;
        }
        filterChain.doFilter(request, response);
        request.setAttribute("dbKey", BaseCommonConstants.dbKey);
        DataSourceManager.clean();
    }

    @Override
    public void destroy() {

    }

}

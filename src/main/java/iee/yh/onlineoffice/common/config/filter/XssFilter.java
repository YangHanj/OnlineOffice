package iee.yh.onlineoffice.common.config.filter;

import iee.yh.onlineoffice.common.config.xss.XssHttpRequestWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author yanghan
 * @date 2022/4/21
 */
@WebFilter("/*")
public class XssFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        XssHttpRequestWrapper wrapper = new XssHttpRequestWrapper(httpServletRequest);
        filterChain.doFilter(wrapper,servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}

package com.demo.activiti.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 自定义Filter
 * 对请求的header 过滤token
 * 过滤器Filter可以拿到原始的HTTP请求和响应的信息，
 * 但是拿不到你真正处理请求方法的信息，也就是方法的信息
 * 拦截顺序：filter—>Interceptor-->ControllerAdvice-->@Aspect -->Controller
 *
 * @author sunjie
 * @date 2022/1/14 17:36
 **/
@Slf4j
@Component
@WebFilter(urlPatterns = {"/**"}, filterName = "projectFilter")
public class ProjectFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("TokenFilter init {}", filterConfig.getFilterName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info(">>>>>>>>>>>请求路径>>>{}", ((HttpServletRequest) request).getRequestURI());
        //到下一个链
        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {
        log.info("TokenFilter destroy");
    }
}

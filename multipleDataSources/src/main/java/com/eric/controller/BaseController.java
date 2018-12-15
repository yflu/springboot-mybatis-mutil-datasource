package com.eric.controller;

import com.eric.util.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class BaseController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

    /**
     * 获取session
     *
     * @return
     */
    public static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return
     */
    public static Object getLoginUser() {
        return getSession().getAttribute(Constants.SESSION_USER_KEY);
    }

    public static void setSessionAttribute(String key, Object obj) {
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        session.setAttribute(key, obj);
    }


    public static Object getSessionAttribute(String key) {
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        return session.getAttribute(key);
    }

    public static void setUserSession(Object user) {
        setSessionAttribute(Constants.SESSION_USER_KEY, user);
    }

    /**
     * 基于@ExceptionHandler异常处理
     */
    @ExceptionHandler
    public String exp(HttpServletRequest request, Exception ex) {
        request.setAttribute("ex", ex);
        // 根据不同错误转向不同页面
        if (ex instanceof UnauthorizedException) {
            return "/error/403";
        } else {
            ex.printStackTrace();
            //插入日志
            Map<String, String[]> paramsMap = request.getParameterMap();
            String params = (paramsMap == null || paramsMap.size() == 0) ? null : gson.toJson(paramsMap);
            logger.error("************URI:" + request.getRequestURI() + ",参数:" + params + "异常信息:" + ex.getMessage());
/*      logService.createLog(request.getRequestURI(), request.getRequestURL().toString(), request.getMethod(), params, ExceptionUtils.getStackTrace(ex), -1D, Common.getRemortIP(request), Common.getJsessionid(request), request.getHeader("User-Agent"), null);*/
            return "/error/500";
        }
    }
}

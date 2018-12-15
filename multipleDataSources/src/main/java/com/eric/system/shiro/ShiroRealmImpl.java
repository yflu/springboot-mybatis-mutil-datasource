/*********************************************************************
 * 源代码版权归作者（们）所有
 * <p>
 * 以 Apache License, Version 2.0 方式授权使用，具体参见：
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 ********************************************************************/
package com.eric.system.shiro;

import com.eric.system.shiro.redis.RedisService;
import com.eric.util.Constants;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * http://shiro.apache.org/java-authentication-guide.html
 * <p>
 * Subject Security specific user 'view' of an application user. It can be a
 * human being, a third-party process, a server connecting to you application
 * application, or even a cron job. Basically, it is anything or anyone
 * communicating with your application.
 * <p>
 * Principals A subjects identifying attributes. First name, last name, social
 * security number, username
 * <p>
 * Credentials secret data that are used to verify identities. Passwords,
 * Biometric data, x509 certificates,
 * <p>
 * Realms Security specific DAO, data access object, software component that
 * talkts to a backend data source. If you have usernames and password in LDAP,
 * then you would have an LDAP Realm that would communicate with LDAP. The idea
 * is that you would use a realm per back-end data source and Shiro would know
 * how to coordinate with these realms together to do what you have to do.
 *
 * @author
 */
public class ShiroRealmImpl extends AuthorizingRealm {

    static final Logger logger = LoggerFactory.getLogger(ShiroRealmImpl.class);

    @Autowired
    private RedisService redisService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

    /*if (user != null) {

    } else {
      throw new AuthorizationException();
    }*/
        //给当前用户设置角色
        info.addRoles(null);
        //给当前用户设置权限
        info.addStringPermissions(null);
        //放入缓存
        //redisService.set(key, info);
        return info;
    }

    /**
     * 认证回调函数,登录时调用.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
    /*User user = userService.getUserByUserame(token.getUsername());
    if (user != null) {
      logger.info("-----------认证------------>" + user.getUsername());
      this.setSession(Constants.SESSION_USER_KEY, new SessionUser(user));
      return new SimpleAuthenticationInfo(user.getUsername(), user
        .getPassword(), getName());*/
        return new SimpleAuthenticationInfo("root", "123456", getName());
    /*} else {
      throw new UnknownAccountException();//没找到帐号
    }*/
    }

    /**
     * ShiroSession设置
     * 使用时直接用HttpSession.getAttribute(key)就可以取到
     *
     * @see
     */
    private void setSession(Object key, Object value) {
        Subject currentUser = SecurityUtils.getSubject();
        if (null != currentUser) {
            Session session = currentUser.getSession();
            if (null != session) {
                session.setAttribute(key, value);
            }
        }
    }

    public static Object getSessionAttribute(String key) {
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        return session.getAttribute(key);
    }

    /**
     * 重写退出时缓存处理方法
     */
    protected void doClearCache(PrincipalCollection principalcollection) {
        Object principal = principalcollection.getPrimaryPrincipal();
        redisService.del(Constants.AUTHORIZATIONINFO_PREFIX + principal.toString());
        redisService.del(Constants.SESSION_PREFIX + principal.toString());
        logger.debug(new StringBuffer().append(principal).append(" on logout to remove the cache [").append(principal)
                .append("]").toString());
    }
}

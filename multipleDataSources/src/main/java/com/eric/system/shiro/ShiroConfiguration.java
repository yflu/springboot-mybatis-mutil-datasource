package com.eric.system.shiro;

import com.eric.system.shiro.redis.RedisSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfiguration {

  private static Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

  @Bean(name = "ShiroRealmImpl")
  public ShiroRealmImpl getShiroRealm() {
    return new ShiroRealmImpl();
  }

  @Bean(name = "lifecycleBeanPostProcessor")
  public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
    return new LifecycleBeanPostProcessor();
  }

  @Bean(name = "RedisSessionDAO")
  public RedisSessionDAO getRedisSessionDAO() {
    return new RedisSessionDAO();
  }

  @Bean
  public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
    DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
    daap.setProxyTargetClass(true);
    return daap;
  }

  @Bean(name = "securityManager")
  public DefaultWebSecurityManager getDefaultWebSecurityManager() {
    DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
    dwsm.setRealm(getShiroRealm());

    DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
    sessionManager.setGlobalSessionTimeout(12 * 60 * 60 * 1000);
    sessionManager.setSessionDAO(getRedisSessionDAO());
    dwsm.setSessionManager(sessionManager);
    return dwsm;
  }

  @Bean
  public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor() {
    AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
    aasa.setSecurityManager(getDefaultWebSecurityManager());
    return new AuthorizationAttributeSourceAdvisor();
  }

  @Bean(name = "shiroFilter")
  public ShiroFilterFactoryBean getShiroFilterFactoryBean() {
    ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
    shiroFilterFactoryBean.setSecurityManager(getDefaultWebSecurityManager());
    shiroFilterFactoryBean.setLoginUrl("/login");
  /*  filterChainDefinitionMap.put("/", "authc");
    filterChainDefinitionMap.put("/login", "anon");
    filterChainDefinitionMap.put("/validatecode", "anon");
    filterChainDefinitionMap.put("/error*//**", "anon");
    filterChainDefinitionMap.put("/templates*//**", "anon");
    filterChainDefinitionMap.put("/css*//**", "anon");
    filterChainDefinitionMap.put("/js*//**", "anon");
    filterChainDefinitionMap.put("/report*//**", "anon");
    filterChainDefinitionMap.put("/images*//**", "anon");
    filterChainDefinitionMap.put("/files*//**", "anon");
    filterChainDefinitionMap.put("/fonts*//**", "anon");
    filterChainDefinitionMap.put("/common*//**", "anon");
    filterChainDefinitionMap.put("/menu*//**", "anon");
    filterChainDefinitionMap.put("/trade*//**", "anon");*/
    filterChainDefinitionMap.put("/**", "anon");
    shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    return shiroFilterFactoryBean;
  }
}
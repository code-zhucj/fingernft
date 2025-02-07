package com.fingerchar.api.config;

import com.fingerchar.api.shiro.AdminAuthorizingRealm;
import com.fingerchar.api.shiro.AdminWebSessionManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public Realm realm() {
        return new AdminAuthorizingRealm();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        filterChainDefinitionMap.put("/sync/**", "anon");
        filterChainDefinitionMap.put("/airdrop/**", "anon");
        filterChainDefinitionMap.put("/ds/**", "anon");
        filterChainDefinitionMap.put("/data/**", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/admin/auth/login", "anon");
        filterChainDefinitionMap.put("/admin/auth/logout", "anon");
        filterChainDefinitionMap.put("/admin/auth/kaptcha", "anon");
        filterChainDefinitionMap.put("/admin/auth/401", "anon");
        filterChainDefinitionMap.put("/admin/auth/index", "anon");
        filterChainDefinitionMap.put("/admin/auth/403", "anon");
        filterChainDefinitionMap.put("/admin/index/index", "anon");
        filterChainDefinitionMap.put("/admin/paytoken/all", "anon");
        filterChainDefinitionMap.put("/admin/config/fetch", "anon");
        shiroFilterFactoryBean.setLoginUrl("/home/list");
//        shiroFilterFactoryBean.setSuccessUrl("/admin/auth/index");
//        shiroFilterFactoryBean.setUnauthorizedUrl("/admin/auth/403");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    @Bean
    public SessionManager sessionManager() {
        AdminWebSessionManager adminWebSessionManager = new AdminWebSessionManager();
        // 修改会话（session）过期时间，因为sessionId用于token验证所以也是设置token过期时间
        adminWebSessionManager.setGlobalSessionTimeout(24 * 60 * 60 * 1000L);
        return adminWebSessionManager;
    }

    /**
     * 是 Shiro 的心脏；
     * 所有具体的交互都通过 SecurityManager 进行拦截并控制；它管理着所有 Subject、且负责进行认证和授权、及会话、缓存的管理
     */
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm());
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public static DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }
}

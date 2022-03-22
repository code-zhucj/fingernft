package com.fingerchar.api.shiro;

import com.fingerchar.api.service.FcMerchantService;
import com.fingerchar.api.utils.bcrypt.BCryptPasswordEncoder;
import com.fingerchar.db.domain.FcMerchant;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

public class AdminAuthorizingRealm extends AuthorizingRealm {

    @Autowired
    private FcMerchantService fcMerchantService;

    /*
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

//        FcMerchant merchant = (FcMerchant) getAvailablePrincipal(principals);
//		String roleIds = merchant.getRoleIds();
//		if (StringUtils.isEmpty(roleIds)) {
//			throw new AuthorizationException("no role.");
//		}
//		List<Long> roleIdList = JSON.parseArray(roleIds, Long.class);
//		Set<String> roles = roleService.queryByIds(roleIdList);
//		Set<String> permissions = permissionService.queryByRoleIds(roleIdList);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        info.setRoles(roles);
//        info.setStringPermissions(permissions);
        return info;
    }

    /**
     * 验证商户信息
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());

        if (StringUtils.isEmpty(username)) {
            throw new AccountException("商户名不能为空");
        }
        if (StringUtils.isEmpty(password)) {
            throw new AccountException("密码不能为空");
        }

        List<FcMerchant> merchantList = fcMerchantService.findMerchant(username);
        Assert.state(merchantList.size() < 2, "同一个商户名存在两个账户");
        if (merchantList.size() == 0) {
            throw new UnknownAccountException("找不到商户（" + username + "）的帐号信息");
        }
        FcMerchant merchant = merchantList.get(0);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, merchant.getPassword())) {
            throw new UnknownAccountException("找不到商户（" + username + "）的帐号信息");
        }

        return new SimpleAuthenticationInfo(merchant, password, getName());
    }

}

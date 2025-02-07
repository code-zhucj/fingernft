package com.fingerchar.api.web;

import com.fingerchar.api.constant.ResponseCode;
import com.fingerchar.api.constant.SysConfConstant;
import com.fingerchar.api.service.FcMerchantService;
import com.fingerchar.api.utils.IpUtil;
import com.fingerchar.core.base.controller.BaseController;
import com.fingerchar.core.util.ResponseUtil;
import com.fingerchar.db.domain.FcMerchant;
import com.google.code.kaptcha.Producer;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhuchuanji
 * @version 1.0.0
 * @Description 商户相关
 * @createTime 2022年03月20日 13:31:00
 */
@RestController
@RequestMapping(SysConfConstant.URL_PREFIX + "/merchant")
public class FcMerchantController extends BaseController {

    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private FcMerchantService merchantService;

    @PostMapping("/register")
    public Object register(String username, String password, String code) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResponseUtil.badArgument();
        }
        if (StringUtils.isEmpty(code)) {
            return ResponseUtil.fail(ResponseCode.INVALID_KAPTCHA_REQUIRED, "The verification code cannot be empty");
        }
        HttpSession session = request.getSession();
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (Objects.requireNonNull(code).compareToIgnoreCase(kaptcha) != 0) {
            return ResponseUtil.fail(ResponseCode.INVALID_KAPTCHA, "Verification code is not correct", doKaptcha(request));
        }
        boolean success = merchantService.insertMerchant(username, password, IpUtil.getIpAddr(request));
        if (!success) {
            return ResponseUtil.fail(ResponseCode.REGISTRATION_FAILED, "register failed");
        }
        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(new UsernamePasswordToken(username, password));
        } catch (UnknownAccountException uae) {
            return ResponseUtil.fail(ResponseCode.INVALID_ACCOUNT, "User name or password is incorrect");
        } catch (LockedAccountException lae) {
            return ResponseUtil.fail(ResponseCode.INVALID_ACCOUNT_LOCKED, "The user account is locked and unavailable");
        } catch (AuthenticationException ae) {
            ae.printStackTrace();
            return ResponseUtil.fail(ResponseCode.INVALID_ACCOUNT_FAILED, "Authentication failed");
        }
        FcMerchant merchant = (FcMerchant) currentUser.getPrincipal();
        Map<Object, Object> result = new HashMap<>();
        result.put("token", currentUser.getSession().getId());
        result.put("merchantInfo", Collections.singletonMap(FcMerchant.USERNAME, merchant.getUsername()));
        return ResponseUtil.ok(result);
    }

    @PostMapping("/login")
    public Object login(String username, String password, String code) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResponseUtil.badArgument();
        }

        if (StringUtils.isEmpty(code)) {
            return ResponseUtil.fail(ResponseCode.INVALID_KAPTCHA_REQUIRED, "The verification code cannot be empty");
        }
        HttpSession session = request.getSession();
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (Objects.requireNonNull(code).compareToIgnoreCase(kaptcha) != 0) {
            return ResponseUtil.fail(ResponseCode.INVALID_KAPTCHA, "Verification code is not correct", doKaptcha(request));
        }
        /*
         *获取当前用户的登录信息
         * 调用doGetAuthenticationInfo
         * */
        Subject currentUser = SecurityUtils.getSubject();

        try {
            currentUser.login(new UsernamePasswordToken(username, password));
            FcMerchant loginUser = (FcMerchant) currentUser.getPrincipal();
            //当用户状态为1时表示禁用，用户不能登录
            if (loginUser != null && loginUser.getStatus()) {
                throw new LockedAccountException();
            }
        } catch (UnknownAccountException uae) {
            return ResponseUtil.fail(ResponseCode.INVALID_ACCOUNT, "User name or password is incorrect");
        } catch (LockedAccountException lae) {
            return ResponseUtil.fail(ResponseCode.INVALID_ACCOUNT_LOCKED, "The user account is locked and unavailable");
        } catch (AuthenticationException ae) {
            ae.printStackTrace();
            return ResponseUtil.fail(ResponseCode.INVALID_ACCOUNT_FAILED, "Authentication failed");
        }

        currentUser = SecurityUtils.getSubject();
        FcMerchant merchant = (FcMerchant) currentUser.getPrincipal();
        merchant.setLastLoginIp(IpUtil.getIpAddr(request));
        merchant.setLastLoginTime(System.currentTimeMillis() / 1000);
        merchantService.updateById(merchant);

        Map<Object, Object> result = new HashMap<>();
        result.put("token", currentUser.getSession().getId());
        result.put("merchantInfo", Collections.singletonMap(FcMerchant.USERNAME, merchant.getUsername()));
        return ResponseUtil.ok(result);
    }

    @PostMapping("/kaptcha")
    public Object kaptcha(HttpServletRequest request) {
        String kaptcha = doKaptcha(request);
        if (kaptcha != null) {
            return ResponseUtil.ok(kaptcha);
        }
        return ResponseUtil.fail();
    }

    private String doKaptcha(HttpServletRequest request) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        HttpSession session = request.getSession();
        session.setAttribute("kaptcha", text);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", outputStream);
            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return "data:image/jpeg;base64," + base64.replaceAll("\r\n", "");
        } catch (IOException e) {
            return null;
        }
    }
}

package com.fingerchar.api.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fingerchar.api.utils.bcrypt.BCryptPasswordEncoder;
import com.fingerchar.core.base.service.IBaseService;
import com.fingerchar.db.base.BaseEntity;
import com.fingerchar.db.domain.FcMerchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhuchuanji
 * @version 1.0.0
 * @Description 商户service
 * @createTime 2022年03月22日 15:57:00
 */
@Service
public class FcMerchantService {

    @Autowired
    private IBaseService baseService;

    /**
     * 根据商户名查找商户信息
     *
     * @param merchantName 商户名
     * @return 商户列表（通常只有一个）
     */
    public List<FcMerchant> findMerchant(String merchantName) {
        QueryWrapper<FcMerchant> wrapper = new QueryWrapper<>();
        wrapper.eq(BaseEntity.DELETED, 0).eq(FcMerchant.STATUS, false)
                .eq(FcMerchant.USERNAME, merchantName);
        return baseService.findByCondition(FcMerchant.class, wrapper);
    }

    /**
     * 注册商户
     *
     * @param merchantName 商户名称
     * @param password     密码
     */
    public boolean insertMerchant(String merchantName, String password, String ip) {
        QueryWrapper<FcMerchant> wrapper = new QueryWrapper<>();
        wrapper.eq(FcMerchant.USERNAME, merchantName);
        if (baseService.findByCondition(FcMerchant.class, wrapper).size() > 0) {
            return false;
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(password);
        FcMerchant fcMerchant = new FcMerchant();
        fcMerchant.setStatus(true);
        fcMerchant.setPassword(encode);
        fcMerchant.setUsername(merchantName);
        fcMerchant.setLastLoginIp(ip);
        fcMerchant.setLastLoginTime(System.currentTimeMillis() / 1000);
        fcMerchant.setUpdateTime(System.currentTimeMillis() / 1000);
        return baseService.save(fcMerchant) >= 1;
    }

    /**
     * 更新商户信息
     *
     * @param merchant 商户信息
     */
    public void updateById(FcMerchant merchant) {
        merchant.setUpdateTime(System.currentTimeMillis() / 1000);
        baseService.update(merchant);
    }
}

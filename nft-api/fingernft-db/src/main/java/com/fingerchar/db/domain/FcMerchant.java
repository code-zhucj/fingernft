package com.fingerchar.db.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fingerchar.db.base.BaseEntity;

/**
 * @author zhuchuanji
 * @version 1.0.0
 * @Description 商户信息
 * @createTime 2022年03月20日 14:30:00
 */
@TableName("fc_merchant")
public class FcMerchant extends BaseEntity {
    @TableField("username")
    private String username;
    @TableField("password")
    private String password;
    @TableField("status")
    private Boolean status;
    @TableField("last_login_time")
    private Long lastLoginTime;
    @TableField("last_login_ip")
    private String lastLoginIp;

    public static final String STATUS = "status";
    public static final String USERNAME = "username";

    public Long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}

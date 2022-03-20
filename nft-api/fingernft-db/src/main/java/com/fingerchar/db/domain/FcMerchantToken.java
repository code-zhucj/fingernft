package com.fingerchar.db.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fingerchar.db.base.BaseEntity;

/**
 * @author zhuchuanji
 * @version 1.0.0
 * @Description 商户token
 * @createTime 2022年03月20日 14:32:00
 */
@TableName("`fc_merchant_token`")
public class FcMerchantToken extends BaseEntity {
    @TableField("'username'")
    private String username;
    @TableField("'token'")
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

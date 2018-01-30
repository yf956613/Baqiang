package com.jiebao.baqiang.data.bean;

/**
 * 用户登录服务器后，服务端返回的JavaBean
 */

public class LoginResponse {
    // authRet若为1，表示认证成功
    private String authRet;

    public String getAuthRet() {
        return this.authRet;
    }
}

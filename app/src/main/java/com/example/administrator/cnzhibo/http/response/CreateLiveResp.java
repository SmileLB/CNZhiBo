package com.example.administrator.cnzhibo.http.response;


import com.example.administrator.cnzhibo.http.IDontObfuscate;

/**
 * @description: 创建直播返回
 *
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class CreateLiveResp  extends IDontObfuscate {
    private String pushUrl;

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }
}

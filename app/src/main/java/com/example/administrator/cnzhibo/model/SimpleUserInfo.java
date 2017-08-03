package com.example.administrator.cnzhibo.model;


/**
 * @description: 用户基本信息封装 id、nickname、faceurl
 */
public class SimpleUserInfo {

    public String userid;
    public String nickname;
    public String headpic;

    public SimpleUserInfo(String userId, String nickname, String headpic) {
        this.userid = userId;
        this.nickname = nickname;
        this.headpic = headpic;
    }
}

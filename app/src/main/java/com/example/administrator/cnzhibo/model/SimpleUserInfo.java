package com.example.administrator.cnzhibo.model;


/**
 * @description: 用户基本信息封装 id、nickname、faceurl
 */
public class SimpleUserInfo {

    public String userId;
    public String nickname;
    public String headPic;

    public SimpleUserInfo(String userId, String nickname, String headpic) {
        this.userId = userId;
        this.nickname = nickname;
        this.headPic = headpic;
    }
}

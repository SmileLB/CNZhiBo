package com.example.administrator.cnzhibo.http.request;

import com.example.administrator.cnzhibo.http.response.ResList;
import com.example.administrator.cnzhibo.http.response.Response;
import com.example.administrator.cnzhibo.model.LiveInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @description: 进入房间接口请求
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class EnterGroupRequest extends IRequest {

    public EnterGroupRequest(int requestId, String userId, String liveId, String hostId, String groupId) {
        mRequestId = requestId;
        mParams.put("action", "enterGroup");
        mParams.put("userId", userId);
        mParams.put("groupId", groupId);
        mParams.put("hostId", hostId);
        mParams.put("liveId", liveId);
    }

    @Override
    public String getUrl() {
        return getHost() + "Live";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<ResList<LiveInfo>>>() {
        }.getType();
    }
}

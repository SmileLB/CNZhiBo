package com.example.administrator.cnzhibo.http.request;

import com.example.administrator.cnzhibo.http.response.ResList;
import com.example.administrator.cnzhibo.http.response.Response;
import com.example.administrator.cnzhibo.model.SimpleUserInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @description: 观众列表请求
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class GroupMemberReuest extends IRequest {

    public GroupMemberReuest(int requestId, String userId, String liveId, String hostId, String groupId,
                             int pageIndex, int pageSize) {
        mRequestId = requestId;
        mParams.put("action", "groupMember");
        mParams.put("userId", userId);
        mParams.put("groupId", groupId);
        mParams.put("liveId", liveId);
        mParams.put("hostId", hostId);
        mParams.put("pageIndex", pageIndex);
        mParams.put("pageSize", pageSize);
    }

    @Override
    public String getUrl() {
        return getHost() + "Live";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<ResList<SimpleUserInfo>>>() {
        }.getType();
    }
}

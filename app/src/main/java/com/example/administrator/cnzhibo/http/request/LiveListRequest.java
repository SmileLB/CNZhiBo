package com.example.administrator.cnzhibo.http.request;

import com.example.administrator.cnzhibo.http.response.ResList;
import com.example.administrator.cnzhibo.http.response.Response;
import com.example.administrator.cnzhibo.model.LiveInfo;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @description: 直播列表请求
 */
public class LiveListRequest extends IRequest {

	public LiveListRequest(int requestId, String userId , int pageIndex, int pageSize) {
		mRequestId = requestId;
//		mParams.put("action","liveList");
		mParams.put("action","liveListTest");//测试加载更多
		mParams.put("userId",userId);
		mParams.put("pageIndex", pageIndex);
		mParams.put("pageSize", pageSize);
	}

	@Override
	public String getUrl() {
		return getHost() + "Live";
	}

	@Override
	public Type getParserType() {
		return new TypeToken<Response<ResList<LiveInfo>>>() {}.getType();
	}
}

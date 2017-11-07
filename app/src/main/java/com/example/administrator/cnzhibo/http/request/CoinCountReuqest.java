package com.example.administrator.cnzhibo.http.request;

import com.example.administrator.cnzhibo.http.response.Response;
import com.example.administrator.cnzhibo.model.CoinCount;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @description: 金币数量
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class CoinCountReuqest extends IRequest {

    public CoinCountReuqest(int requestId, String userId) {
        mRequestId = requestId;
        mParams.put("action", "coinCount");
        mParams.put("userId", userId);
    }

    @Override
    public String getUrl() {
        return getHost() + "Live";
    }

    @Override
    public Type getParserType() {
        return new TypeToken<Response<CoinCount>>() {
        }.getType();
    }
}

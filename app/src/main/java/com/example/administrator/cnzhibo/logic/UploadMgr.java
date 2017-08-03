package com.example.administrator.cnzhibo.logic;

import android.content.Context;

import com.example.administrator.cnzhibo.http.AsyncHttp;
import com.example.administrator.cnzhibo.http.request.RequestComm;
import com.example.administrator.cnzhibo.http.request.UploadPicRequest;
import com.example.administrator.cnzhibo.http.response.Response;
import com.example.administrator.cnzhibo.http.response.UploadResp;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * @description: 图片上传类
 */
public class UploadMgr {
	private static final String TAG = "UploadMgr";

	private final static int MAIN_CALL_BACK = 1;
	private final static int MAIN_PROCESS = 2;
	private final static int UPLOAD_AGAIN = 3;

	private Context mContext;
	private OnUploadListener mListerner;

	public UploadMgr(final Context context, OnUploadListener listener) {
		mContext = context;
		mListerner = listener;
	}

	public void uploadCover(String userId, final String path,int type) {
		try {
			UploadPicRequest req = new UploadPicRequest(RequestComm.UploadPic, userId, type, new File(path));
			AsyncHttp.instance().post(req, new AsyncHttp.IHttpListener() {
				@Override
				public void onStart(int requestId) {

				}

				@Override
				public void onSuccess(int requestId, Response response) {
					if (response.status == RequestComm.SUCCESS) {
						try {
							UploadResp resp = (UploadResp) response.data;
							if (mListerner != null) {
								mListerner.onUploadResult(0, resp.getId(), resp.getUrl());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						if (mListerner != null) {
							mListerner.onUploadResult(-1, "", null);
						}
					}
				}

				@Override
				public void onFailure(int requestId, int httpStatus, Throwable error) {
					if (mListerner != null) {
						mListerner.onUploadResult(-1, "", null);
					}
				}
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public interface OnUploadListener {
		/**
		 * @param code:0,表示成功，-1表示失败
		 * @param imageId
		 * @param url
		 */
		public void onUploadResult(int code, String imageId, String url);
	}
}

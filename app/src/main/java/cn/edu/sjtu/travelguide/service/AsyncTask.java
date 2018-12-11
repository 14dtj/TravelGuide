package cn.edu.sjtu.travelguide.service;

import okhttp3.ResponseBody;

/**
 * 用于异步任务回调
 */
public interface AsyncTask {
    /**
     * 请求成功
     */
    void onSuccess(ResponseBody body);

    /**
     * 请求失败
     */
    void onFailure();
}

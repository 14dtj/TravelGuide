package cn.edu.sjtu.travelguide.service;

/**
 * 用于异步任务回调
 */
public interface AsyncTask {
    /**
     * 请求成功
     */
    void onSuccess();

    /**
     * 请求失败
     */
    void onFailure();
}

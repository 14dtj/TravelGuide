package cn.edu.sjtu.travelguide;

import android.app.Application;
import android.content.SharedPreferences;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import cn.edu.sjtu.travelguide.entity.User;
import cn.edu.sjtu.travelguide.service.AsyncTask;
import cn.edu.sjtu.travelguide.service.RMPService;
import okhttp3.ResponseBody;

/**
 * Created by wanglei on 2018/10/7.
 */

public class MyApplication extends Application implements AsyncTask {
    /**
     * 登录后的用户信息
     */
    private static User user;

    @Override
    public void onCreate() {
        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        //自动登录
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        if (sp != null) {
            String username = sp.getString("username", "");
            String password = sp.getString("password", "0");
            RMPService.getInstance().login(username, password, this);
        }
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        MyApplication.user = user;
    }

    @Override
    public void onSuccess(ResponseBody body) {
        //do nothing
    }

    @Override
    public void onFailure() {
        // do nothing
    }
}

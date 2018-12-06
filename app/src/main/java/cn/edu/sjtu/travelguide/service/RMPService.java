package cn.edu.sjtu.travelguide.service;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import cn.edu.sjtu.travelguide.MyApplication;
import cn.edu.sjtu.travelguide.entity.User;
import cn.edu.sjtu.travelguide.entity.UserList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RMPService {
    private final static String TAG = RMPService.class.getSimpleName();
    private static RMPService instance;
    private OkHttpClient client;
    private static final String BASE_URL = "http://119.23.241.119:8080/Entity/U18494e8f6fa80/travel/";

    public static RMPService getInstance() {
        if (instance == null) {
            instance = new RMPService();
        }
        return instance;
    }

    private RMPService() {
        client = new OkHttpClient();
    }

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     */
    public void login(String username, final String password, final AsyncTask task) {
        String url = BASE_URL + "User/?User.username=" + username;
        Request request = new Request.Builder().url(url)
                .get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage());
                task.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) {
                    task.onFailure();
                } else {
                    String jsonStr = body.string();
                    Log.d(TAG, jsonStr);
                    instance.onLoginResponse(jsonStr, password, task);
                }
            }
        });
    }

    /**
     * 回调函数
     *
     * @param jsonStr
     * @param password
     * @param task
     */
    private void onLoginResponse(String jsonStr, String password, AsyncTask task) {
        Gson gson = new Gson();
        UserList response = gson.fromJson(jsonStr, UserList.class);
        if (response != null) {
            List<User> users = response.getUser();
            if (users != null && !users.isEmpty()) {
                User user = users.get(0);
                if (user.getPassword().equals(password)) {
                    MyApplication.setUser(user);
                    task.onSuccess();
                }
            }
        }
    }

    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    public void register(User user, final AsyncTask task) {
        String url = BASE_URL + "User/";
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String data = gson.toJson(user);
        RequestBody body = RequestBody.create(mediaType, data);
        Request request = new Request.Builder().url(url)
                .post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage());
                task.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) {
                    task.onFailure();
                } else {
                    String jsonStr = body.string();
                    Log.d(TAG, jsonStr);
                    task.onSuccess();
                }
            }
        });
    }


}

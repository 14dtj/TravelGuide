package cn.edu.sjtu.travelguide.service;

import com.google.gson.Gson;

import java.util.List;

import cn.edu.sjtu.travelguide.entity.User;
import cn.edu.sjtu.travelguide.entity.UserList;

public class RMPService {
    private static RMPService instance;
    private HttpHelper httpHelper;
    private static final String BASE_URL = "http://119.23.241.119:8080/Entity/U18494e8f6fa80/travel/";

    public static RMPService getInstance() {
        if (instance == null) {
            instance = new RMPService();
        }
        return instance;
    }

    private RMPService() {
        httpHelper = new HttpHelper();
    }

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     */
    public boolean login(String username, String password) {
        String jsonStr = httpHelper.sendGet(BASE_URL + "User/?User.username=" + username);
        Gson gson = new Gson();
        UserList response = gson.fromJson(jsonStr, UserList.class);
        if (response != null) {
            List<User> users = response.getUser();
            if (users != null && !users.isEmpty()) {
                User user = users.get(0);
                return user.getPassword().equals(password);
            }
        }
        return false;
    }
}

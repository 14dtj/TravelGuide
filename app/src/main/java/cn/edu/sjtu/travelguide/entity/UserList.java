package cn.edu.sjtu.travelguide.entity;

import java.util.List;

public class UserList {
    private List<User> User;

    public UserList(List<cn.edu.sjtu.travelguide.entity.User> user) {
        User = user;
    }

    public List<cn.edu.sjtu.travelguide.entity.User> getUser() {
        return User;
    }

    public void setUser(List<cn.edu.sjtu.travelguide.entity.User> user) {
        User = user;
    }
}

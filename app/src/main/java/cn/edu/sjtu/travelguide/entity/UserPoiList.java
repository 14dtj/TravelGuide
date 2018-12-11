package cn.edu.sjtu.travelguide.entity;

import java.util.List;

public class UserPoiList {
    private List<SearchRecord> User_poi;

    public UserPoiList(List<SearchRecord> user_poi) {
        User_poi = user_poi;
    }

    public List<SearchRecord> getUser_poi() {
        return User_poi;
    }

    public void setUser_poi(List<SearchRecord> user_poi) {
        User_poi = user_poi;
    }
}

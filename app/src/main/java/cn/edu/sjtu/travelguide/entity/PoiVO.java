package cn.edu.sjtu.travelguide.entity;

public class PoiVO {
    public String name;
    public String phone;
    public String address;
    public String imgUrl;
    public String uid;


    public PoiVO(String name, String phone, String address, String uid, String imgUrl) {
        this.name = name;
        this.phone = phone;
        this.imgUrl = imgUrl;
        this.address = address;
        this.uid = uid;
    }
}

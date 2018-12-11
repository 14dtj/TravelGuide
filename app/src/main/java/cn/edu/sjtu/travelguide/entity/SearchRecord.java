package cn.edu.sjtu.travelguide.entity;

/**
 * 搜索记录
 */
public class SearchRecord {
    /**
     * RMP中自动生成的id
     */
    private long id;
    /**
     * 用户搜索时间
     */
    private String search_time;
    /**
     * RMP中的用户id
     */
    private long user_id;
    /**
     * 用户搜索的关键词
     */
    private String keyword;
    /**
     * 以下字段从百度poi api中获取
     * 店铺名
     */
    private String name;
    /**
     * 手机号/电话
     */
    private String phone_num;
    /**
     * 店铺地址
     */
    private String address;
    /**
     * 百度poi id
     */
    private String uid;
    /**
     * 店铺图片的第一张图
     */
    private String img_url;
    /**
     * 店铺详情页面链接
     */
    private String detail_url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSearch_time() {
        return search_time;
    }

    public void setSearch_time(String search_time) {
        this.search_time = search_time;
    }


    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDetail_url() {
        return detail_url;
    }

    public void setDetail_url(String detail_url) {
        this.detail_url = detail_url;
    }
}

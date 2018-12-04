package cn.edu.sjtu.travelguide.entity;

/**
 * 搜索记录
 */
public class SearchRecord {
    private long id;
    private String search_time;
    private long poi_id;

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

    public long getPoi_id() {
        return poi_id;
    }

    public void setPoi_id(long poi_id) {
        this.poi_id = poi_id;
    }
}

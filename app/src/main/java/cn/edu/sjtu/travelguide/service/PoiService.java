package cn.edu.sjtu.travelguide.service;

import android.util.Log;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.sjtu.travelguide.MyApplication;
import cn.edu.sjtu.travelguide.entity.SearchRecord;
import cn.edu.sjtu.travelguide.entity.User;

public class PoiService {
    private final static String TAG = PoiService.class.getSimpleName();
    private PoiSearch mPoiSearch = PoiSearch.newInstance();
    private Map<String, SearchRecord> map = new HashMap<>();
    private List<SearchRecord> records = new ArrayList<>();
    private static PoiService instance;

    private PoiService() {
    }

    public static PoiService getInstance() {
        if (instance == null) {
            instance = new PoiService();
        }
        return instance;
    }

    /**
     * 用户添加搜索记录
     *
     * @param keyword
     */
    public void addSearchRecord(final String keyword) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String searchTime = df.format(new Date());
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                //获取POI检索结果
                if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    Log.e(TAG, "POI not found");
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    List<PoiInfo> pois = result.getAllPoi();
                    User user = MyApplication.getUser();
                    if (user == null) {
                        return;
                    }
                    long userId = user.getId();
                    if (pois != null) {
                        for (PoiInfo info : pois) {
                            if (info.name.contains(keyword)) {
                                SearchRecord record = new SearchRecord();
                                record.setKeyword(keyword);
                                record.setSearch_time(searchTime);
                                record.setUser_id(userId);
                                record.setName(info.name);
                                record.setAddress(info.address);
                                record.setPhone_num(info.phoneNum);
                                record.setUid(info.uid);
                                map.put(info.uid, record);
                                mPoiSearch.searchPoiDetail(new PoiDetailSearchOption()
                                        .poiUid(info.uid).showPlaceCarter(false));
                            }
                        }
                    }

                }

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult result) {
                //获取Place详情页检索结果
                if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    Log.e(TAG, "POI detail not found");
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    SearchRecord record = map.get(result.uid);
                    //Log.d(TAG, result.uid + ", " + result.detailUrl);
                    if (record != null) {
                        record.setDetail_url(result.detailUrl);
                        records.add(record);
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        RMPService.getInstance().addSearchRecord(record);
                    }
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
                Log.d(TAG, poiDetailSearchResult.error + "");
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                Log.d(TAG, poiIndoorResult.pageNum + "");
            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        String[] keywords = {"美食", "酒店", "购物", "旅游景点", "休闲娱乐"};
        for (String str : keywords) {
            for (int i = 0; i < 100; i++) {
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city("上海")
                        .keyword(str)
                        .pageNum(i).scope(1));
            }
        }
    }
}

package cn.edu.sjtu.travelguide;

import android.os.Bundle;
import android.text.TextUtils;
//import android.widget.SearchView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanglei on 2018/12/13.
 */

public class PublicRouteActivity extends QMUIFragmentActivity implements OnGetPoiSearchResultListener, OnGetBusLineSearchResultListener{

    //@BindView(R.id.lineSearch)
    SearchView lineSearch = null;

    private PoiSearch mSearch = null;
    private BusLineSearch mBuslineSearch = null;
    private BusLineResult route =  null;
    private List<String> busLineIDList = null;
    private List<String> buslineList = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicroute);

        mSearch = PoiSearch.newInstance();
        mSearch.setOnGetPoiSearchResultListener(this);
        mBuslineSearch = BusLineSearch.newInstance();
        mBuslineSearch.setOnGetBusLineSearchResultListener(this);

        lineSearch = (SearchView) findViewById(R.id.searchview);
        lineSearch.bringToFront();
        lineSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(TextUtils.isEmpty(s)){
                    Toast.makeText(PublicRouteActivity.this, "请输入查找内容", Toast.LENGTH_SHORT).show();
                    return false;
                }
                mSearch.searchInCity((new PoiCitySearchOption())
                        .city("上海")
                        .keyword(s));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    protected int getContextViewId() {
        return R.id.qmuidemo;
    }

    @Override
    public void onGetBusLineResult(BusLineResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果",
                    Toast.LENGTH_LONG).show();
            return;
        }
        buslineList = new ArrayList<>();
        List<BusLineResult.BusStation> bs = result.getStations();
        for(BusLineResult.BusStation b: bs){
            buslineList.add(b.getTitle());
        }

        Toast.makeText(this, "得到了"+result.getBusLineName(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果",
                    Toast.LENGTH_LONG).show();
            return;
        }
        // 遍历所有poi，找到类型为公交线路的poi
        busLineIDList = new ArrayList<>();
        for (PoiInfo poi : result.getAllPoi()) {
            busLineIDList.add(poi.uid);
//            if (poi.type == PoiInfo.POITYPE.BUS_LINE ||poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
//                busLineIDList.add(poi.uid);
//                break;
//            }
        }
        if(busLineIDList!=null)
            mBuslineSearch.searchBusLine((new BusLineSearchOption()
                .city("上海")
                .uid(busLineIDList.get(0))));
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }
}

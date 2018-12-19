package cn.edu.sjtu.travelguide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
//import android.widget.SearchView;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    MyAdapter mAdapter;

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
                hintKeyBoard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

//        RecyclerView mRecyclerView = findViewById(R.id.stationView);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(layoutManager);
//
//        mAdapter = new MyAdapter();
//        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addItemDecoration(new StationItemDecoration(getApplicationContext()));
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

        RecyclerView mRecyclerView = findViewById(R.id.stationView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new StationItemDecoration(getApplicationContext()));

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

    public void hintKeyBoard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive() && getCurrentFocus()!=null){
            if(getCurrentFocus().getWindowToken()!=null){
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    /*
   当点击默认的返回按钮时的操作
    */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyVH>{

        @NonNull
        @Override
        public MyVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.station_item, parent, false);
            return new MyVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyVH holder, int position){
            holder.tv.setText(buslineList.get(position));
        }
        @Override
        public int getItemCount(){
            return buslineList.size();
        }
    }

    private class MyVH extends android.support.v7.widget.RecyclerView.ViewHolder{

        public TextView tv;

        public MyVH(View view){
            super(view);
            tv = view.findViewById(R.id.stationItem);
        }

    }

    private class StationItemDecoration extends android.support.v7.widget.RecyclerView.ItemDecoration{
        private Paint mPaint; //绘制线
        private Paint mPaint1;//绘制圆圈
        private float radius;
        // 左 上偏移长度
        private int itemView_leftinterval;
        private int itemView_topinterval;
        public StationItemDecoration(Context context){
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(2); //时间轴线的宽度。
            mPaint.setColor(Color.GRAY); //时间轴线的颜色

            mPaint1 = new Paint();
            mPaint1.setColor(Color.GRAY);
            radius = 10.0f;
            // 赋值ItemView的左偏移长度为200
            itemView_leftinterval = 0;

            // 赋值ItemView的上偏移长度为50
            itemView_topinterval = 0;

        }
        // 重写getItemOffsets（）方法
        // 作用：设置ItemView 左 & 上偏移长度
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            // 设置ItemView的左 & 上偏移长度分别为150 px & 30px,即此为onDraw()可绘制的区域
            outRect.set(itemView_leftinterval, itemView_topinterval, 0, 0);

        }


        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state){
            super.onDraw(c, parent, state);

            int childCount = parent.getChildCount();
            for(int i = 0; i < childCount ; i++){
                View view = parent.getChildAt(i);

                float left = dip2px(getApplicationContext(), 14 + 10);
                //float bottom = view.getBottom()-dip2px(getApplicationContext(), 300);
                float bottom = view.getBottom();
                c.drawLine(left, dip2px(getApplicationContext(), (50 - 20) / 2), left, bottom, mPaint);

                float x = view.getLeft();
                float y = view.getTop()+dip2px(getApplicationContext(),10);
                c.drawCircle(left, y, radius, mPaint1);
            }
        }
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

package cn.edu.sjtu.travelguide.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.SearchActivity;

import static android.content.Context.SENSOR_SERVICE;

public class MapFragment extends BaseFragment implements SensorEventListener, OnGetGeoCoderResultListener {

    private static final int LOCATION_REQUEST = 0;

    @BindView(R.id.bmapView)
    MapView mMapView;
    private BaiduMap mBaiduMap;
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

//    @BindView(R.id.departure)
//    EditText departure;
    @BindView(R.id.destination)
    EditText destination;

    LocationClient mLocClient;
    public MyLocationListener myListener = new MyLocationListener();
    private LocationMode mCurrentMode;
    //BitmapDescriptor mCurrentMarker;
//    private static final int accuracyCircleFillColor = 0xAAFFFF88;
//    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccuracy;

    private MyLocationData locData;
    boolean isFirstLoc = true; // 是否首次定位


    @Override
    protected View onCreateView() {
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_map, null);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, layout);
        //departure.bringToFront();
        destination.bringToFront();
        //departure.setBackgroundColor(Color.WHITE);
        destination.setBackgroundColor(Color.WHITE);
//        destination.setFocusable(true);
//        destination.setFocusableInTouchMode(true);

        destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    if(getActivity()!=null){
                        Intent intent = new Intent(getActivity(), SearchActivity.class);
                        startActivityForResult(intent,2);//2代表searchActivity
                    }

//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    if(!sf.isAdded()){
//                        if(currentFragment != null){
//                            ft.hide(currentFragment);
//                        }
//                        ft.add(R.id.fragment, sf, sf.getClass().getName());
//                    }else{
//                        ft.hide(currentFragment).show(sf);
//                    }
//                    currentFragment = sf;
//                    ft.commit();
                }else{

                }
            }
        });

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mCurrentMode = LocationMode.COMPASS;
        mBaiduMap = mMapView.getMap();

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this.getContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mLocClient.requestLocation();
        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 2){
            if(resultCode == LOCATION_REQUEST){
                Bundle bundle = data.getExtras();
                destination.setText(bundle.getString("destination"));
                //城市可以从定位那里获取
                //mSearch.geocode(new GeoCodeOption().city("上海").address("梅赛德斯奔驰文化中心"));
                mSearch.geocode(new GeoCodeOption().city("上海").address(destination.getText().toString()));
            }
        }
    }

    @Override
    public void onDestroy() {
        //退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mSearch.destroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccuracy)
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }

        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions()
                .position(result.getLocation())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding)));

        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude,
                result.getLocation().longitude);

        Toast.makeText(getActivity(), strInfo, Toast.LENGTH_LONG).show();

        Log.e("GeoCodeDemo", "onGetGeoCodeResult = " + result.toString());
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccuracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccuracy)
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }
}

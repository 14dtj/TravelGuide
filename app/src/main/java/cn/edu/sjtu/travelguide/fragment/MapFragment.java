package cn.edu.sjtu.travelguide.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
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
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.PublicRouteActivity;
import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.SearchActivity;
import cn.edu.sjtu.travelguide.SlideVerticalActivity;
import cn.edu.sjtu.travelguide.service.AsyncTask;
import cn.edu.sjtu.travelguide.service.WeatherService;
import okhttp3.ResponseBody;

import static android.content.Context.SENSOR_SERVICE;

public class MapFragment extends BaseFragment implements SensorEventListener, OnGetGeoCoderResultListener, AsyncTask {

    private static final String TAG = MapFragment.class.getSimpleName();
    private static final int LOCATION_REQUEST = 0;

    @BindView(R.id.bmapView)
    MapView mMapView;
    private BaiduMap mBaiduMap;
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    @BindView(R.id.departure)
    EditText departure;
    @BindView(R.id.destination)
    EditText destination;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.publicTraffic)
    FloatingActionButton publicTraffic;//规划路线
    @BindView(R.id.trafficCondition)
    FloatingActionButton trafficButton;//查看交通状况
    @BindView(R.id.weatherButton)
    FloatingActionButton weatherButton;
    @BindView(R.id.weatherView)
    EditText weatherView;

    private ConstraintLayout layout;

    LocationClient mLocClient;
    public MyLocationListener myListener = new MyLocationListener();
    private LocationMode mCurrentMode;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccuracy;

    private MyLocationData locData;
    boolean isFirstLoc = true; // 是否首次定位

    String mylocation = null;
    String city = null;

    //need to be declared final
    String weather = null;
    String humidity = null;
    String pm = null;
    String quality = null;
    String temperature = null;
    String sun = null;

    @Override
    protected View onCreateView() {
        layout = (ConstraintLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_map, null);
        ButterKnife.bind(this, layout);

        departure.setVisibility(View.INVISIBLE);
        destination.bringToFront();
        Drawable drawableLeft = getResources().getDrawable(
                R.drawable.search_ali);
        drawableLeft.setBounds(10, 0, 60, 50);
        destination.setCompoundDrawables(drawableLeft, null, null, null);
        departure.setCompoundDrawables(drawableLeft, null, null, null);
//        destination.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
//                null, null, null);
//        destination.setCompoundDrawablePadding(4);

        weatherView.setVisibility(View.INVISIBLE);
        weatherView.setBackgroundColor(Color.WHITE);
        weatherView.bringToFront();

        publicTraffic.bringToFront();
        trafficButton.bringToFront();
        weatherButton.bringToFront();
        fab.setBackgroundResource(R.drawable.bus);
        fab.bringToFront();
        fab.hide();

        View.OnClickListener fabListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (view.getId()){
                    case R.id.publicTraffic:
                        intent = new Intent(getActivity(), PublicRouteActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.trafficCondition:
                        if(mBaiduMap.isTrafficEnabled()){
                            mBaiduMap.setTrafficEnabled(false);
                            //trafficButton.setBackgroundResource(R.drawable.invisibility);
                            trafficButton.setImageResource(R.drawable.visibility_off_white);
                        }else{
                            mBaiduMap.setTrafficEnabled(true);
                            //trafficButton.setBackgroundResource(R.drawable.visibility);
                            trafficButton.setImageResource(R.drawable.visibility_white);
                        }
                        break;
                    case R.id.fab:
                        String departureLocation = destination.getText().toString();//出发地
                        String destinationLocation = departure.getText().toString();//目的地
                        intent = new Intent(getActivity(), SlideVerticalActivity.class);
                        intent.putExtra("departureLocation", departureLocation);
                        intent.putExtra("destinationLocation", destinationLocation);
                        startActivity(intent);
                        break;
                    case R.id.weatherButton:
                        if(weatherView.getVisibility() == View.VISIBLE){
                            weatherView.setVisibility(View.INVISIBLE);
                        }else{
                            weatherView.setVisibility(View.VISIBLE);
                            test();
                        }
                    default:
                        break;
                }
            }
        };

        fab.setOnClickListener(fabListener);
        publicTraffic.setOnClickListener(fabListener);
        trafficButton.setOnClickListener(fabListener);
        weatherButton.setOnClickListener(fabListener);

        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    if(getActivity()!=null){
                        getFragmentManager().beginTransaction().addToBackStack(this.getClass().getName());
                        Intent intent = new Intent(getActivity(), SearchActivity.class);
                        if(fab.isShown()){
                            intent.putExtra("location", mylocation);
                            intent.putExtra("destination", departure.getText().toString());
                            intent.putExtra("departure", destination.getText().toString());
                            startActivityForResult(intent,2);//2代表searchActivity
                        }else{
                            //第一次搜索
                            intent.putExtra("location", mylocation);
                            intent.putExtra("destination", "");
                            intent.putExtra("departure", departure.getText().toString());
                            startActivityForResult(intent,2);//2代表searchActivity
                        }

                    }
                }
            }
        };

        destination.setOnFocusChangeListener(onFocusChangeListener);
        departure.setOnFocusChangeListener(onFocusChangeListener);

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mCurrentMode = LocationMode.COMPASS;
        mBaiduMap = mMapView.getMap();

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(getContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mCurrentMode = LocationMode.NORMAL;
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, null));
        mLocClient.requestLocation();
        return layout;
    }

    public void test(){
        //获取天气
        WeatherService.getInstance().getWeatherCondition("101020100", this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 2){
            if(resultCode == LOCATION_REQUEST){
                Bundle bundle = data.getExtras();
                if(bundle.getString("departure").equals("") && bundle.getString("destination").equals("")){
                    destination.setText("");
                    destination.setFocusable(false);
                    destination.setFocusableInTouchMode(true);
                    departure.setVisibility(View.INVISIBLE);
                }else if(!bundle.getString("departure").equals("") && !bundle.getString("destination").equals("")){
                    destination.setText(bundle.getString("departure"));
                    destination.setFocusable(false);
                    destination.setFocusableInTouchMode(true);

                    departure.setVisibility(View.VISIBLE);
                    departure.bringToFront();
                    departure.setText(bundle.getString("destination"));
                    departure.setFocusable(false);
                    departure.setFocusableInTouchMode(true);

                    mSearch.geocode(new GeoCodeOption().city(city).address(bundle.getString("destination")));

                    //这里调用可能会出问题！！！！
                    //PoiService.getInstance().addSearchRecord(bundle.getString("destination"));
                }
                fab.show();

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
        mLocClient.stop();
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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));

        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude,
                result.getLocation().longitude);

       // Toast.makeText(getActivity(), strInfo, Toast.LENGTH_LONG).show();

        //Log.e("GeoCodeDemo", "onGetGeoCodeResult = " + result.toString());
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }

//        mBaiduMap.clear();
//        mBaiduMap.addOverlay(new MarkerOptions()
//                .position(result.getLocation())
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka)));
//
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

        mylocation = result.getAddress();
        departure.setText(result.getAddress().toString());

        //Toast.makeText(getActivity(), result.getAddress() + " adcode: " + result.getAdcode(), Toast.LENGTH_LONG).show();

        //Log.e("GeoCodeDemo", "ReverseGeoCodeResult = " + result.toString());
    }

    @Override
    public void onSuccess(ResponseBody body) {

        try{
            weather = body.string();
            JSONObject json = new JSONObject(weather);
            JSONObject data = json.getJSONObject("data");
            humidity = data.getString("shidu");
            pm = data.get("pm25").toString();
            quality = data.get("quality").toString();
            temperature = data.get("wendu").toString();
            JSONArray jsonArray = data.getJSONArray("forecast");
            JSONObject jo = jsonArray.getJSONObject(0);
            sun = jo.getString("type");
        } catch(Exception e){
            e.printStackTrace();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 新建一个可以添加属性的文本对象
                SpannableString ss = new SpannableString(temperature+"℃" + "  湿度" + humidity);
                // 新建一个属性对象,设置文字的大小
                AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15,true);
                // 附加属性到文本
                ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // 设置hint
                weatherView.setText(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
                //weatherView.setText(temperature+"℃" + " 湿度" + humidity);
                if(sun.equals("阴")||sun.equals("多云")){
                    Drawable drawableRight = getResources().getDrawable(
                            R.drawable.cloudy);
                    drawableRight.setBounds(10, 0, 60, 50);
                    weatherView.setCompoundDrawables(drawableRight, null, null, null);
                    //weatherView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.cloudy), null, null);
                }else if(sun.equals("晴")){
                    Drawable drawableRight = getResources().getDrawable(
                            R.drawable.sunny);
                    drawableRight.setBounds(10, 0, 60, 50);
                    weatherView.setCompoundDrawables(drawableRight, null, null, null);
                    //weatherView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.sunny), null, null);
                }else if(sun.contains("雨")){
                    Drawable drawableRight = getResources().getDrawable(
                            R.drawable.rainy);
                    drawableRight.setBounds(10, 0, 60, 50);
                    weatherView.setCompoundDrawables(drawableRight, null, null, null);
                    //weatherView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.rainy), null, null);
                }else{
                    Drawable drawableRight = getResources().getDrawable(
                            R.drawable.nighty);
                    drawableRight.setBounds(10, 0, 60, 50);
                    weatherView.setCompoundDrawables(drawableRight, null, null, null);
                    //weatherView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.nighty), null, null);
                }
            }
        });

    }

    @Override
    public void onFailure() {

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

            city = location.getCity();
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }
}

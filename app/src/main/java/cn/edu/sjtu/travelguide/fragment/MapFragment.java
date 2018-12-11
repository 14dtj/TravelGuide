package cn.edu.sjtu.travelguide.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.RoutePlanActivity;
import cn.edu.sjtu.travelguide.SearchActivity;

import static android.content.Context.SENSOR_SERVICE;

public class MapFragment extends BaseFragment implements SensorEventListener, OnGetGeoCoderResultListener {

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

//    @BindView(R.id.tv1)
//    TextView tv1;
//    @BindView(R.id.tv2)
//    TextView tv2;

//    @BindView(R.id.menu)
//    ListView listview;
//    private ArrayList<Drawable> mList;

    private ConstraintLayout layout;

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

    String mylocation = null;
    String city = null;

    @Override
    protected View onCreateView() {
        layout = (ConstraintLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_map, null);
        ButterKnife.bind(this, layout);

        departure.setBackgroundColor(Color.WHITE);
        departure.setVisibility(View.INVISIBLE);
        destination.bringToFront();
        destination.setBackgroundColor(Color.WHITE);

//        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/fa-solid-900.ttf");
//        tv1.setTypeface(font);
//        tv2.setTypeface(font);
//        tv1.bringToFront();
//        tv2.bringToFront();
//        tv1.setBackgroundColor(Color.WHITE);
//        tv2.setBackgroundColor(Color.WHITE);

//        listview.bringToFront();
//        List<ListItemView> lists = new ArrayList<>();
//        lists.add(new ListItemView(R.drawable.bus));
//        lists.add(new ListItemView(R.drawable.ybr));

//
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });

//        int[] images = new int[]{R.drawable.ybr, R.drawable.bus};
//        String[] titles = new String[]{"1","2"};
//        List<Map<String, Object>> listitems = new ArrayList<Map<String, Object>>();
//        for(int i=0;i<images.length;i++){
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("image", images[i]);
//            map.put("title", titles[i]);
//            listitems.add(map);
//        }
//
//        SimpleAdapter adapter = new SimpleAdapter(getActivity(), listitems, R.layout.icon_items, new String[]{"title", "image"},
//                new int[]{R.id.title, R.id.image});
//        listview.setAdapter(adapter);
//        Resources res = this.getResources();
//        mList = new ArrayList<Drawable>();
//        mList.add(res.getDrawable(R.drawable.ybr));
//        mList.add(res.getDrawable(R.drawable.bus));


        publicTraffic.bringToFront();
        trafficButton.bringToFront();
        fab.setBackgroundResource(R.drawable.bus);
        fab.bringToFront();
        fab.hide();

        View.OnClickListener fabListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.publicTraffic:

                        break;
                    case R.id.trafficCondition:
                        if(mBaiduMap.isTrafficEnabled()){
                            mBaiduMap.setTrafficEnabled(false);
                            //trafficButton.setBackgroundResource(R.drawable.invisibility);
                            trafficButton.setImageResource(R.drawable.invisibility);
                        }else{
                            mBaiduMap.setTrafficEnabled(true);
                            //trafficButton.setBackgroundResource(R.drawable.visibility);
                            trafficButton.setImageResource(R.drawable.visibility);
                        }
                        break;
                    case R.id.fab:
                        String departureLocation = destination.getText().toString();//出发地
                        String destinationLocation = departure.getText().toString();//目的地
                        Intent intent = new Intent(getActivity(), RoutePlanActivity.class);
                        intent.putExtra("departureLocation", departureLocation);
                        intent.putExtra("destinationLocation", destinationLocation);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        };

        fab.setOnClickListener(fabListener);
        publicTraffic.setOnClickListener(fabListener);
        trafficButton.setOnClickListener(fabListener);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                /*
//                这里规划路线-------TO DO
//                 */
//                String departureLocation = destination.getText().toString();//出发地
//                String destinationLocation = departure.getText().toString();//目的地
//                Intent intent = new Intent(getActivity(), RoutePlanActivity.class);
//                intent.putExtra("departureLocation", departureLocation);
//                intent.putExtra("destinationLocation", destinationLocation);
//                startActivity(intent);
//            }
//        });
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
        mLocClient.requestLocation();
        return layout;
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
                    //动态添加出发地的输入框控件
//                    ConstraintSet cSet = new ConstraintSet();
//                    EditText depart_new = new EditText(getContext());
//                    depart_new.setText(bundle.getString("departure"));
//                    layout.addView(depart_new);
//                    cSet.clone(layout);
//                    cSet.constrainWidth(depart_new.getId(), ConstraintLayout.layoutParams.WRAP_CONTENT);
//                    cSet.constrainHeight(depart_new.getId(), 317);
                    destination.setText(bundle.getString("departure"));
                    destination.setFocusable(false);
                    destination.setFocusableInTouchMode(true);

                    departure.setVisibility(View.VISIBLE);
                    departure.bringToFront();
                    departure.setText(bundle.getString("destination"));
                    departure.setFocusable(false);
                    departure.setFocusableInTouchMode(true);

                    mSearch.geocode(new GeoCodeOption().city(city).address(bundle.getString("destination")));
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
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding)));

        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude,
                result.getLocation().longitude);

       // Toast.makeText(getActivity(), strInfo, Toast.LENGTH_LONG).show();

        Log.e("GeoCodeDemo", "onGetGeoCodeResult = " + result.toString());
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

        Log.e("GeoCodeDemo", "ReverseGeoCodeResult = " + result.toString());
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

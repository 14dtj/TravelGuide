package cn.edu.sjtu.travelguide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.edu.sjtu.travelguide.overlayutil.BikingRouteOverlay;
import cn.edu.sjtu.travelguide.overlayutil.DrivingRouteOverlay;
import cn.edu.sjtu.travelguide.overlayutil.MassTransitRouteOverlay;
import cn.edu.sjtu.travelguide.overlayutil.OverlayManager;
import cn.edu.sjtu.travelguide.overlayutil.TransitRouteOverlay;
import cn.edu.sjtu.travelguide.overlayutil.WalkingRouteOverlay;
import cn.edu.sjtu.travelguide.util.SlidingMenuVertical;

public class SlideVerticalActivity extends Activity implements BaiduMap.OnMapClickListener,
        OnGetRoutePlanResultListener {

    FloatingActionButton routeDetail;
    // 浏览路线节点相关
    Button mBtnPre = null; // 上一个节点
    Button mBtnNext = null; // 下一个节点
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    RouteLine route = null;
    MassTransitRouteLine massroute = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    private TextView popupText = null; // 泡泡view
    TextView routeText = null;
    // 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    // 如果不处理touch事件，则无需继承，直接使用MapView即可
    MapView mMapView = null;    // 地图View
    BaiduMap mBaidumap = null;
    // 搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    WalkingRouteResult nowResultwalk = null;
    BikingRouteResult nowResultbike = null;
    TransitRouteResult nowResultransit = null;
    DrivingRouteResult nowResultdrive = null;
    MassTransitRouteResult nowResultmass = null;

    int nowSearchType = -1; // 当前进行的检索，供判断浏览节点时结果使用。

    String startNodeStr = "上海交通大学（闵行校区）";
    String endNodeStr = "上海站";

    String fastOrShort = "fast";
    String howtogo = "drive";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_vertical);
        CharSequence titleLable = "路线规划功能";

        setTitle(titleLable);
        Intent intent = getIntent();
//        startNodeStr=intent.getStringExtra("departureLocation");
//        endNodeStr =intent.getStringExtra("destinationLocation");




        routeDetail=findViewById(R.id.routeDetail);
        routeDetail.bringToFront();

        // 初始化地图
        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(this);

        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        findViewById(R.id.drive).setActivated(true);
        findViewById(R.id.fast).setActivated(true);
        routeText = findViewById(R.id.routeContent);
        final TextView tv_middle = (TextView) findViewById(R.id.tv_middle);

        tv_middle.setText("终点："+endNodeStr+"\n起点："+startNodeStr);


        final SlidingMenuVertical slidingMenuVertical = ((SlidingMenuVertical) findViewById(R.id.slidingMenu));
        slidingMenuVertical.setDuration_max(300);
        slidingMenuVertical.setAmbit_scroll(100);
        slidingMenuVertical.setOnSwitchListener(new SlidingMenuVertical.OnSwitchListener() {
            /*
                   滑动中
        y_now:实时view_bottom的top y, y_opened:抽屉打开时view_bootom的top y,y_closed:抽屉关闭时view_bottom的top y  top y:在屏幕中的top y坐标
                    */
            @Override
            public void onSwitching(boolean isToOpen, int y_now, int y_opened, int y_closed) {

                tv_middle.setBackgroundColor(Color.argb((int) (1.0f * (y_opened - y_now) / (y_opened - y_closed) * 255),
                        Color.red(0xff3F51B5), Color.green(0xff3F51B5), Color.blue(0xff3F51B5)));

                tv_middle.setTextColor(Color.argb((int) (1.0f * (y_opened - y_now) / (y_opened - y_closed) * 255),
                        Color.red(0xffffffff), Color.green(0xffffffff), Color.blue(0xffffffff)));
            }

            @Override
            public void onSwitched(boolean opened) {

                if (opened) {
                    tv_middle.setBackgroundColor(0xffffffff);
                    tv_middle.setTextColor(0xff454545);
                }
            }
        });
        View.OnClickListener routeDetailListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SlideVerticalActivity.this, StepActivity.class);
                intent.putExtra("routeDetail",getNodeTitles());
                        startActivity(intent);
            }
        };

        routeDetail.setOnClickListener(routeDetailListener);

        //点击驾车最快按钮
        findViewById(R.id.drive).performClick();

    }

    public void backProcess(View v) {
        finish();
    }

    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */
    public void searchButtonProcess(View v) {

        // 重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();
        // 实际使用中请对起点终点城市进行正确的设定
        if (v.getId() == R.id.fast) {
            findViewById(R.id.fast).setActivated(true);
            findViewById(R.id.shortest).setActivated(false);
            fastOrShort = "fast";
        } else if (v.getId() == R.id.shortest) {
            findViewById(R.id.fast).setActivated(false);
            findViewById(R.id.shortest).setActivated(true);
            fastOrShort = "short";
        } else {

        }
        // 处理搜索按钮响应
        // 设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("上海", startNodeStr);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("上海", endNodeStr);


        if (v.getId() == R.id.drive) {
            howtogo = "drive";
            findViewById(R.id.drive).setActivated(true);
            findViewById(R.id.transit).setActivated(false);
            findViewById(R.id.walk).setActivated(false);
        } else if (v.getId() == R.id.transit) {
            howtogo = "transit";
            findViewById(R.id.transit).setActivated(true);
            findViewById(R.id.walk).setActivated(false);
            findViewById(R.id.drive).setActivated(false);
        } else if (v.getId() == R.id.walk) {
            howtogo = "walk";
            findViewById(R.id.walk).setActivated(true);
            findViewById(R.id.transit).setActivated(false);
            findViewById(R.id.drive).setActivated(false);
        }

        if (howtogo.equals("drive")) {
            mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode).to(enNode));
            nowSearchType = 1;
        } else if (howtogo.equals("transit")) {
            mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(stNode).city("上海").to(enNode));
            nowSearchType = 2;
        } else {
            mSearch.walkingSearch((new WalkingRoutePlanOption())
                    .from(stNode).to(enNode));
            nowSearchType = 3;
        }

    }


    public ArrayList<String> getNodeTitles(){
        ArrayList<String> result=new ArrayList<String>();
        int size=route.getAllStep().size();
        Object step = null;
        for(int i=0;i<size;i++) {
            step = route.getAllStep().get(i);
            if (step instanceof DrivingRouteLine.DrivingStep) {

                result.add(((DrivingRouteLine.DrivingStep) step).getInstructions());
            } else if (step instanceof WalkingRouteLine.WalkingStep) {

                result.add(((WalkingRouteLine.WalkingStep) step).getInstructions());
            } else if (step instanceof TransitRouteLine.TransitStep) {

                result.add(((TransitRouteLine.TransitStep) step).getInstructions());
            } else if (step instanceof BikingRouteLine.BikingStep) {

                result.add( ((BikingRouteLine.BikingStep) step).getInstructions());
            }
        }
        return result;
    }
    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {

        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = null;

        if (nowSearchType != 0 && nowSearchType != -1) {
            // 非跨城综合交通
            if (route == null || route.getAllStep() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.pre) {
                return;
            }
            // 设置节点索引
            if (v.getId() == R.id.next) {
                if (nodeIndex < route.getAllStep().size() - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            // 获取节结果信息
            step = route.getAllStep().get(nodeIndex);
            if (step instanceof DrivingRouteLine.DrivingStep) {
                nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
                nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
            } else if (step instanceof WalkingRouteLine.WalkingStep) {
                nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
                nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
            } else if (step instanceof TransitRouteLine.TransitStep) {
                nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
                nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
            } else if (step instanceof BikingRouteLine.BikingStep) {
                nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
                nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
            }
        } else if (nowSearchType == 0) {
            // 跨城综合交通  综合跨城公交的结果判断方式不一样


            if (massroute == null || massroute.getNewSteps() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.pre) {
                return;
            }
            boolean isSamecity = nowResultmass.getOrigin().getCityId() == nowResultmass.getDestination().getCityId();
            int size = 0;
            if (isSamecity) {
                size = massroute.getNewSteps().size();
            } else {
                for (int i = 0; i < massroute.getNewSteps().size(); i++) {
                    size += massroute.getNewSteps().get(i).size();
                }
            }

            // 设置节点索引
            if (v.getId() == R.id.next) {
                if (nodeIndex < size - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            if (isSamecity) {
                // 同城
                step = massroute.getNewSteps().get(nodeIndex).get(0);
            } else {
                // 跨城
                int num = 0;
                for (int j = 0; j < massroute.getNewSteps().size(); j++) {
                    num += massroute.getNewSteps().get(j).size();
                    if (nodeIndex - num < 0) {
                        int k = massroute.getNewSteps().get(j).size() + nodeIndex - num;
                        step = massroute.getNewSteps().get(j).get(k);
                        break;
                    }
                }
            }

            nodeLocation = ((MassTransitRouteLine.TransitStep) step).getStartLocation();
            nodeTitle = ((MassTransitRouteLine.TransitStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }

        // 移动节点至中心
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(SlideVerticalActivity.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(SlideVerticalActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            AlertDialog.Builder builder = new AlertDialog.Builder(SlideVerticalActivity.this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。\n可通过getSuggestAddrInfo()接口获得建议查询信息");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);

            if (result.getRouteLines().size() > 1) {
                nowResultwalk = result;


                route = nowResultwalk.getRouteLines().get(0);
                routeText.setText(setRouteInfo(route)+"\n\n\n\n");
                WalkingRouteOverlay overlay = new SlideVerticalActivity.MyWalkingRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(nowResultwalk.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();


            } else if (result.getRouteLines().size() == 1) {
                // 直接显示
                route = result.getRouteLines().get(0);
                WalkingRouteOverlay overlay = new SlideVerticalActivity.MyWalkingRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(SlideVerticalActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);


            if (result.getRouteLines().size() > 1) {
                nowResultransit = result;


                route = nowResultransit.getRouteLines().get(0);
                routeText.setText(setRouteInfo(route)+"\n\n\n\n");
                TransitRouteOverlay overlay = new SlideVerticalActivity.MyTransitRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(nowResultransit.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();


            } else if (result.getRouteLines().size() == 1) {
                // 直接显示
                route = result.getRouteLines().get(0);
                TransitRouteOverlay overlay = new SlideVerticalActivity.MyTransitRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else {
                Log.d("route result", "结果数<0");
                return;
            }


        }
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(SlideVerticalActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点模糊，获取建议列表
            result.getSuggestAddrInfo();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nowResultmass = result;

            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);

            SlideVerticalActivity.MyMassTransitRouteOverlay overlay = new SlideVerticalActivity.MyMassTransitRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            massroute = nowResultmass.getRouteLines().get(0);
            overlay.setData(nowResultmass.getRouteLines().get(0));

            MassTransitRouteLine line = nowResultmass.getRouteLines().get(0);
            overlay.setData(line);
            if (nowResultmass.getOrigin().getCityId() == nowResultmass.getDestination().getCityId()) {
                // 同城
                overlay.setSameCity(true);
            } else {
                // 跨城
                overlay.setSameCity(false);

            }
            mBaidumap.clear();
            overlay.addToMap();
            overlay.zoomToSpan();
        }


    }

    public String setRouteInfo(RouteLine routeLine) {
        String result = "";
        int time = routeLine.getDuration();
        if (time / 3600 == 0) {
            result = "大约需要：" + time / 60 + "分钟" + "\n";
        } else {
            result = "大约需要：" + time / 3600 + "小时" + (time % 3600) / 60 + "分钟" + "\n";
        }
        result += ("距离大约是：" + routeLine.getDistance() + "米\n");
        if (routeLine instanceof DrivingRouteLine) {
            DrivingRouteLine DrouteLine = (DrivingRouteLine) routeLine;
            result += ("红绿灯数：" + DrouteLine.getLightNum() + "个\n");

            result += ("拥堵距离为：" + DrouteLine.getCongestionDistance() + "米\n");
        }

        return result;
    }

    public int shortOrFast(List<DrivingRouteLine> routeLine, String type){
        int time = routeLine.get(0).getDuration();
        float distance=routeLine.get(0).getDistance();
        int  Fastposition=0;
        int shortPosition=0;
        for(int i=0;i<routeLine.size();i++){
            if(routeLine.get(i).getDuration()<time){
                time = routeLine.get(i).getDuration();
                Fastposition=i;
            }
            if(routeLine.get(i).getDistance()<distance){
                distance = routeLine.get(i).getDistance();
                shortPosition=i;
            }
        }
    if(type.equals("fast")){
            return Fastposition;
    }else{
            return shortPosition;
    }
    }
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(SlideVerticalActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            if (result.getRouteLines().size() > 1) {

                nowResultdrive = result;
                int position=shortOrFast(result.getRouteLines(),fastOrShort);
                route = nowResultdrive.getRouteLines().get(position);
                String nodeTitle="";
                ArrayList<String> getNode=getNodeTitles();
                for(int i=0;i<getNode.size();i++){
                    nodeTitle+=getNode.get(i)+"\n";
                }
                routeText.setText(setRouteInfo(route)+"打车约"+70+"元\n");
                DrivingRouteOverlay overlay = new SlideVerticalActivity.MyDrivingRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(nowResultdrive.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else if (result.getRouteLines().size() == 1) {
                route = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new SlideVerticalActivity.MyDrivingRouteOverlay(mBaidumap);
                routeOverlay = overlay;
                mBaidumap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(SlideVerticalActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            AlertDialog.Builder builder = new AlertDialog.Builder(SlideVerticalActivity.this);
            builder.setTitle("提示");
            builder.setMessage("检索地址有歧义，请重新设置。\n可通过getSuggestAddrInfo()接口获得建议查询信息");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);

            if (result.getRouteLines().size() > 1) {
                nowResultbike = result;
                route = nowResultbike.getRouteLines().get(0);
                BikingRouteOverlay overlay = new SlideVerticalActivity.MyBikingRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(nowResultbike.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else if (result.getRouteLines().size() == 1) {
                route = result.getRouteLines().get(0);
                BikingRouteOverlay overlay = new SlideVerticalActivity.MyBikingRouteOverlay(mBaidumap);
                routeOverlay = overlay;
                mBaidumap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }
    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyBikingRouteOverlay extends BikingRouteOverlay {
        public MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }


    }

    private class MyMassTransitRouteOverlay extends MassTransitRouteOverlay {
        public MyMassTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }


    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mSearch != null) {
            mSearch.destroy();
        }
        mMapView.onDestroy();
        super.onDestroy();
    }


}

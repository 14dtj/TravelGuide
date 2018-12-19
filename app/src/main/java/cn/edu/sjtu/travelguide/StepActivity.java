package cn.edu.sjtu.travelguide;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.edu.sjtu.travelguide.adapter.StepAdapter;
import cn.edu.sjtu.travelguide.util.Step;

public class StepActivity extends AppCompatActivity {

    private List<Step> mData = null;
    private Context mContext;
    private StepAdapter mAdapter = null;
    private ListView list_animal;
    ArrayList<String> routeDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        Intent intent = getIntent();

        routeDetail=intent.getStringArrayListExtra("routeDetail");
        mContext = StepActivity.this;
        list_animal = (ListView) findViewById(R.id.list_test);
        mData = new LinkedList<Step>();
        for(int i=0;i<routeDetail.size();i++){
            mData.add(new Step(routeDetail.get(i), routeDetail.get(i), R.drawable.ic_arrow_upward_black_36dp));
        }

        mAdapter = new StepAdapter((LinkedList<Step>) mData, mContext);
        list_animal.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(list_animal);
    }
    public void backProcess(View v) {
        finish();
    }
    /*
     * 当ScrollView 与 LiseView 同时滚动计算高度的方法
     * 设置listview 的高度
     * 参数：listivew的findviewbyid
     * */
    /**动态改变listView的高度*/
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
//           totalHeight += 80;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
//          params.height = 80 * (listAdapter.getCount() - 1);
//          params.height = 80 * (listAdapter.getCount());
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(0, 0, 0, 0);
        listView.setLayoutParams(params);

    }

}

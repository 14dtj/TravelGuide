package cn.edu.sjtu.travelguide;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        mContext = StepActivity.this;
        list_animal = (ListView) findViewById(R.id.list_test);
        mData = new LinkedList<Step>();
        mData.add(new Step("狗说", "你是狗么?", R.drawable.icon_geo));
        mData.add(new Step("牛说", "你是牛么?", R.drawable.icon_geo));
        mData.add(new Step("鸭说", "你是鸭么?", R.drawable.icon_geo));
        mData.add(new Step("鱼说", "你是鱼么?", R.drawable.icon_geo));
        mData.add(new Step("马说", "你是马么?", R.drawable.icon_geo));
        mAdapter = new StepAdapter((LinkedList<Step>) mData, mContext);
        list_animal.setAdapter(mAdapter);
    }
}

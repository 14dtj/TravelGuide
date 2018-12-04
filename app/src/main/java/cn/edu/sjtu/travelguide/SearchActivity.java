package cn.edu.sjtu.travelguide;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by wanglei on 2018/12/3.
 */

public class SearchActivity extends QMUIFragmentActivity implements OnGetSuggestionResultListener {

    private static final int LOCATION_REQUEST = 0;
    
    //@BindView(R.id.search)
    Button searchButton;

    private SuggestionSearch mSuggestionSearch = null;
    /* 搜索关键字输入 */
    private AutoCompleteTextView departureView = null;
    private ArrayAdapter<String> departureSug = null;
    private AutoCompleteTextView destinationView = null;
    private ArrayAdapter<String> destinationSug = null;

    @Override
    protected int getContextViewId() {
        return R.id.qmuidemo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        searchButton = findViewById(R.id.search);

        departureView = (AutoCompleteTextView) findViewById(R.id.departure);
        departureSug = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        departureView.setAdapter(departureSug);
        departureView.setThreshold(1);
        
        destinationView = (AutoCompleteTextView) findViewById(R.id.searchkey);
        destinationSug = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        destinationView.setAdapter(destinationSug);
        destinationView.setThreshold(1);

        /* 当出发地输入关键字变化时，动态更新建议列表 */
        departureView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                /* 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新 */
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString())
                        .city("上海"));
            }
        });
        
        /* 当目的地输入关键字变化时，动态更新建议列表 */
        destinationView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                /* 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新 */
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString())
                        .city("上海"));
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("departure", "上海市闵行区上海交通大学");
                //bundle.putString("destination", "梅赛德斯奔驰文化中心 上海市浦东新区");
                bundle.putString("destination", destinationView.getText().toString());
                setResult(LOCATION_REQUEST, SearchActivity.this.getIntent().putExtras(bundle));
                SearchActivity.this.finish();
            }
        });



    }


    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }

        List<String> suggest = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
            }
        }

        destinationSug = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_dropdown_item_1line,
                suggest);
        destinationView.setAdapter(destinationSug);
        destinationSug.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mSuggestionSearch.destroy();
        super.onDestroy();
    }
}

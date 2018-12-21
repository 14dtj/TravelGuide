package cn.edu.sjtu.travelguide.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.poi.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.MyApplication;
import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.adapter.RecommendListAdapter;
import cn.edu.sjtu.travelguide.entity.SearchRecord;
import cn.edu.sjtu.travelguide.entity.User;
import cn.edu.sjtu.travelguide.entity.UserPoiList;
import cn.edu.sjtu.travelguide.service.AsyncTask;
import cn.edu.sjtu.travelguide.service.PoiService;
import cn.edu.sjtu.travelguide.service.RMPService;
import okhttp3.ResponseBody;

public class RecommendFragment extends BaseFragment implements BaseQuickAdapter.OnRecyclerViewItemClickListener, AsyncTask {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.list_post_t)
    RecyclerView listPost;

    private RecommendListAdapter listAdapter;
    private List<SearchRecord> data = new ArrayList<>();
    private PoiSearch mPoiSearch = PoiSearch.newInstance();
    private Gson gson = new Gson();
    private RecommendDetailFragment detailFragment;


    @Override
    protected View onCreateView() {
        User user = MyApplication.getUser();
        if (user == null) {
            return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_about, null);
        }
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recommend, null);
        ButterKnife.bind(this, layout);
        mTopBar.setTitle("推荐");
        RMPService.getInstance().getRecommendPoi(user.getId(), this);
        listAdapter = new RecommendListAdapter(this.getContext(), data);
        listAdapter.openLoadMore(10, true);
        listAdapter.openLoadAnimation();
        listAdapter.setOnRecyclerViewItemClickListener(this);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        listPost.setLayoutManager(linearLayoutManager);
        listPost.setAdapter(listAdapter);
        return layout;
    }


    @Override
    public void onItemClick(View view, int i) {
        if (!(view instanceof ViewGroup)) {
            return;
        }
        String url = null;
        ViewGroup viewGroup = (ViewGroup) view;
        for (int j = 0; j < viewGroup.getChildCount(); j++) {
            View child = viewGroup.getChildAt(j);
            if (child.getId() == R.id.recommend_detail_url) {
                TextView textView = (TextView) child;
                url = textView.getText().toString();
            }
        }
        if (url == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        if (detailFragment == null) {
            detailFragment = new RecommendDetailFragment();
        }
        detailFragment.setArguments(bundle);
        startFragment(detailFragment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }

    @Override
    public void onSuccess(ResponseBody body) {
        List<SearchRecord> pois = new ArrayList<>();
        try {
            UserPoiList userPoiList = gson.fromJson(body.string(), UserPoiList.class);
            pois = userPoiList.getUser_poi();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (pois != null) {
            for (SearchRecord info : pois) {
                String imgUrl = "http://119.23.241.119:8080/file/U18494e8f6fa80/travel/User_poi/" + info.getId();
                info.setImg_url(imgUrl);
            }
            final List<SearchRecord> pois1 = new ArrayList<>(pois);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    data.addAll(pois1);
                    listAdapter.setNewData(data);
                    listAdapter.openLoadMore(10, true);
                }
            });

        }
    }

    @Override
    public void onFailure() {

    }
}

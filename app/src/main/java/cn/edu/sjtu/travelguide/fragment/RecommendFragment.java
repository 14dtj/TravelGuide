package cn.edu.sjtu.travelguide.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.adapter.RecommendListAdapter;
import cn.edu.sjtu.travelguide.entity.PoiVO;

public class RecommendFragment extends BaseFragment implements BaseQuickAdapter.OnRecyclerViewItemClickListener {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.list_post_t)
    RecyclerView listPost;

    private RecommendListAdapter listAdapter;
    private List<PoiVO> data = new ArrayList<>();
    private PoiSearch mPoiSearch = PoiSearch.newInstance();

    @Override
    protected View onCreateView() {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recommend, null);
        ButterKnife.bind(this, layout);
        mTopBar.setTitle("推荐");
        getPOI();
        listAdapter = new RecommendListAdapter(this.getContext(), data);
        listAdapter.openLoadMore(10, true);
        listAdapter.openLoadAnimation();
        listAdapter.setOnRecyclerViewItemClickListener(this);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        listPost.setLayoutManager(linearLayoutManager);
        listPost.setAdapter(listAdapter);
        return layout;
    }

    private void getPOI() {
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                //获取POI检索结果
                if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    Log.e("hhhhTAG", "Not found");
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    List<PoiInfo> pois = result.getAllPoi();
                    if (pois != null) {
                        for (PoiInfo info : pois) {
                            data.add(new PoiVO(info.name, info.phoneNum, info.address, info.uid, "http://119.23.241.119:8080/file/U18494e8f6fa80/travel/Poi/1543036765786"));
                        }
                        listAdapter.setNewData(data);
                        listAdapter.openLoadMore(10, true);
                    }

                }

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult result) {
                //获取Place详情页检索结果
                if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    Log.e("hhhhTAG", "Not found");
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    Bundle bundle = new Bundle();
                    bundle.putString("url", result.detailUrl);
                    RecommendDetailFragment detailFragment = new RecommendDetailFragment();
                    detailFragment.setArguments(bundle);
                    startFragment(detailFragment);
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
                Log.d("hhhTAGdetail", poiDetailSearchResult.error + "");
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                Log.d("hhhhTAG", poiIndoorResult.pageNum + "");
            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city("上海")
                .keyword("娱乐")
                .pageNum(10).scope(1));
    }

    @Override
    public void onItemClick(View view, int i) {
        PoiVO vo = data.get(i);
        mPoiSearch.searchPoiDetail(new PoiDetailSearchOption()
                .poiUid(vo.uid).showPlaceCarter(false));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }
}

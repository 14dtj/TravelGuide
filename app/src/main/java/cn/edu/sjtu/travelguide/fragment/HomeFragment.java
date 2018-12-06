package cn.edu.sjtu.travelguide.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.MainActivity;
import cn.edu.sjtu.travelguide.MyApplication;
import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.adapter.BaseFragmentPagerAdapter;

/**
 * 管理其他fragment的主界面
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.tabs)
    QMUITabSegment mTabSegment;
    @BindView(R.id.pager)
    ViewPager mViewPager;


    private MapFragment mapFragment = new MapFragment();
    private MyFragment myFragment = new MyFragment();
    private RecommendFragment recommendFragment = new RecommendFragment();
    List<Fragment> fragments = new ArrayList<>();

    private MainActivity context;
    private BaseFragmentPagerAdapter adapter;

    @Override
    protected View onCreateView() {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, layout);
        context = (MainActivity) getActivity();
        initTabs();
        initPagers();
        return layout;
    }

    private void initTabs() {
        int normalColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.qmui_config_color_gray_6);
        int selectColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.qmui_config_color_blue);
        mTabSegment.setDefaultNormalColor(normalColor);
        mTabSegment.setDefaultSelectedColor(selectColor);
        mTabSegment.setDefaultTabIconPosition(QMUITabSegment.ICON_POSITION_BOTTOM);

        QMUITabSegment.Tab component = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_component),
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_component_selected),
                "地图", false
        );

        QMUITabSegment.Tab util = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_util),
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_util_selected),
                "推荐", false
        );
        QMUITabSegment.Tab lab = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_lab),
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_lab_selected),
                "我的", false
        );
        mTabSegment.addTab(component)
                .addTab(util)
                .addTab(lab);
    }

    private void initPagers() {
        fragments.add(mapFragment);
        fragments.add(recommendFragment);
        fragments.add(myFragment);
        adapter = new BaseFragmentPagerAdapter(context.getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabSegment.setupWithViewPager(mViewPager, false);
    }

    @Override
    protected boolean canDragBack() {
        return false;
    }


}
package cn.edu.sjtu.travelguide.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.R;

public class RecommendFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @Override
    protected View onCreateView() {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recommend, null);
        ButterKnife.bind(this, layout);
        mTopBar.setTitle("推荐");
        return layout;
    }
}

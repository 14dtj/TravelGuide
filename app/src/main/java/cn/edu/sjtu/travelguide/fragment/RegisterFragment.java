package cn.edu.sjtu.travelguide.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.qmuiteam.qmui.arch.QMUIFragment;

import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.R;

public class RegisterFragment extends QMUIFragment {
    @Override
    protected View onCreateView() {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_about, null);
        ButterKnife.bind(this, layout);
        return layout;
    }
}

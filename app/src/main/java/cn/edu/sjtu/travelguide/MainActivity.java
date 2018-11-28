package cn.edu.sjtu.travelguide;

import android.os.Bundle;

import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

import cn.edu.sjtu.travelguide.fragment.BaseFragment;
import cn.edu.sjtu.travelguide.fragment.HomeFragment;

public class MainActivity extends QMUIFragmentActivity {

    @Override
    protected int getContextViewId() {
        return R.id.qmuidemo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseFragment fragment = new HomeFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(getContextViewId(), fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();

    }
}

package cn.edu.sjtu.travelguide;

import android.os.Bundle;
import android.view.Window;

import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

import cn.edu.sjtu.travelguide.fragment.BaseFragment;
import cn.edu.sjtu.travelguide.fragment.HomeFragment;

public class MainActivity extends QMUIFragmentActivity {
    private BaseFragment currentFragment;
    private HomeFragment homeFragment;

    @Override
    protected int getContextViewId() {
        return R.id.qmuidemo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        homeFragment = new HomeFragment();
        currentFragment = homeFragment;

        getSupportFragmentManager()
                .beginTransaction()
                .add(getContextViewId(), currentFragment, currentFragment.getClass().getSimpleName())
                .addToBackStack(currentFragment.getClass().getSimpleName())
                .commit();

        if(savedInstanceState!= null)
        {
            String FRAGMENTS_TAG = "Android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);

        }
    }

    public HomeFragment getHomeFragment() {
        return homeFragment;
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState){
//        //super.onSaveInstanceState(outState);
//    }
}

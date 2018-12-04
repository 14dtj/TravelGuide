package cn.edu.sjtu.travelguide;

import android.os.Bundle;
import android.view.Window;

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
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        BaseFragment fragment = new HomeFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(getContextViewId(), fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();

        if(savedInstanceState!= null)
        {
            String FRAGMENTS_TAG = "Android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);

        }
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState){
//        //super.onSaveInstanceState(outState);
//    }
}

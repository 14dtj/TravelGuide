package cn.edu.sjtu.travelguide;

import android.os.Bundle;
import android.view.Window;

import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

import cn.edu.sjtu.travelguide.fragment.BaseFragment;
import cn.edu.sjtu.travelguide.fragment.HomeFragment;
import cn.edu.sjtu.travelguide.fragment.LoginFragment;

public class MainActivity extends QMUIFragmentActivity {
    private BaseFragment currentFragment;
    private HomeFragment homeFragment;
    private LoginFragment loginFragment;

    @Override
    protected int getContextViewId() {
        return R.id.qmuidemo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

      //  if (MyApplication.getUser() != null) {
            homeFragment = new HomeFragment();
            currentFragment = homeFragment;
     //   } else {
//            loginFragment = new LoginFragment();
//            currentFragment = loginFragment;
//        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(getContextViewId(), currentFragment, currentFragment.getClass().getSimpleName())
                .addToBackStack(currentFragment.getClass().getSimpleName())
                .commit();

        if (savedInstanceState != null) {
            String FRAGMENTS_TAG = "Android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);

        }
    }

    public void change2Home() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        startFragment(homeFragment);
    }

    public void change2Login() {
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }
        startFragment(loginFragment);
    }
}

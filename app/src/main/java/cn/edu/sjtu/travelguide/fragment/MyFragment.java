package cn.edu.sjtu.travelguide.fragment;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.MainActivity;
import cn.edu.sjtu.travelguide.MyApplication;
import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.entity.User;

import static android.content.Context.MODE_PRIVATE;

public class MyFragment extends BaseFragment {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView)
    QMUIGroupListView mGroupListView;

    private MainActivity context;

    @Override
    protected View onCreateView() {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_my, null);
        ButterKnife.bind(this, layout);
        context = (MainActivity) getActivity();
        mTopBar.setTitle("我的");
        initGroupListView();
        return layout;
    }

    private void initGroupListView() {
        QMUICommonListItemView usernameItem = mGroupListView.createItemView("用户名");
        usernameItem.setOrientation(QMUICommonListItemView.HORIZONTAL);

        QMUICommonListItemView emailItem = mGroupListView.createItemView("邮箱");
        emailItem.setOrientation(QMUICommonListItemView.HORIZONTAL);

        QMUICommonListItemView pwdItem = mGroupListView.createItemView("密码");
        pwdItem.setOrientation(QMUICommonListItemView.VERTICAL);

        QMUICommonListItemView logoutItem = mGroupListView.createItemView("退出");
        logoutItem.setOrientation(QMUICommonListItemView.VERTICAL);

        User user = MyApplication.getUser();
        if (user != null) {
            usernameItem.setDetailText(user.getUsername());
            emailItem.setDetailText(user.getEmail());
        }
        View.OnClickListener pwdListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    startFragment(new PwdFragment());
                }
            }
        };

        View.OnClickListener logoutListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof QMUICommonListItemView) {
                    MyApplication.setUser(null);
                    SharedPreferences sp = context.getSharedPreferences("user", MODE_PRIVATE);
                    sp.edit().clear().apply();
                    context.change2Login();
                }
            }
        };
        QMUIGroupListView.newSection(getContext())
                .addItemView(usernameItem, null)
                .addItemView(emailItem, null)
                .addItemView(pwdItem, pwdListener)
                .addItemView(logoutItem, logoutListener)
                .addTo(mGroupListView);
    }
}

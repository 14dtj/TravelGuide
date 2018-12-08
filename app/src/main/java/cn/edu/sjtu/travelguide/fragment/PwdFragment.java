package cn.edu.sjtu.travelguide.fragment;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.MainActivity;
import cn.edu.sjtu.travelguide.MyApplication;
import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.entity.User;
import cn.edu.sjtu.travelguide.service.AsyncTask;
import cn.edu.sjtu.travelguide.service.RMPService;

import static android.content.Context.MODE_PRIVATE;

public class PwdFragment extends BaseFragment implements AsyncTask {
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.et_oldPwd)
    EditText oldPassword;
    @BindView(R.id.et_newPwd)
    EditText newPassword;
    @BindView(R.id.complete_btn)
    Button btnComplete;

    private RMPService rmpService;
    private PwdFragment pwdFragment;
    private MainActivity context;

    @Override
    protected View onCreateView() {
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pwd, null);
        ButterKnife.bind(this, layout);
        mTopBar.setTitle("我的");
        rmpService = RMPService.getInstance();
        pwdFragment = this;
        context = (MainActivity) getActivity();
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPassword.getText() != null && newPassword.getText() != null) {
                    String oldPwd = oldPassword.getText().toString();
                    String newPwd = newPassword.getText().toString();
                    User user = MyApplication.getUser();
                    if (user != null) {
                        user.setPassword(newPwd);
                        rmpService.changePwd(user, pwdFragment);
                    }
                }
            }
        });
        return layout;
    }

    @Override
    public void onSuccess() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                MyApplication.setUser(null);
                SharedPreferences sp = context.getSharedPreferences("user", MODE_PRIVATE);
                sp.edit().clear().apply();
                context.change2Login();
            }
        });
    }

    @Override
    public void onFailure() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

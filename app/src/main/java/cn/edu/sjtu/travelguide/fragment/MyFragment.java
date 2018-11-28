package cn.edu.sjtu.travelguide.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.service.RMPService;
import cn.edu.sjtu.travelguide.util.EditTextClearTools;

public class MyFragment extends BaseFragment {
    @BindView(R.id.et_userName)
    EditText userName;
    @BindView(R.id.et_password)
    EditText password;
    @BindView(R.id.login_btn)
    Button btnLogin;
    @BindView(R.id.iv_unameClear)
    ImageView unameClear;
    @BindView(R.id.iv_pwdClear)
    ImageView pwdClear;
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    private RMPService rmpService;

    @Override
    protected View onCreateView() {
        rmpService = RMPService.getInstance();
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_login, null);
        ButterKnife.bind(this, layout);
        mTopBar.setTitle("登录");
        EditTextClearTools.addClearListener(userName, unameClear);
        EditTextClearTools.addClearListener(password, pwdClear);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText() != null && password.getText() != null) {
                    String usernameStr = userName.getText().toString();
                    String passwordStr = password.getText().toString();
                    boolean test = rmpService.login(usernameStr, passwordStr);
                    if (test) {
                        Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return layout;
    }
}

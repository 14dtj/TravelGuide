package cn.edu.sjtu.travelguide.fragment;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.MainActivity;
import cn.edu.sjtu.travelguide.MyApplication;
import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.service.AsyncTask;
import cn.edu.sjtu.travelguide.service.RMPService;
import cn.edu.sjtu.travelguide.util.EditTextClearTools;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends BaseFragment implements AsyncTask {
    @BindView(R.id.et_userName)
    EditText userName;
    @BindView(R.id.et_password)
    EditText password;
    @BindView(R.id.login_btn)
    Button btnLogin;
    @BindView(R.id.register_btn)
    Button btnRegister;
    @BindView(R.id.iv_unameClear)
    ImageView unameClear;
    @BindView(R.id.iv_pwdClear)
    ImageView pwdClear;
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.cb_checkbox)
    CheckBox checkBox;

    private RMPService rmpService;
    private LoginFragment loginFragment;
    private MainActivity context;

    private String usernameStr;
    private String passwordStr;


    @Override
    protected View onCreateView() {
        rmpService = RMPService.getInstance();
        context = (MainActivity) getActivity();
        FrameLayout layout = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.fragment_login, null);
        ButterKnife.bind(this, layout);
        loginFragment = this;
        mTopBar.setTitle("登录");
        EditTextClearTools.addClearListener(userName, unameClear);
        EditTextClearTools.addClearListener(password, pwdClear);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText() != null && password.getText() != null) {
                    usernameStr = userName.getText().toString();
                    passwordStr = password.getText().toString();
                    rmpService.login(usernameStr, passwordStr, loginFragment);
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new RegisterFragment());
            }
        });
        return layout;
    }


    @Override
    public void onSuccess(ResponseBody body) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
                if (checkBox.isChecked()) {
                    SharedPreferences sp = context.getSharedPreferences("user", MODE_PRIVATE);
                    sp.edit().clear().apply();
                    sp.edit()
                            .putString("username", usernameStr)
                            .putString("password", passwordStr)
                            .putInt("role", MyApplication.getUser().getRole())
                            .apply();
                }
                context.change2Home();
            }
        });
    }

    @Override
    public void onFailure() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

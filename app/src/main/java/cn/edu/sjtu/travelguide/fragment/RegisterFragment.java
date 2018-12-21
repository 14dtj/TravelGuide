package cn.edu.sjtu.travelguide.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.sjtu.travelguide.MainActivity;
import cn.edu.sjtu.travelguide.R;
import cn.edu.sjtu.travelguide.entity.User;
import cn.edu.sjtu.travelguide.service.AsyncTask;
import cn.edu.sjtu.travelguide.service.RMPService;
import cn.edu.sjtu.travelguide.util.EditTextClearTools;
import okhttp3.ResponseBody;

public class RegisterFragment extends QMUIFragment implements AsyncTask {
    @BindView(R.id.et_userName)
    EditText userName;
    @BindView(R.id.et_emailName)
    EditText email;
    @BindView(R.id.et_password)
    EditText password;
    @BindView(R.id.main_register_btn)
    Button btnRegister;
    @BindView(R.id.iv_unameClear)
    ImageView unameClear;
    @BindView(R.id.iv_pwdClear)
    ImageView pwdClear;
    @BindView(R.id.iv_emailClear)
    ImageView emailClear;
    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.common_user)
    RadioButton userRadio;
    @BindView(R.id.logistics_business)
    RadioButton logisticsRadio;

    private RMPService rmpService;
    private RegisterFragment registerFragment;
    private MainActivity context;

    @Override
    protected View onCreateView() {
        registerFragment = this;
        rmpService = RMPService.getInstance();
        context = (MainActivity) getActivity();
        FrameLayout layout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_register, null);
        ButterKnife.bind(this, layout);
        mTopBar.setTitle("注册");
        EditTextClearTools.addClearListener(userName, unameClear);
        EditTextClearTools.addClearListener(password, pwdClear);
        EditTextClearTools.addClearListener(password, emailClear);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText() != null && password.getText() != null) {
                    String usernameStr = userName.getText().toString();
                    String passwordStr = password.getText().toString();
                    String emailStr = email.getText().toString();
                    int role = 0;
                    if (userRadio.isChecked()) {
                        role = 1;
                    }
                    User user = new User(usernameStr, emailStr, passwordStr, role);
                    rmpService.register(user, registerFragment);
                }
            }
        });
        return layout;
    }

    @Override
    public void onSuccess(ResponseBody body) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

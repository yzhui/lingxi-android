package me.cl.lingxi.module.member;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.view.CustomProgressDialog;
import me.cl.lingxi.common.widget.JsonCallback;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.module.BaseActivity;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.password)
    EditText mPassword;
    @BindView(R.id.do_password)
    EditText mDoPassword;
    @BindView(R.id.phone)
    EditText mPhone;

    private CustomProgressDialog registerProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registe);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mToolbar.setTitle(R.string.title_bar_reg);
    }

    public void goRegister(View view) {
        registerProgress = new CustomProgressDialog(this, R.string.dialog_loading_reg);
        String uName = mUsername.getText().toString().trim();
        String uPwd = mPassword.getText().toString().trim();
        String uDoPwd = mDoPassword.getText().toString().trim();
        String uPhone = mPhone.getText().toString().trim();
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uPhone)) {
            Utils.toastShow(this, R.string.toast_reg_null);
        } else {
            if (uPwd.equals(uDoPwd)) {
                if (uPhone.length() == 11) {
                    if (isMobileNum(uPhone)) {
                        postRegister(uName, uPwd, uPhone);
                    }
                    Utils.toastShow(this, "请输入正确的手机号码");
                } else {
                    Utils.toastShow(this, R.string.toast_phone_error);
                }
            } else {
                Utils.toastShow(this, R.string.toast_again_error);
            }
        }
    }

    /**
     * 验证手机
     *
     * @param mobiles 手机好
     * @return 是或否
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("1[34578]\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 注册请求
     *
     * @param userName 用户名
     * @param userPwd  秘密
     * @param phone    手机
     */
    public void postRegister(String userName, String userPwd, String phone) {
        registerProgress.show();
        OkGo.<Result>post(Api.register)
                .params("username", userName)
                .params("password", userPwd)
                .params("phone", phone)
                .execute(new JsonCallback<Result>() {

                    @Override
                    public void onSuccess(Response<Result> response) {
                        registerProgress.dismiss();
                        int tag = response.body().getRet();
                        switch (tag) {
                            case 0:
                                Utils.toastShow(RegisterActivity.this, R.string.toast_reg_ok);
                                Intent intent = new Intent(RegisterActivity.this,
                                        LoginActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case 2:
                                Utils.toastShow(RegisterActivity.this, R.string.toast_uname_being);
                                break;
                            case 3:
                                Utils.toastShow(RegisterActivity.this, R.string.toast_phone_being);
                                break;
                        }
                    }

                    @Override
                    public void onError(Response<Result> response) {
                        registerProgress.dismiss();
                        Utils.toastShow(RegisterActivity.this, R.string.toast_reg_error);
                    }
                });
    }

}

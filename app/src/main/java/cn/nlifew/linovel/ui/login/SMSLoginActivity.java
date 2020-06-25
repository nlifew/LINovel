package cn.nlifew.linovel.ui.login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.tencent.captchasdk.TCaptchaDialog;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.ui.BaseActivity;
import cn.nlifew.linovel.utils.ToastUtils;

import static cn.nlifew.linovel.ui.login.SmsViewModel.State.STATUS_FAILED;
import static cn.nlifew.linovel.ui.login.SmsViewModel.State.STATUS_OK;
import static cn.nlifew.linovel.ui.login.SmsViewModel.State.STATUS_READY;
import static cn.nlifew.linovel.ui.login.SmsViewModel.State.STATUS_VERIFY;

public class SMSLoginActivity extends BaseActivity implements TextWatcher {
    private static final String TAG = "SMSLoginActivity";

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(0xFFFFCC00);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_login);

        mViewModel = new ViewModelProvider(this).get(SmsViewModel.class);
        mViewModel.mState.observe(this, this::onLoginStateChanged);
        SmsViewModel.sCountDownTimer.observe(this, (s) -> {
            if (mLoginButton.getTag() == mPhoneView) {
                syncButtonWithPhoneView();
            }
        });

        mSmsView = findViewById(R.id.activity_login_sms);
        mPhoneView = findViewById(R.id.activity_login_phone);
        mLoginButton = findViewById(R.id.activity_login_login);

        mSmsView.addTextChangedListener(this);
        mSmsView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                syncButtonWithSmsView();
            }
            return false;
        });
        mSmsView.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && mLoginButton.isEnabled()) {
                onLoginButtonClick(mLoginButton);
            }
            return false;
        });

        mPhoneView.addTextChangedListener(this);
        mPhoneView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                syncButtonWithPhoneView();
            }
            return false;
        });
        mPhoneView.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && mLoginButton.isEnabled()) {
                onLoginButtonClick(mLoginButton);
            }
            return false;
        });

        mLoginButton.setOnClickListener(this::onLoginButtonClick);
        syncButtonWithPhoneView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mShouldSetResult) {
            setResult(RESULT_CANCELED);
        }
    }

    private Button mLoginButton;
    private TextInputEditText mPhoneView;
    private TextInputEditText mSmsView;

    private SmsViewModel mViewModel;
    private boolean mShouldSetResult = true;
    private ProgressDialog mProgressDialog;


    private void syncButtonWithPhoneView() {
        mLoginButton.setTag(mPhoneView);

        int second = SmsViewModel.sCountDownTimer.getCurrentSecond();
        if (second != 0) {
            mLoginButton.setEnabled(false);
            mLoginButton.setText(second + "秒后发送验证码");
        }
        else {
            mLoginButton.setText("发送验证码");
            CharSequence text = mPhoneView.getText();
            mLoginButton.setEnabled(text != null && text.length() == 11);
        }
    }

    private void syncButtonWithSmsView() {
        mLoginButton.setTag(mSmsView);
        mLoginButton.setText("登录");

        CharSequence text = mSmsView.getText();
        mLoginButton.setEnabled(text != null && text.length() == 6);
    }

    private void onLoginButtonClick(View v) {
        if (mLoginButton.getTag() == mPhoneView) {
            mViewModel.sendMsmCode(mPhoneView.getText().toString(), null, null);
//            mPhoneView.setEnabled(false);
            SmsViewModel.sCountDownTimer.startCountingDown(20);
        }
        else if (mLoginButton.getTag() == mSmsView) {
            if (mProgressDialog == null) {
                mProgressDialog = createProgressDialog();
            }
            mProgressDialog.show();
            mViewModel.tryToLogin(mPhoneView.getText().toString(),
                    mSmsView.getText().toString());
        }
    }

    private ProgressDialog createProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("正在登录");
        dialog.setMessage("请稍等 ...");
        return dialog;
    }


    @Override
    public void afterTextChanged(Editable s) {
        if (mLoginButton.getTag() == mPhoneView) {
            syncButtonWithPhoneView();
        }
        else if (mLoginButton.getTag() == mSmsView){
            syncButtonWithSmsView();
        }
    }


    private void onLoginStateChanged(SmsViewModel.State state) {
        if (state == null) {
            return;
        }
        ToastUtils.getInstance(this).show(state.message);
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        switch (state.status) {
            case STATUS_READY: {    // 验证码已发送，允许填写
                mSmsView.setEnabled(true);
                mPhoneView.setEnabled(true);
                break;
            }
            case STATUS_VERIFY: {   // 需要验证码
                if (! isFinishing()) {
                    createTCapchaDialog().show();
                }
                break;
            }
            case STATUS_OK: {       // 成功
                mShouldSetResult = false;
                SmsViewModel.sCountDownTimer.cancelCountingDown();
                setResult(RESULT_OK);
                finish();
            }

        }
    }

    private TCaptchaDialog createTCapchaDialog() {
        return new TCaptchaDialog(this, true, null,
                "1600000770", (jsonObject) ->
            mViewModel.updateVerifyData(mPhoneView.getText().toString(),
                    jsonObject), null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // eat it.
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // eat it.
    }
}

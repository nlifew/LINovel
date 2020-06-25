package cn.nlifew.linovel.ui.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import cn.nlifew.linovel.R;
import cn.nlifew.linovel.ui.BaseActivity;
import cn.nlifew.linovel.utils.ToastUtils;

public class LoginActivity extends BaseActivity implements TextWatcher {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_x);

        mIdView = findViewById(R.id.activity_login_id);
        mLoginButton = findViewById(R.id.activity_login_login);
        mPasswordView = findViewById(R.id.activity_login_password);

        mIdView.addTextChangedListener(this);
        mPasswordView.addTextChangedListener(this);
        mPasswordView.setOnEditorActionListener(this::onPasswordViewFinish);
        mLoginButton.setOnClickListener(this::onLoginButtonClick);

        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mViewModel.mState.observe(this, this::onLoginStateChanged);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (mShouldSetStatus) {
            setResult(RESULT_CANCELED);
        }
    }

    private LoginViewModel mViewModel;
    private ProgressDialog mProgressDialog;

    private TextInputLayout mIdLayout, mPasswordLayout;
    private TextInputEditText mIdView, mPasswordView;
    private Button mLoginButton;
    private boolean mShouldSetStatus = true;

    private ProgressDialog createProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("正在登录");
        dialog.setMessage("请稍等 ...");
        return dialog;
    }

    private boolean onPasswordViewFinish(TextView tv, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && mLoginButton.isEnabled()) {
            onLoginButtonClick(mLoginButton);
        }
        return false;
    }

    private void onLoginButtonClick(View view) {
        if (mProgressDialog == null) {
            mProgressDialog = createProgressDialog();
        }
        mProgressDialog.show();
        mViewModel.tryToLogin(mIdView.getText().toString(), mPasswordView.getText().toString());
    }

    private void onLoginStateChanged(LoginViewModel.State state) {
        if (state == null) {
            return;
        }

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        switch (state.targetView) {
            case LoginViewModel.State.TARGET_ID: {
                if (mIdLayout == null) {
                    mIdLayout = findViewById(R.id.activity_login_id_layout);
                }
                mIdLayout.setError(state.message);
                break;
            }
            case LoginViewModel.State.TARGET_PASSWORD: {
                if (mPasswordLayout == null) {
                    mPasswordLayout = findViewById(R.id.activity_login_password_layout);
                }
                mPasswordLayout.setError(state.message);
                break;
            }
            case LoginViewModel.State.TARGET_TOAST: {
                ToastUtils.getInstance(this).show(state.message);
                break;
            }
        }

        switch (state.status) {
            case LoginViewModel.State.STATUS_FAILED: {
                mLoginButton.setEnabled(false);
                break;
            }
            case LoginViewModel.State.STATUS_READY: {
                mLoginButton.setEnabled(true);
                break;
            }
            case LoginViewModel.State.STATUS_SUCCESS: {
                mShouldSetStatus = false;
                setResult(RESULT_OK);
                finish();
                break;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mIdLayout != null) {
            mIdLayout.setError(null);
        }
        if (mPasswordLayout != null) {
            mPasswordLayout.setError(null);
        }
        mViewModel.checkFormData(mIdView.getText(), mPasswordView.getText());
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

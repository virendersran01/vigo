package com.retrytech.vilo.view.base;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ItemLoginBinding;
import com.retrytech.vilo.utils.Const;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.utils.NetWorkChangeReceiver;
import com.retrytech.vilo.utils.SessionManager;
import com.retrytech.vilo.view.home.MainActivity;
import com.retrytech.vilo.view.web.WebViewActivity;

import org.json.JSONException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.retrytech.vilo.utils.Global.RC_SIGN_IN;


/**
 * Created by DeveloperAndroid on 06/05/2019.
 */
@SuppressLint("NewApi")
public abstract class BaseActivity extends AppCompatActivity {


    private NetWorkChangeReceiver netWorkChangeReceiver = null;
    public SessionManager sessionManager = null;
    private BottomSheetDialog dialog;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        sessionManager = new SessionManager(this);
    }

    protected void startReceiver() {
        netWorkChangeReceiver = new NetWorkChangeReceiver(this::showHideInternet);
        registerNetworkBroadcastForNougat();
    }

    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(), (object, response) -> {
                        try {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("device_token", Global.FIREBASE_DEVICE_TOKEN);
                            hashMap.put("user_email", object.getString("email"));
                            hashMap.put("full_name", object.getString("name"));
                            hashMap.put("login_type", Const.FACEBOOK_LOGIN);
                            hashMap.put("user_name", object.getString("id"));
                            hashMap.put("identity", object.getString("id"));
                            registerUser(hashMap);
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Log.i("", "");
        }

        @Override
        public void onError(FacebookException error) {
            Log.i("", "");
        }
    };

    private void slideToTop(View view) {
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, view.getHeight(), 0f);
        animation.setDuration(300);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    private void showHideInternet(Boolean isOnline) {
        final TextView tvInternetStatus = findViewById(R.id.tv_internet_status);

        if (isOnline != null && isOnline) {
            if (tvInternetStatus != null && tvInternetStatus.getVisibility() == View.VISIBLE && tvInternetStatus.getText().toString().equalsIgnoreCase(getString(R.string.no_internet_connection))) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.kellygreen));
                tvInternetStatus.setText(R.string.back_online);
                new Handler().postDelayed(() -> slideToBottom(tvInternetStatus), 200);
            }
        } else {
            if (tvInternetStatus != null) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.india_red));
                tvInternetStatus.setText(R.string.no_internet_connection);
                if (tvInternetStatus.getVisibility() == View.GONE) {
                    slideToTop(tvInternetStatus);
                }
            }
        }
    }

    private void registerNetworkBroadcastForNougat() {
        registerReceiver(netWorkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(netWorkChangeReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    /**
     * Hide keyboard when user click anywhere on screen
     *
     * @param event contains int value for motion event actions
     * @return boolean value of touch event.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        closeKeyboard();
        return true;
    }

    private void slideToBottom(final View view) {
        TranslateAnimation animation = new TranslateAnimation(0f, 0f, 0f, view.getHeight());
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.i("", "");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.i("", "");
            }
        });
        view.startAnimation(animation);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    public void openActivity(Activity activity) {
        startActivity(new Intent(this, activity.getClass()));
    }

    public void closeKeyboard() {
        try {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null && getCurrentFocus() != null) {
                im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void initFaceBook() {
        CallbackManager callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, mFacebookCallback);
    }

    public void initLogin(Context context, OnLoginSheetClose close) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        dialog = new BottomSheetDialog(context);

        ItemLoginBinding loginBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_login, null, false);
        dialog.setContentView(loginBinding.getRoot());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setDismissWithAnimation(true);

        FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        loginBinding.setOnGoogleClick(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            ((MainActivity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        loginBinding.setOnTermClick(v -> {
            Intent intent = new Intent(this, WebViewActivity.class);
            startActivity(intent);
        });

        loginBinding.setOnFacebookClick(v -> {
            LoginManager.getInstance().logInWithReadPermissions((MainActivity) context, Collections.singletonList("public_profile"));

            LoginManager.getInstance().logInWithReadPermissions(
                    (MainActivity) context,
                    Arrays.asList("user_photos", "email", "user_birthday", "public_profile")
            );
        });

        loginBinding.setOnCloseClick(v -> dismissBottom());

        dialog.setOnDismissListener(view -> close.onClose());

        dialog.show();

    }

    public void dismissBottom() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void registerUser(Map<String, Object> hashMap) {
        disposable.add(Global.initRetrofit().registrationUser((HashMap<String, Object>) hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((user, throwable) -> {
                    if (user != null && user.getStatus()) {
                        sessionManager.saveBooleanValue(Const.IS_LOGIN, true);
                        sessionManager.saveUser(user);
                        Global.ACCESS_TOKEN = sessionManager.getUser().getData() != null ? sessionManager.getUser().getData().getToken() : "";
                        Global.USER_ID = sessionManager.getUser().getData() != null ? sessionManager.getUser().getData().getUserId() : "";
                        sessionManager.saveBooleanValue("notification", true);
                        dismissBottom();
                    }
                }));
    }

    public interface OnLoginSheetClose {
        void onClose();
    }

    protected void setStatusBarTransparentFlag() {

        View decorView = getWindow().getDecorView();
        decorView.setOnApplyWindowInsetsListener((v, insets) -> {
            WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
            return defaultInsets.replaceSystemWindowInsets(
                    defaultInsets.getSystemWindowInsetLeft(),
                    0,
                    defaultInsets.getSystemWindowInsetRight(),
                    defaultInsets.getSystemWindowInsetBottom());
        });
        ViewCompat.requestApplyInsets(decorView);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
    }

    protected void removeStatusBarTransparentFlag() {
        View decorView = getWindow().getDecorView();
        decorView.setOnApplyWindowInsetsListener((v, insets) -> {
            WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
            return defaultInsets.replaceSystemWindowInsets(
                    defaultInsets.getSystemWindowInsetLeft(),
                    defaultInsets.getSystemWindowInsetTop(),
                    defaultInsets.getSystemWindowInsetRight(),
                    defaultInsets.getSystemWindowInsetBottom());
        });
        ViewCompat.requestApplyInsets(decorView);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }
}
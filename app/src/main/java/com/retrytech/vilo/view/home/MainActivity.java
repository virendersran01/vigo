package com.retrytech.vilo.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.retrytech.vilo.R;
import com.retrytech.vilo.adapter.MainViewPagerAdapter;
import com.retrytech.vilo.databinding.ActivityMainBinding;
import com.retrytech.vilo.databinding.BtnAddLytBinding;
import com.retrytech.vilo.utils.Const;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.utils.GlobalApi;
import com.retrytech.vilo.utils.TimerTask;
import com.retrytech.vilo.view.base.BaseActivity;
import com.retrytech.vilo.view.recordvideo.CameraActivity;
import com.retrytech.vilo.viewmodel.MainViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.retrytech.vilo.utils.Global.RC_SIGN_IN;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    private TimerTask timerTask;
    private static int CAMERA = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparentFlag();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new MainViewModel()).createFor()).get(MainViewModel.class);
        startReceiver();
        initView();
        initTabLayout();
        initFaceBook();
        rewardDailyCheckIn();
        Log.d("TOKEN", "onCreate: " + Global.FIREBASE_DEVICE_TOKEN);
        binding.setViewModel(viewModel);
    }

    private void initTabLayout() {
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOffscreenPageLimit(4);
    }

    private void initView() {
        if (!Global.ACCESS_TOKEN.isEmpty()) {
            timerTask = new TimerTask();
        }
        for (int i = 0; i <= 4; i++) {
            switch (i) {
                case 0:
                    binding.tabLout.addTab(binding.tabLout.newTab().setIcon(R.drawable.ic_home_tab));
                    TabLayout.Tab tab = binding.tabLout.getTabAt(0);
                    if (tab != null && tab.getIcon() != null) {
                        tab.getIcon().setTint(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                    }
                    break;
                case 1:
                    binding.tabLout.addTab(binding.tabLout.newTab().setIcon(R.drawable.ic_explore_tab));
                    break;
                case 2:
                    BtnAddLytBinding addLytBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.btn_add_lyt, null, false);
                    binding.tabLout.addTab(binding.tabLout.newTab().setCustomView(addLytBinding.getRoot()));
                    break;
                case 3:
                    binding.tabLout.addTab(binding.tabLout.newTab().setIcon(R.drawable.ic_bell_tab));
                    break;
                case 4:
                    binding.tabLout.addTab(binding.tabLout.newTab().setIcon(R.drawable.ic_profile_tab));
                    break;

                default:
                    break;
            }
        }
        binding.tabLout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab != null) {
                    viewModel.onStop.postValue(tab.getPosition() != 0);
                    switch (tab.getPosition()) {
                        case 0:
                            setStatusBarTransparentFlag();
                            viewModel.selectedPosition.setValue(0);
                            binding.viewPager.setCurrentItem(0);
                            tab.setIcon(R.drawable.ic_home_tab);
                            if (tab.getIcon() != null)
                                tab.getIcon().setTint(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                            break;
                        case 1:
                            removeStatusBarTransparentFlag();
                            viewModel.selectedPosition.setValue(1);
                            binding.viewPager.setCurrentItem(1);
                            tab.setIcon(R.drawable.ic_explore_tab);
                            if (tab.getIcon() != null)
                                tab.getIcon().setTint(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                            break;
                        case 2:
                            if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                                startActivityForResult(new Intent(MainActivity.this, CameraActivity.class), CAMERA);
                            } else {
                                initLogin(MainActivity.this, () -> {
                                    TabLayout.Tab tab1 = binding.tabLout.getTabAt(0);
                                    if (tab1 != null) {
                                        tab1.select();
                                    }
                                });
                            }
                            break;
                        case 3:
                            removeStatusBarTransparentFlag();
                            viewModel.selectedPosition.setValue(2);
                            binding.viewPager.setCurrentItem(2);
                            tab.setIcon(R.drawable.ic_bell_tab);
                            if (tab.getIcon() != null)
                                tab.getIcon().setTint(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                            break;
                        case 4:
                            removeStatusBarTransparentFlag();
                            if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                                binding.viewPager.setCurrentItem(3);
                                viewModel.selectedPosition.setValue(3);
                                tab.setIcon(R.drawable.ic_profile_tab);
                                if (tab.getIcon() != null)
                                    tab.getIcon().setTint(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                            } else {
                                initLogin(MainActivity.this, () -> {
                                    TabLayout.Tab tab1 = binding.tabLout.getTabAt(0);
                                    if (tab1 != null) {
                                        tab1.select();
                                    }
                                });
                            }
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.ic_home_tab);
                        if (tab.getIcon() != null)
                            tab.getIcon().setTint(ContextCompat.getColor(MainActivity.this, R.color.white));
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_explore_tab);
                        if (tab.getIcon() != null)
                            tab.getIcon().setTint(ContextCompat.getColor(MainActivity.this, R.color.white));
                        break;
                    case 2:

                        break;
                    case 3:
                        tab.setIcon(R.drawable.ic_bell_tab);
                        if (tab.getIcon() != null)
                            tab.getIcon().setTint(ContextCompat.getColor(MainActivity.this, R.color.white));
                        break;
                    case 4:
                        tab.setIcon(R.drawable.ic_profile_tab);
                        if (tab.getIcon() != null)
                            tab.getIcon().setTint(ContextCompat.getColor(MainActivity.this, R.color.white));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 2) {
                    if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                        startActivityForResult(new Intent(MainActivity.this, CameraActivity.class), CAMERA);
                    } else {
                        initLogin(MainActivity.this, () -> {
                            TabLayout.Tab tab1 = binding.tabLout.getTabAt(0);
                            if (tab1 != null) {
                                tab1.select();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else if (resultCode == RESULT_OK && requestCode == CAMERA) {
            TabLayout.Tab tab = binding.tabLout.getTabAt(0);
            if (tab != null) {
                tab.select();
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("device_token", Global.FIREBASE_DEVICE_TOKEN);
                hashMap.put("user_email", account.getEmail());
                hashMap.put("full_name", account.getDisplayName());
                hashMap.put("login_type", Const.GOOGLE_LOGIN);
                hashMap.put("user_name", Objects.requireNonNull(account.getEmail()).split("@")[0]);
                hashMap.put("identity", account.getEmail());
                registerUser(hashMap);
                Log.e("Google login", "handleSignInResult: " + account.getEmail());
            }
            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google login", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void rewardDailyCheckIn() {
        if (sessionManager.getStringValue("intime").isEmpty()) {
            new GlobalApi().rewardUser("2");
            sessionManager.saveStringValue("intime", new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date()));
        } else {
            try {
                simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                Date date1 = simpleDateFormat.parse(sessionManager.getStringValue("intime"));
                Date date2 = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
                long difference = 0;
                if (date2 != null && date1 != null) {
                    difference = date2.getTime() - date1.getTime();
                }

                int days = (int) (difference / (1000 * 60 * 60 * 24));
                int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                hours = (hours < 0 ? -hours : hours);
                if (hours >= 24) {
                    new GlobalApi().rewardUser("2");
                    sessionManager.saveStringValue("intime", new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date()));

                }
                Log.i("======= Hours", " :: " + hours + ":" + min);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    protected void onPause() {
        viewModel.onStop.setValue(true);
        if (timerTask != null) {
            timerTask.stopTimerTask();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        viewModel.onStop.setValue(true);
        if (timerTask != null) {
            timerTask.stopTimerTask();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkChanges();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        long size = 0;
        File[] files = getExternalCacheDir().listFiles();
        if (files != null) {
            for (File f : files) {
                size = size + f.length();
            }
        }
        Log.d("TAG", "onResume: " + size);
        if (binding.tabLout.getSelectedTabPosition() == 0) {
            viewModel.onStop.setValue(false);
        }
        if (binding.tabLout.getSelectedTabPosition() >= 3) {
            viewModel.selectedPosition.setValue(binding.tabLout.getSelectedTabPosition() - 1);
        } else {
            viewModel.selectedPosition.setValue(binding.tabLout.getSelectedTabPosition());
        }
        if (timerTask != null) {
            timerTask.doTimerTask();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (viewModel.selectedPosition.getValue() != null && viewModel.selectedPosition.getValue() != 0) {
            TabLayout.Tab tab = binding.tabLout.getTabAt(0);
            if (tab != null) {
                tab.select();
            }
            viewModel.selectedPosition.setValue(0);
        } else if (viewModel.selectedPosition.getValue() != null && viewModel.selectedPosition.getValue() == 0 && !viewModel.isBack) {
            viewModel.isBack = true;
            Toast.makeText(this, "Press Again To Exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> viewModel.isBack = false, 2000);
        } else {
            super.onBackPressed();
        }
    }
}

package com.retrytech.vilo.view.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ActivitySettingsBinding;
import com.retrytech.vilo.utils.Const;
import com.retrytech.vilo.utils.CustomDialogBuilder;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.view.SplashActivity;
import com.retrytech.vilo.view.base.BaseActivity;
import com.retrytech.vilo.view.wallet.WalletActivity;
import com.retrytech.vilo.view.web.WebViewActivity;
import com.retrytech.vilo.viewmodel.SettingsActivityViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import java.util.Calendar;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;

public class SettingsActivity extends BaseActivity {

    ActivitySettingsBinding binding;
    SettingsActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new SettingsActivityViewModel()).createFor()).get(SettingsActivityViewModel.class);

        initListeners();
        initObserve();
        binding.notiSwitch.setChecked(sessionManager.getBooleanValue("notification"));
    }


    private void initListeners() {

        binding.notiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

        });
        binding.notiSwitch.setOnClickListener(v -> {
            if (binding.notiSwitch.isChecked()) {
                if (!Global.FIREBASE_DEVICE_TOKEN.isEmpty()) {
                    viewModel.updateFireBaseToken(Global.FIREBASE_DEVICE_TOKEN);
                    sessionManager.saveBooleanValue("notification", true);
                }
            } else {
                viewModel.updateFireBaseToken(" ");
                sessionManager.saveBooleanValue("notification", false);
            }
        });

        binding.loutPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, WebViewActivity.class);
            intent.putExtra("url", "http://instamask.invatomarket.com/terms&conditions.html");
            startActivity(intent);
        });
        binding.loutShareProfile.setOnClickListener(v -> shareProfile());
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.loutWallet.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, WalletActivity.class)));
        binding.loutVerify.setOnClickListener(v -> {
            if (sessionManager.getUser().getData().getFollowersCount() >= 1000) {
                startActivity(new Intent(SettingsActivity.this, VerificationActivity.class));
            } else {
                Toast.makeText(this, "You can verify your profile once you have 1k Followers...", Toast.LENGTH_SHORT).show();
            }
        });
        binding.loutMycode.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, MyQRActivity.class)));

        binding.loutLogout.setOnClickListener(v -> new CustomDialogBuilder(this).showSimpleDialog("Log out", "Do you really want\nto log out?", "Cancel", "Log out", new CustomDialogBuilder.OnDismissListener() {
            @Override
            public void onPositiveDismiss() {

                viewModel.logOutUser();
            }

            @Override
            public void onNegativeDismiss() {
                Log.i("", "");
            }
        }));
    }

    private void shareProfile() {

        String json = new Gson().toJson(sessionManager.getUser());
        String title = sessionManager.getUser().getData().getFullName();

        Log.i("ShareJson", "Json Object: " + json);
        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle(title)
                .setContentImageUrl(Const.ITEM_BASE_URL + sessionManager.getUser().getData().getUserProfile())
                .setContentDescription("Hey There, Check This BubbleTok Profile")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(new ContentMetadata().addCustomMetadata("data", json));

        LinkProperties lp = new LinkProperties()
                .setFeature("sharing")
                .setCampaign("Content launch")
                .setStage("User")
                .addControlParameter("$desktop_url", "https://codecanyon.net/item/media-loot-the-ultimate-social-media-downloader/25391411")
                .addControlParameter("custom", "data")
                .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));

        buo.generateShortUrl(this, lp, (url, error) -> {

            Log.d("PROFILEURL", "shareProfile: " + url);

            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            String shareBody = url + "\nHey, check my profile on BubbleTok App";
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Profile Share");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(intent, "Share Profile"));
        });

    }

    private void initObserve() {
        viewModel.logOut.observe(this, logout -> logOutUser());
        viewModel.updateToken.observe(this, updateToken -> {
            if (binding.notiSwitch.isChecked()) {
                Toast.makeText(this, "Notifications Turned On", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications Turned Off", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logOutUser() {
        if (sessionManager.getUser().getData().getLoginType().equals("google")) {
            GoogleSignInOptions gso = new GoogleSignInOptions.
                    Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                    build();

            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(SettingsActivity.this, gso);
            googleSignInClient.signOut();

        } else {
            LoginManager.getInstance().logOut();
        }

        sessionManager.clear();
        Global.ACCESS_TOKEN = "";
        Global.USER_ID = "";
        startActivity(new Intent(SettingsActivity.this, SplashActivity.class));
        finishAffinity();
    }
}
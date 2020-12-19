package com.retrytech.vilo.view.share;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.FragmentShareSheetBinding;
import com.retrytech.vilo.model.videos.Video;
import com.retrytech.vilo.utils.Const;
import com.retrytech.vilo.utils.CustomDialogBuilder;
import com.retrytech.vilo.view.home.ReportSheetFragment;
import com.retrytech.vilo.viewmodel.ShareSheetViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Calendar;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;

public class ShareSheetFragment extends BottomSheetDialogFragment {


    FragmentShareSheetBinding binding;
    ShareSheetViewModel viewModel;
    private CustomDialogBuilder customDialogBuilder;
    private static final int MY_PERMISSIONS_REQUEST = 101;

    public ShareSheetFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog dialog = (BottomSheetDialog) dialog1;
            dialog.setCanceledOnTouchOutside(false);

        });

        return bottomSheetDialog;

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share_sheet, container, false);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new ShareSheetViewModel()).createFor()).get(ShareSheetViewModel.class);
        customDialogBuilder = new CustomDialogBuilder(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initListeners();
        initObserve();
    }

    private void initView() {
        binding.setViewModel(viewModel);
        if (getArguments() != null && getArguments().getString("video") != null) {
            viewModel.video = new Gson().fromJson(getArguments().getString("video"), Video.Data.class);
        }
        createVideoShareLink();

    }


    private void initListeners() {
        binding.btnCopy.setOnClickListener(v -> {
            if (getActivity() != null) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Video link", viewModel.shareUrl);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getActivity(), "Copied Clipboard To Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.btnDownload.setOnClickListener(view -> initPermission());
        binding.btnReport.setOnClickListener(view -> {
            ReportSheetFragment fragment = new ReportSheetFragment();
            Bundle args = new Bundle();
            args.putString("postid", viewModel.video.getPostId());
            args.putInt("reporttype", 1);
            fragment.setArguments(args);
            if (getParentFragment() != null) {
                fragment.show(getParentFragment().getChildFragmentManager(), fragment.getClass().getSimpleName());
            }
            dismiss();
        });
    }

    private void initObserve() {
        viewModel.onItemClick.observe(this, type -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            switch (type) {
                case 1:     // Instagram
                    share.setPackage("com.instagram.android");
                    break;
                case 2:   // facebook
                    share.setPackage("com.facebook.katana");
                    break;
                case 3:   // whatsapp
                    share.setPackage("com.whatsapp");
                    break;
                // other
                default:

                    break;

            }
            String shareBody = viewModel.shareUrl + "\nWatch this amazing video on BubbleTok App";
            share.setType("text/plain");
            share.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Video");
            share.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(share, "Share Video"));
            dismiss();
        });
    }


    private void initPermission() {
        if (getActivity() != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST);
            } else {
                startDownload();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startDownload();
        }
    }

    private void startDownload() {
        Log.d("DOWNLOAD", "startDownload: ");
        PRDownloader.download(Const.ITEM_BASE_URL + viewModel.video.getPostVideo(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), viewModel.video.getPostVideo())
                .build()
                .setOnStartOrResumeListener(() -> customDialogBuilder.showLoadingDialog())
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        customDialogBuilder.hideLoadingDialog();
                        Toast.makeText(getActivity(), "Saved Successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Error error) {
                        customDialogBuilder.hideLoadingDialog();
                        Log.d("DOWNLOAD", "onError: " + error.getConnectionException().getMessage());
                    }
                });
    }

    public File getPath() {
        if (getActivity() != null) {
            String state = Environment.getExternalStorageState();
            File filesDir;
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // We can read and write the media
                filesDir = getActivity().getExternalFilesDir(null);
            } else {
                // Load another directory, probably local memory
                filesDir = getActivity().getFilesDir();
            }
            return filesDir;
        }
        return new File(Environment.getRootDirectory().getAbsolutePath());
    }

    private void createVideoShareLink() {
        String json = new Gson().toJson(viewModel.video);
        String title = viewModel.video.getPostDescription();

        Log.i("ShareJson", "Json Object: " + json);
        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle(title)
                .setContentImageUrl(Const.ITEM_BASE_URL + viewModel.video.getPostImage())
                .setContentDescription(viewModel.video.getPostDescription())
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(new ContentMetadata().addCustomMetadata("data", json));

        LinkProperties lp = new LinkProperties()
                .setFeature("sharing")
                .setCampaign("Content launch")
                .setStage("Video")
                .addControlParameter("custom", "data")
                .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));

        if (getActivity() != null) {
            buo.generateShortUrl(getActivity(), lp, (url, error) -> {
                Log.d("VIDEO_URL", "shareProfile: " + url);
                viewModel.shareUrl = url;
            });
        }

    }


}
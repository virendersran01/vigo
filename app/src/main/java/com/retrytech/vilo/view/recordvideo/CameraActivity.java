package com.retrytech.vilo.view.recordvideo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ActivityCameraBinding;
import com.retrytech.vilo.databinding.DailogProgressBinding;
import com.retrytech.vilo.utils.AutoFitPreviewBuilder;
import com.retrytech.vilo.utils.Const;
import com.retrytech.vilo.utils.CustomDialogBuilder;
import com.retrytech.vilo.view.base.BaseActivity;
import com.retrytech.vilo.view.media.BottomSheetImagePicker;
import com.retrytech.vilo.view.music.MusicFrameFragment;
import com.retrytech.vilo.viewmodel.CameraViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import java.io.File;
import java.util.ArrayList;

public class CameraActivity extends BaseActivity {

    private static final int MY_PERMISSIONS_REQUEST = 101;
    private CameraViewModel viewModel;
    private ActivityCameraBinding binding;
    private CustomDialogBuilder customDialogBuilder;
    private Dialog mBuilder;
    private DailogProgressBinding progressBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new CameraViewModel()).createFor()).get(CameraViewModel.class);
        customDialogBuilder = new CustomDialogBuilder(CameraActivity.this);
        initView();
        initListener();
        initObserve();
        initProgressDialog();
        binding.setViewModel(viewModel);
    }


    @SuppressLint("RestrictedApi")
    private void initView() {
        String musicUrl = getIntent().getStringExtra("music_url");
        if (musicUrl != null && !musicUrl.isEmpty()) {
            downLoadMusic(getIntent().getStringExtra("music_url"));
            if (getIntent().getStringExtra("music_title") != null) {
                binding.tvSoundTitle.setText(getIntent().getStringExtra("music_title"));
            }
            if (getIntent().getStringExtra("sound_id") != null) {
                viewModel.soundId = getIntent().getStringExtra("sound_id");
            }
        }

        if (viewModel.onDurationUpdate.getValue() != null) {
            binding.progressBar.enableAutoProgressView(viewModel.onDurationUpdate.getValue());
        }
        binding.progressBar.setDividerColor(Color.WHITE);
        binding.progressBar.setDividerEnabled(true);
        binding.progressBar.setDividerWidth(4);
        binding.progressBar.setListener(mills -> {
            viewModel.isEnabled.set(mills >= 14500);
            if (mills == viewModel.onDurationUpdate.getValue()) {
                stopRecording();
            }
        });
        binding.ivSelect.setOnClickListener(v -> Toast.makeText(this, "Make sure video is longer than 15s...!", Toast.LENGTH_LONG).show());
        binding.progressBar.setShader(new int[]{ContextCompat.getColor(this, R.color.colorTheme2), ContextCompat.getColor(this, R.color.colorTheme1), ContextCompat.getColor(this, R.color.colorTheme)});
    }

    private void initListener() {
        binding.btnCapture.setOnClickListener(v -> {
            if (!viewModel.isRecording.get()) {
                startReCording();
            } else {
                stopRecording();
            }
        });
        binding.btnFlip.setOnClickListener(v -> {
            viewModel.isFacingFront.set(!viewModel.isFacingFront.get());
            if (viewModel.isFacingFront.get()) {
                viewModel.lensFacing = CameraX.LensFacing.FRONT;
            } else {
                viewModel.lensFacing = CameraX.LensFacing.BACK;
            }
            recreateCamera();
        });
        binding.tvSelect.setOnClickListener(v -> {
            BottomSheetImagePicker bottomSheetImagePicker = BottomSheetImagePicker.Companion.getNewInstance(1);
            bottomSheetImagePicker.setOnDismiss(uri -> {
                if (!uri.isEmpty()) {
                    File file = new File(uri);
                    // Get length of file in bytes
                    long fileSizeInBytes = file.length();
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    long fileSizeInMB = fileSizeInKB / 1024;
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(this, Uri.fromFile(new File(uri)));
                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long timeInMilliSec = Long.parseLong(time);
                    if (timeInMilliSec > 60000) {
                        customDialogBuilder.showSimpleDialog("Too long video", "This video is longer than 1 min.\nPlease select onOther..",
                                "Cancel", "Select onOther", new CustomDialogBuilder.OnDismissListener() {
                                    @Override
                                    public void onPositiveDismiss() {
                                        bottomSheetImagePicker.show(getSupportFragmentManager(), BottomSheetImagePicker.class.getSimpleName());
                                    }

                                    @Override
                                    public void onNegativeDismiss() {
                                        Log.i("", "");
                                    }
                                });
                    } else if (fileSizeInMB < 60) {

                        viewModel.videoPaths = new ArrayList<>();
                        viewModel.videoPaths.add(uri);

                        customDialogBuilder.showLoadingDialog();
                    } else {
                        customDialogBuilder.showSimpleDialog("Too long video's size", "This video's size is grater than 60Mb.\nPlease select onOther..",
                                "Cancel", "Select onOther", new CustomDialogBuilder.OnDismissListener() {
                                    @Override
                                    public void onPositiveDismiss() {
                                        bottomSheetImagePicker.show(getSupportFragmentManager(), BottomSheetImagePicker.class.getSimpleName());
                                    }

                                    @Override
                                    public void onNegativeDismiss() {
                                        Log.i("", "");
                                    }
                                });
                    }
                    retriever.release();
                }
            });
            bottomSheetImagePicker.show(getSupportFragmentManager(), BottomSheetImagePicker.class.getSimpleName());
        });
        binding.ivClose.setOnClickListener(v -> customDialogBuilder.showSimpleDialog("Are you sure", "Do you really wan to go back ?",
                "No", "Yes", new CustomDialogBuilder.OnDismissListener() {
                    @Override
                    public void onPositiveDismiss() {
                        //1.86
                        onBackPressed();
                    }

                    @Override
                    public void onNegativeDismiss() {
                        Log.i("TAG", "onTranscodeFailed: ");
                    }
                }));
    }


    private void recreateCamera() {
        binding.viewFinder.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                CameraX.unbindAll();
                startCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Log.i("TAG", "onTranscodeFailed: ");
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                Log.i("TAG", "onTranscodeFailed: ");
            }
        });


    }


    @SuppressLint("RestrictedApi")
    private void startCamera() {
        TextureView viewFinder = binding.viewFinder;
        AspectRatio ratio = AspectRatio.RATIO_16_9;
        viewModel.builder = new PreviewConfig.Builder();
        viewModel.previewConfig = viewModel.builder.setTargetAspectRatio(ratio)
                .setLensFacing(viewModel.lensFacing)
                .setTargetRotation(Surface.ROTATION_90)
                .build();
        viewModel.preview = AutoFitPreviewBuilder.Companion.build(viewModel.previewConfig, viewFinder);
        viewModel.builder1 = new VideoCaptureConfig.Builder();
        viewModel.videoCaptureConfig = viewModel.builder1.setTargetAspectRatio(ratio)
                .setLensFacing(viewModel.lensFacing)
                .setVideoFrameRate(24)
                .setTargetRotation(Surface.ROTATION_0)
                .build();
        viewModel.videoCapture = new VideoCapture(viewModel.videoCaptureConfig);
        CameraX.bindToLifecycle(this, viewModel.preview, viewModel.videoCapture);
    }

    private void initObserve() {
        viewModel.parentPath = getPath().getPath();
        viewModel.onItemClick.observe(this, type -> {
            if (type != null) {
                if (type == 1) {
                    MusicFrameFragment frameFragment = new MusicFrameFragment();
                    frameFragment.show(getSupportFragmentManager(), frameFragment.getClass().getSimpleName());
                }
                viewModel.onItemClick.setValue(null);
            }
        });
        viewModel.onSoundSelect.observe(this, sound -> {
            if (sound != null) {
                binding.tvSoundTitle.setText(sound.getSoundTitle());
                downLoadMusic(sound.getSound());
            }
        });
        viewModel.onDurationUpdate.observe(this, duration -> binding.progressBar.enableAutoProgressView(duration));
    }

    @SuppressLint("RestrictedApi")
    private void stopRecording() {
        binding.btnCapture.clearAnimation();
        if (viewModel.audio != null) {
            viewModel.audio.pause();
        }
        viewModel.count += 1;
        binding.progressBar.pause();
        binding.progressBar.addDivider();
        viewModel.isRecording.set(false);
        viewModel.videoCapture.stopRecording();
    }

    private void initPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST);
        } else {
            recreateCamera();
        }
    }

    public void initProgressDialog() {
        mBuilder = new Dialog(this);
        mBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBuilder.setCancelable(false);
        mBuilder.setCanceledOnTouchOutside(false);
        if (mBuilder.getWindow() != null) {
            mBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mBuilder.setCancelable(false);
        mBuilder.setCanceledOnTouchOutside(false);
        progressBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dailog_progress, null, false);
        progressBinding.progressBar.setShader(new int[]{ContextCompat.getColor(this, R.color.colorTheme2), ContextCompat.getColor(this, R.color.colorTheme1), ContextCompat.getColor(this, R.color.colorTheme)});

        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        Animation reverseAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_reverse);
        progressBinding.ivParent.startAnimation(rotateAnimation);
        progressBinding.ivChild.startAnimation(reverseAnimation);
        mBuilder.setContentView(progressBinding.getRoot());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                onBackPressed();
            }
            recreateCamera();
        }

    }

    private void downLoadMusic(String endPoint) {

        PRDownloader.download(Const.ITEM_BASE_URL + endPoint, getPath().getPath(), "recordSound.aac")
                .build()
                .setOnStartOrResumeListener(() -> customDialogBuilder.showLoadingDialog())
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        customDialogBuilder.hideLoadingDialog();
                        viewModel.isStartRecording.set(true);
                        viewModel.createAudioForCamera();
                    }

                    @Override
                    public void onError(Error error) {
                        customDialogBuilder.hideLoadingDialog();
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    private void startReCording() {
        viewModel.isStartRecording.set(true);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale);
        binding.btnCapture.startAnimation(animation);
        if (binding.progressBar.getProgressPercent() != 100) {
            if (viewModel.audio != null) {
                viewModel.audio.start();
            }
            File file = new File(getPath(), "video".concat(String.valueOf(viewModel.count)).concat(".mp4"));
            viewModel.videoPaths.add(getPath().getPath().concat("/video").concat(String.valueOf(viewModel.count)).concat(".mp4"));
            viewModel.videoCapture.startRecording(file, Runnable::run, new VideoCapture.OnVideoSavedListener() {
                @Override
                public void onVideoSaved(@NonNull File file) {
                    Log.i("TAG", "onTranscodeFailed: ");
                }

                @Override
                public void onError(@NonNull VideoCapture.VideoCaptureError videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                    Log.i("TAG", "onTranscodeFailed: ");
                }
            });
            binding.progressBar.resume();
            viewModel.isRecording.set(true);
        }
    }

    public File getPath() {
        String state = Environment.getExternalStorageState();
        File filesDir;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            filesDir = getExternalFilesDir(null);
        } else {
            // Load another directory, probably local memory
            filesDir = getFilesDir();
        }
        if (filesDir != null) {
            viewModel.parentPath = filesDir.getPath();
        }
        return filesDir;
    }

    @Override
    protected void onDestroy() {
        if (viewModel.isRecording.get()) {
            stopRecording();
        }
        CameraX.unbindAll();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d("TAG", "onResume: ");
        initPermission();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (viewModel.isRecording.get()) {
            stopRecording();
        }
        CameraX.unbindAll();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (viewModel.isRecording.get()) {
            stopRecording();
        }
        CameraX.unbindAll();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        CameraX.unbindAll();
        super.onBackPressed();
    }
}

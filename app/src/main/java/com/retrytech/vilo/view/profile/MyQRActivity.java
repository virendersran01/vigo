package com.retrytech.vilo.view.profile;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ActivityMyQRBinding;
import com.retrytech.vilo.view.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MyQRActivity extends BaseActivity {

    ActivityMyQRBinding binding;
    String userid;
    private static final int MY_PERMISSIONS_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_q_r);

        if (sessionManager.getUser() != null) {
            userid = sessionManager.getUser().getData().getUserId();
            generateQRAndSet(userid);
            binding.setUser(sessionManager.getUser().getData());
        }
        initListeners();

    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        v.setDrawingCacheEnabled(true);
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    private void generateQRAndSet(String userid) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(userid, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            binding.imgQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.loutSaveCode.setOnClickListener(v -> initPermission());

    }

    private void initPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveQRForQ();
            } else {
                saveQRCode();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveQRForQ();
            } else {
                saveQRCode();
            }
        }
    }

    private void saveQRForQ() {

        OutputStream fos = null;
        Bitmap bitmap = loadBitmapFromView(binding.loutQR, binding.loutQR.getWidth(), binding.loutQR.getHeight());
        String fname = "myQR.jpg";
        ContentResolver resolver = MyQRActivity.this.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fname);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/");
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try {
            if (imageUri != null) {
                fos = resolver.openOutputStream(imageUri);
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            if (fos != null) {
                fos.flush();
                fos.close();
            }

            Toast.makeText(this, "QR Saved to phone successfully", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    private void saveQRCode() {
        Bitmap bitmap = loadBitmapFromView(binding.loutQR, binding.loutQR.getWidth(), binding.loutQR.getHeight());
        String fname = "my_bubble_qr.jpg";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/" + fname;
        File qrFile = new File(path);

        try {
            FileOutputStream stream = new FileOutputStream(qrFile);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
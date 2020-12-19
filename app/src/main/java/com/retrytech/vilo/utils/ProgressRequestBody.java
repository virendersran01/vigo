package com.retrytech.vilo.utils;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private static final int DEFAULT_BUFFER_SIZE = 2048;
    private File file;
    private UploadListener uploadListener;

    public ProgressRequestBody(File file, UploadListener uploadListener) {
        this.file = file;
        this.uploadListener = uploadListener;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("video/*");
    }

    @Override
    public long contentLength() {
        return file.length();
    }

    @Override
    public void writeTo(@NotNull BufferedSink sink) throws IOException {
        long fileLength = file.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        try (FileInputStream inputStream = new FileInputStream(file)) {
            long uploaded = 0;
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = inputStream.read(buffer)) != -1) {
                uploaded += read;
                sink.write(buffer, 0, read);
                handler.post(new ProgressUpdater(uploaded, fileLength));
            }
        }
    }

    private class ProgressUpdater implements Runnable {
        private long uploaded, fileLength;

        ProgressUpdater(long uploaded, long fileLength) {
            this.uploaded = uploaded;
            this.fileLength = fileLength;
        }

        @Override
        public void run() {
            try {
                int progress = (int) (100 * uploaded / fileLength);

                if (progress != 100) {
                    uploadListener.onProgressUpdate(progress);
                }
            } catch (ArithmeticException e) {

                e.printStackTrace();
            }
        }
    }
}

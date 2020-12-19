package com.retrytech.vilo.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.retrytech.vilo.R;
import com.retrytech.vilo.view.home.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        if (remoteMessage.getData().containsKey("message")) {
            Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("message"));
            sendNotification(remoteMessage.getData());

        }
    }

    private void sendNotification(Map<String, String> data) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.bubble_single);

        int requestID = (int) System.currentTimeMillis();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Bundle bundle = new Bundle();

        bundle.putString("pictureurl", data.get("picture_url"));
        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT
                | PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "01";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("BubbleTok")
                .setContentText(data.containsKey("message") ? data.get("message") : "Hello...!")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win))
                .setContentIntent(pendingIntent)
                .setContentInfo("Hello")
                .setLargeIcon(icon)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.bubble_single_small);

        try {
            URL url = new URL(data.get("picture_url"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            Bitmap bigPicture = BitmapFactory.decodeStream(inputStream);
            notificationBuilder.setStyle(
                    new NotificationCompat.BigPictureStyle().bigPicture(bigPicture)
            );

        } catch (IOException e) {
            e.printStackTrace();
        }


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String name = "Channel_001";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}
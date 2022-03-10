package com.fastaccess.provider.fcm;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.FastHubNotification;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.modules.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Kosh on 16 Apr 2017, 1:17 PM
 */

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class PushNotificationService extends FirebaseMessagingService {

    @Override public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        remoteMessage.getData();
        if (!remoteMessage.getData().isEmpty()) {
            Date date = new Date(remoteMessage.getSentTime());
            FastHubNotification fastHubNotification = RestProvider.gson
                    .fromJson(new JSONObject(remoteMessage.getData()).toString(), FastHubNotification.class);
            fastHubNotification.setDate(date);
            FastHubNotification.save(fastHubNotification);
        } else if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            remoteMessage.getData();
            if (!remoteMessage.getData().isEmpty()) {
                title = title == null ? remoteMessage.getData().get("title") : title;
                body = body == null ? remoteMessage.getData().get("message") : body;
            }
            Intent intent = MainActivity.launchMain(this, true);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "In App-Notifications")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(1, notificationBuilder.build());
            }
        }
    }
}

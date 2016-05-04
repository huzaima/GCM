package com.huzaima.gcmtest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

public class GcmMessageHandler extends GcmListenerService {

    public static final int MESSAGE_NOTIFICATION_ID = 435345;
    private final String LOG_TAG = GcmMessageHandler.class.getSimpleName();
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    public void onMessageReceived(final String from, final Bundle data) {
        Log.v(LOG_TAG, "Message Received: " + from + " " + data.toString());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Message Received: " + from + " " + data.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
        String message = data.getString("message");
        Log.d(LOG_TAG, "From: " + from);
        Log.d(LOG_TAG, "Message: " + message);
        createNotification(from, message);
    }

    private void createNotification(String title, String body) {

        Context context = getBaseContext();

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Title: " + title)
                .setContentText("Body: " + body)
                .setSound(soundUri);

        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pi);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }
}

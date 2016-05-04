package com.huzaima.gcmtest;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

public class GcmMessageHandler extends GcmListenerService {

    public static final int MESSAGE_NOTIFICATION_ID = 435345;
    private final String LOG_TAG = GcmMessageHandler.class.getName();

    @Override
    public void onMessageReceived(final String from, final Bundle data) {
        Log.v("MessageReceived","sdfs " + from + " " + data.toString());
        (new Handler()).post(new Runnable() {
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

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Title: " + title)
                .setContentText("Body: " + body);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }
}

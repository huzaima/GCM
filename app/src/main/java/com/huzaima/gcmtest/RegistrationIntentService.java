package com.huzaima.gcmtest;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegistrationIntentService extends IntentService {

    private static final String LOG_TAG = RegistrationIntentService.class.getSimpleName();

    public RegistrationIntentService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderID = getResources().getString(R.string.defaultSenderId);
        String token = null;
        try {
            token = instanceID.getToken(senderID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
            e.printStackTrace();
        }
        sharedPreferences.edit().putString("GCM_TOKEN", token).apply();
        if (token != null)
            registerDevice(token);

        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("GCM_TOKEN", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void registerDevice(String token) {
        URL url;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            url = new URL("http://huziama.5gbfree.com/register.php?token=" + token);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String temp;

            while ((temp = br.readLine()) != null)
                sb.append(temp);

            JSONObject rootObject = new JSONObject(sb.toString());
            boolean successful = rootObject.getBoolean("successful");

            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, successful).apply();

        } catch (IOException e) {
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
            e.printStackTrace();
        } catch (JSONException e) {
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
            e.printStackTrace();
        }


    }
}

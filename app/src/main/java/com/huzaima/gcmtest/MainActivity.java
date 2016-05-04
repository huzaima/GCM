package com.huzaima.gcmtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private boolean isReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationProgressBar.setVisibility(View.VISIBLE);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(Config.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                    String tok = sharedPreferences.getString("GCM_TOKEN","Not found");
                    Toast.makeText(getApplicationContext(),
                            "GCM Token: " + tok,
                            Toast.LENGTH_LONG).show();
                    Log.v(MainActivity.class.getName(),"GCM Token: " + tok);
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        // Registering BroadcastReceiver
        registerReceiver();

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        boolean ex = sharedPreferences.getBoolean(Config.SENT_TOKEN_TO_SERVER,false);

        Toast.makeText(getApplicationContext(),
                "GCM Token: " + ex,
                Toast.LENGTH_LONG).show();
        String t = sharedPreferences.getString("GCM_TOKEN","Not found");
        Toast.makeText(getApplicationContext(),
                "GCM Token: " + t, Toast.LENGTH_LONG).show();

        if (!ex) {
            // Start IntentService to register this application with GCM.
            Toast.makeText(getApplicationContext(),"Service starting",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        else {
            mInformationTextView.setText(getString(R.string.gcm_registered) + "\nToken: " + t);
            mRegistrationProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Config.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
}

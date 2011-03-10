package me.lhom.timeout;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class Configure extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        IntentFilter screenOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
        IntentFilter screenOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        EventReceiver mReceiver = new EventReceiver();
        registerReceiver(mReceiver, screenOn);
        registerReceiver(mReceiver, screenOff);
    }
}
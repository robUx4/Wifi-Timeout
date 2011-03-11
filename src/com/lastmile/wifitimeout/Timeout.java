package com.lastmile.wifitimeout;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Timeout extends Application {

	private static final boolean LOG = false;
	private static SharedPreferences prefs;
	protected static Timeout mApp;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mApp = this;
		
		prefs = getSharedPreferences("timeout", 0);

        IntentFilter screenOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
        IntentFilter screenOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        IntentFilter batteryChange = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        
        EventReceiver mReceiver = new EventReceiver(this);
        if (LOG) Log.d("Timeout", "register screenOn");
        registerReceiver(mReceiver, screenOn);
        if (LOG) Log.d("Timeout", "register screenOff");
        registerReceiver(mReceiver, screenOff);
        if (LOG) Log.d("Timeout", "register batteryChange");
        registerReceiver(mReceiver, batteryChange);
	}

	boolean isEnabled() {
		return prefs.getBoolean("isEnabled", true);
	}
	
	void setEnabled(boolean set) {
		Editor e = prefs.edit();
		e.putBoolean("isEnabled", set);
		e.commit();
	}
	
	int getDelayInSeconds() {
		return prefs.getInt("delay", 20);
	}
	
	void setDelayInSeconds(int delay) {
		Editor e = prefs.edit();
		e.putInt("delay", delay);
		e.commit();
	}
}

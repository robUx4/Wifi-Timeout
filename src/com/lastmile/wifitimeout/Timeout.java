package com.lastmile.wifitimeout;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Timeout extends Application {

	private static final boolean LOG = false;
	protected static Timeout mApp;
	private EventReceiver mReceiver;
	
	private static final String PREF_NAME = "timeout";
	private static final String PREF_ENABLED = "isEnabled";
	private static final String PREF_DELAY = "delay";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mApp = this;
		
        IntentFilter screenOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
        IntentFilter screenOff = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        IntentFilter batteryChange = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        
        mReceiver = new EventReceiver(this);
        if (LOG) Log.d("Timeout", "register screenOn");
        registerReceiver(mReceiver, screenOn);
        if (LOG) Log.d("Timeout", "register screenOff");
        registerReceiver(mReceiver, screenOff);
        if (LOG) Log.d("Timeout", "register batteryChange");
        registerReceiver(mReceiver, batteryChange);

        mReceiver.setEnabled(this, isEnabled());
	}

	boolean isEnabled() {
		return getSharedPreferences(PREF_NAME, 0).getBoolean(PREF_ENABLED, true);
	}
	
	void setEnabled(boolean set) {
		Editor e = getSharedPreferences(PREF_NAME, 0).edit();
		e.putBoolean(PREF_ENABLED, set);
		e.commit();

		mReceiver.setEnabled(this, set);
	}
	
	int getDelayInSeconds() {
		return getSharedPreferences(PREF_NAME, 0).getInt(PREF_DELAY, 20);
	}
	
	void setDelayInSeconds(int delay) {
		Editor e = getSharedPreferences(PREF_NAME, 0).edit();
		e.putInt(PREF_DELAY, delay);
		e.commit();
	}
}

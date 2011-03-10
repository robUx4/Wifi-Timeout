package me.lhom.timeout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

public class EventReceiver extends BroadcastReceiver {

	private static final String ACTION_TIMEOUT = "me.lhom.timeout.action.TIMEOUT";
	private static boolean isPowerPlugged = false;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("Timeout", "EventReceiver received "+intent);
		String action = intent.getAction();
		if (action!=null) {
			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				// start a timer to disable Wifi
				setPendingAlarm(context);
			}
			else if (Intent.ACTION_SCREEN_ON.equals(action)) {
				// cancel the timer to disable Wifi
				// TODO: enable back wifi only it is was enabled before we disabled it
				setPendingAlarm(context);
				setWifiState(context, true);
			}
			else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				isPowerPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)!=0;
				setPendingAlarm(context);
			}
			else if (ACTION_TIMEOUT.equals(action)) {
				setWifiState(context, false);
			}
			else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				Log.i("Timeout", "wifi changed "+intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
				//TODO: if disabled, cancel the possible alarm
			}
		}
	}

	private static void setPendingAlarm(Context context) {
		// get the screen state
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean screenIsOn = pm.isScreenOn();
		
		// get the wifi state
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		boolean wifiIsOn = wifiManager.isWifiEnabled();
		
		Log.w("Timeout", "screenIsOn:"+screenIsOn+" wifiIsOn:"+wifiIsOn+" isPlugged:"+isPowerPlugged);

		Intent intentService = new Intent(context, EventReceiver.class);
		intentService.setAction(ACTION_TIMEOUT);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentService, 0);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		if (screenIsOn || isPowerPlugged)
			alarmManager.cancel(pendingIntent);
		else {
			Time time = new Time();
			time.set(System.currentTimeMillis() + 10 * DateUtils.SECOND_IN_MILLIS); //TODO: make the 10s a parameter
			long nextStart = time.toMillis(false);
			alarmManager.set(0, nextStart, pendingIntent);
		}
	}
	
	private static void setWifiState(Context context, boolean enabled) {
		Log.e("Timeout", "We should enable WIFI "+enabled);
		
		// TODO: only disable if power not plugged and screen is off
		
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(enabled);
	}
}

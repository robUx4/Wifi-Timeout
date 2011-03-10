package me.lhom.timeout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

public class EventReceiver extends BroadcastReceiver {

	private static final String ACTION_TIMEOUT = "me.lhom.timeout.action.TIMEOUT";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("Timeout", "EventReceiver received "+intent);
		String action = intent.getAction();
		if (action!=null) {
			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				// start a timer to disable Wifi
				Intent intentService = new Intent(context, getClass());
				intentService.setAction(ACTION_TIMEOUT);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentService, 0);
				AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

				Time time = new Time();
				time.set(System.currentTimeMillis() + 10 * DateUtils.SECOND_IN_MILLIS);
				long nextStart = time.toMillis(false);
				alarmManager.set(0, nextStart, pendingIntent);
			}
			else if (Intent.ACTION_SCREEN_ON.equals(action)) {
				// cancel the timer to disable Wifi
				Intent intentService = new Intent(context, getClass());
				intentService.setAction(ACTION_TIMEOUT);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentService, 0);
				AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				alarmManager.cancel(pendingIntent);
				
				// TODO: enable back wifi only it is was enabled before we disabled it
				setWifiState(context, true);
			}
			else if (ACTION_TIMEOUT.equals(action)) {
				setWifiState(context, false);
			}
		}
	}

	
	private static void setWifiState(Context context, boolean enabled) {
		Log.w("Timeout", "We should disable WIFI here");
		
		// TODO: only disable if power not plugged and screen is off
		
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(enabled);
	}
}

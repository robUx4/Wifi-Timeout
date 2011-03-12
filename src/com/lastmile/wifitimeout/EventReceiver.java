/* This file is part of Wifi Timeout.
 *
 *   Wifi Timeout is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Wifi Timeout is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lastmile.wifitimeout;

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

	private static final String ACTION_TIMEOUT = "com.lastmile.wifitimeout.action.TIMEOUT";
	private static final String ACTION_KEEPALIVE = "com.lastmile.wifitimeout.action.KEEPALIVE";
	private static boolean isPowerPlugged = false;
	private static boolean userDisabledWifi;
	private static boolean weDisabledWifi = false;

	public EventReceiver(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		userDisabledWifi = !wifiManager.isWifiEnabled();
	}

	public EventReceiver() {
		// constructor for the system to send us events defined in the manifest
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("Timeout", "EventReceiver received "+intent);
		String action = intent.getAction();
		if (action!=null) {
			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				// start a timer to disable Wifi
				setPendingAlarm(context);
			}
			else if (Intent.ACTION_SCREEN_ON.equals(action)) {
				// cancel the timer to disable Wifi
				setPendingAlarm(context);
			}
			else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				isPowerPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)!=0;
				setPendingAlarm(context);
			}
			else if (ACTION_TIMEOUT.equals(action)) {
				setWifiState(context, false);
			}
			else if (ACTION_KEEPALIVE.equals(action)) {
				setPendingAlarm(context);
			}
			else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				Log.i("Timeout", "wifi changed "+intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)+" / "+wifiManager.isWifiEnabled()+" by us:"+weDisabledWifi);
				if (wifiManager.isWifiEnabled()) {
					userDisabledWifi = false;
					weDisabledWifi = false;
				} else {
					if (!weDisabledWifi) {
						//TODO: if disabled, cancel the possible alarm
						PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
						if (pm.isScreenOn()) {
							userDisabledWifi = true;
							weDisabledWifi = false;
						} else {
							weDisabledWifi = true; // assume we did it so we can bring it back when needed
							userDisabledWifi = false;
						}
					}
				}
			}
		}
	}

	private static void setPendingAlarm(Context context) {
		// get the screen state
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean screenIsOn = pm.isScreenOn();

		Log.d("Timeout", "screenIsOn:"+screenIsOn+" isPlugged:"+isPowerPlugged+" userDisabled:"+userDisabledWifi);

		Intent intentService = new Intent(context, EventReceiver.class);
		intentService.setAction(ACTION_TIMEOUT);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentService, 0);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		// only disable if power not plugged and screen is off
		if (screenIsOn || isPowerPlugged) {
			alarmManager.cancel(pendingIntent);
			if (!userDisabledWifi)
				setWifiState(context, true);
		} else {
			Time time = new Time();
			time.set(System.currentTimeMillis() + Timeout.mApp.getDelayInSeconds() * DateUtils.SECOND_IN_MILLIS);
			long nextStart = time.toMillis(false);
			alarmManager.set(0, nextStart, pendingIntent);
		}
	}

	private static void setWifiState(Context context, boolean enabled) {
		if (!Timeout.mApp.isEnabled())
			return;

		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled() != enabled) {
			if (!enabled)
				weDisabledWifi = true;
			Log.d("Timeout", "enable WIFI "+enabled);
			wifiManager.setWifiEnabled(enabled);
		}
	}

	void setEnabled(Context context, boolean enabled) {
		Log.d("Timeout", "enable KeepAlive "+enabled);
		Intent intentService = new Intent(context, EventReceiver.class);
		intentService.setAction(ACTION_KEEPALIVE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentService, 0);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		
		if (!enabled)
			alarmManager.cancel(pendingIntent);
		else {
			Time time = new Time();
			time.set(System.currentTimeMillis() + 20 * DateUtils.SECOND_IN_MILLIS);
			long nextStart = time.toMillis(false);
			alarmManager.setInexactRepeating(0, nextStart, 5 * DateUtils.MINUTE_IN_MILLIS, pendingIntent);
		}
	}
}

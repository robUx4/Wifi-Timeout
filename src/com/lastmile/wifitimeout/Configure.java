package com.lastmile.wifitimeout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Configure extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final TextView tv = (TextView) findViewById(R.id.textTimeoutValue);
        tv.setText(String.valueOf(Timeout.mApp.getDelayInSeconds()));
        
        SeekBar sb = (SeekBar) findViewById(R.id.seekBarTimeout);
        sb.setProgress(Timeout.mApp.getDelayInSeconds());
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				//Log.w("Timeout","new progress "+progress);
				tv.setText(String.valueOf(progress));
				Timeout.mApp.setDelayInSeconds(progress);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {}

			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
        
        CheckBox cb = (CheckBox) findViewById(R.id.checkBoxEnabled);
        cb.setChecked(Timeout.mApp.isEnabled());
        enable(Timeout.mApp.isEnabled());
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Timeout.mApp.setEnabled(isChecked);
				enable(isChecked);
			}
		});
    }
    
    private void enable(boolean enabled) {
        SeekBar sb = (SeekBar) findViewById(R.id.seekBarTimeout);
        sb.setEnabled(enabled);
        
        TextView tv = (TextView) findViewById(R.id.textTimeoutValue);
        tv.setEnabled(enabled);
        
        TextView t = (TextView) findViewById(R.id.textViewTimeout);
        t.setEnabled(enabled);
    }
}
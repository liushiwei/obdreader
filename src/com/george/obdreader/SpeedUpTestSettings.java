package com.george.obdreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

public class SpeedUpTestSettings extends Activity implements OnCheckedChangeListener, OnClickListener{
	private int mSpeed ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speed_up_test_settings);
		RadioButton button1 = (RadioButton) findViewById(R.id.radio0);
		button1.setOnCheckedChangeListener(this);
		button1 = (RadioButton) findViewById(R.id.radio1);
		button1.setOnCheckedChangeListener(this);
		button1 = (RadioButton) findViewById(R.id.radio2);
		button1.setOnCheckedChangeListener(this);
		Button test_start = (Button) findViewById(R.id.test_start);
		test_start.setOnClickListener(this);
		findViewById(R.id.home_button).setOnClickListener(this);
		findViewById(R.id.back_button).setOnClickListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
		switch(buttonView.getId()){
		case R.id.radio0:
			mSpeed = 60;
			break;
		case R.id.radio1:
			mSpeed = 80;
			break;
		case R.id.radio2:
			mSpeed = 100;
			break;
		}
		}
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.home_button:
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
			break;
		case R.id.back_button:
			finish();
			break;
		case R.id.test_start:
			Intent intent = new Intent(this,SpeedUpTest.class);
			intent.putExtra("speed", mSpeed);
			startActivity(intent);
		}
	}
	
	

}

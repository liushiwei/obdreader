package com.george.obdreader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TextView;

public class FuellingLogs extends FragmentActivity {
    private FragmentTabHost mTabHost;
    
    private void setupTabHost() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setBackgroundResource(R.drawable.vpi__tab_indicator);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_host);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setupTabHost();
        setTabs();
        
    }
    
    private void setTabs() {
    	addTab(getString(R.string.fuell_logs), R.drawable.money_icon, MaintenanceLog.class);
   }
   
   

   @Override
   public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       findViewById(R.id.action_bar).getLayoutParams().height = findViewById(android.R.id.tabs).getHeight();
       findViewById(R.id.action_bar).postInvalidate();
   }

   private void addTab(String labelId, int drawableId, Class<?> c) {
       TabHost.TabSpec spec = mTabHost.newTabSpec("tab" + labelId);

       View tabIndicator = LayoutInflater.from(this).inflate(
               R.layout.tab_indicator, mTabHost.getTabWidget(), false);
       TextView title = (TextView) tabIndicator.findViewById(R.id.tabs_text);
       title.setText(labelId);
       spec.setIndicator(tabIndicator);
       mTabHost.addTab(spec, c, null);
       //TODO: http://androil.sinaapp.com/models/141/che_xi.json
       //TODO: http://androil.sinaapp.com/models/141/che_xing_2454.json
       //TODO: http://androil.sinaapp.com/models/spec.php?cheXing=11021
       
   }
    

}

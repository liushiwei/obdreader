package com.george.obdreader.config;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TextView;

import com.george.obdreader.Log;
import com.george.obdreader.R;

public class ConfigurationActivity extends FragmentActivity {
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
        addTab(getString(R.string.base_settings), R.drawable.money_icon, BaseSetting.class);
   }
   
   

   @Override
   public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       Log.d("=====>", "TabHost height = "+findViewById(android.R.id.tabs).getHeight());
       findViewById(R.id.action_bar).getLayoutParams().height = findViewById(android.R.id.tabs).getHeight();
       findViewById(R.id.action_bar).postInvalidate();
   }

   private void addTab(String labelId, int drawableId, Class<?> c) {
       TabHost.TabSpec spec = mTabHost.newTabSpec("tab" + labelId);

       View tabIndicator = LayoutInflater.from(this).inflate(
               R.layout.tab_indicator, mTabHost.getTabWidget(), false);
       TextView title = (TextView) tabIndicator.findViewById(R.id.tabs_text);
       title.setText(labelId);
//     ImageView icon = (ImageView) tabIndicator.findViewById(R.id.tabs_icon);
//     icon.setImageResource(drawableId);
       spec.setIndicator(tabIndicator);
       mTabHost.addTab(spec, c, null);
       
       
   }
    

}

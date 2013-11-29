package com.george.obdreader;

import java.util.Calendar;
import java.util.Date;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.echo.holographlibrary.LineGraph.OnPointClickedListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MaintenanceSetting extends PreferenceFragment {

	private static final int DEFAULT_MONTHS = 0;

	 @Override  
     public void onCreate(Bundle savedInstanceState) {  
         // TODO Auto-generated method stub  
         super.onCreate(savedInstanceState);  
         addPreferencesFromResource(R.xml.preferences);  
     }  
    
    

}

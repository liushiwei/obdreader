package com.george.obdreader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MaintenanceLog extends Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d("=====>", "AppleFragment onCreateView");
        View root = inflater.inflate(R.layout.maintenance_log, container, false);
        
        return root;
    }


}

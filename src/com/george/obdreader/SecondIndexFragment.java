package com.george.obdreader;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SecondIndexFragment extends Fragment {
	private static final String TAG = "FirstIndexFragment";
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Log.d(TAG, "TestFragment-----onCreate");
	        Bundle args = getArguments();
	    }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	        Log.d(TAG, "TestFragment-----onCreateView");
	        View view = inflater.inflate(R.layout.first_index, container, false);
	       
	        return view;

	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	        Log.d(TAG, "TestFragment-----onDestroy");
	    }

}

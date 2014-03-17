package com.george.obdreader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

public class FirstIndexFragment extends Fragment {
	private static final String TAG = "FirstIndexFragment";
	private View mRoot;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "TestFragment-----onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "TestFragment-----onCreateView");
		mRoot = inflater.inflate(R.layout.first_index, container, false);
		GaugeView mGaugeView1 = (GaugeView) mRoot.findViewById(R.id.rpm_view);
		mGaugeView1.setTargetValue(0);
		MainActivity activity = (MainActivity) getActivity();
		AlphaAnimation alpha = new AlphaAnimation(0, 0);
		alpha.setDuration(0); // Make animation instant
		alpha.setFillAfter(true); // Tell it to persist after the animation ends
		mRoot.findViewById(R.id.light).startAnimation(alpha);
		mRoot.findViewById(R.id.obd).startAnimation(alpha);
		mRoot.findViewById(R.id.stopwatch).startAnimation(alpha);
		mRoot.findViewById(R.id.trouble_codes).startAnimation(alpha);
		mRoot.findViewById(R.id.maintenance).startAnimation(alpha);
		mRoot.findViewById(R.id.right_top).startAnimation(alpha);
		mRoot.findViewById(R.id.right_bottom).startAnimation(alpha);
		mRoot.findViewById(R.id.left_top).startAnimation(alpha);
		mRoot.findViewById(R.id.left_bottom).startAnimation(alpha);
		mRoot.findViewById(R.id.obd).setOnClickListener(activity);
		mRoot.findViewById(R.id.stopwatch).setOnClickListener(activity);
		mRoot.findViewById(R.id.trouble_codes).setOnClickListener(activity);
		mRoot.findViewById(R.id.maintenance).setOnClickListener(activity);
		
		return mRoot;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "TestFragment-----onDestroy");
	}

	public View getRootView() {
		return mRoot;
	}

}

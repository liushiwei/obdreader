package com.george.obdreader;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LineGraph.OnPointClickedListener;
import com.echo.holographlibrary.LinePoint;


public class AppleFragment extends Fragment implements OnClickListener {
	
	private String value = "";
	private MoneyActivity mActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("=====>", "AppleFragment onAttach");
		//value = mainActivity.getAppleData();
		mActivity = (MoneyActivity) activity;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("=====>", "AppleFragment onCreateView");
		View root = inflater.inflate(R.layout.refuel_log, container, false);
		Line l = new Line();
		LinePoint p = new LinePoint();
		p.setX(0);
		p.setY(5);
		l.addPoint(p);
		p = new LinePoint();
		p.setX(8);
		p.setY(8);
		l.addPoint(p);
		p = new LinePoint();
		p.setX(10);
		p.setY(4);
		l.addPoint(p);
		l.setColor(Color.parseColor("#FFBB33"));
		
		LineGraph li = (LineGraph)root.findViewById(R.id.linegraph);
		li.addLine(l);
		li.setRangeY(0, 10);
		li.setLineToFill(0);
		
		li.setOnPointClickedListener(new OnPointClickedListener(){

			@Override
			public void onClick(int lineIndex, int pointIndex) {
				// TODO Auto-generated method stub
				
			}
			
		});
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("=====>", "AppleFragment onActivityCreated");
//		TextView txtResult = (TextView) this.getView().findViewById(R.id.textView1);
//		txtResult.setText(value);
	}
	
	

	@Override
	public void onClick(View v) {
		
	}
	
}

package com.george.obdreader;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LineGraph.OnPointClickedListener;
import com.echo.holographlibrary.LinePoint;

public class SpeedUpTestSettings extends Activity implements OnCheckedChangeListener, OnClickListener{
	private int mSpeed ;
	private Line l;
	private LineGraph li ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speed_up_test);
		l = new Line();
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
		
		li = (LineGraph)findViewById(R.id.linegraph);
		li.addLine(l);
		li.setRangeY(0, 10);
		li.setLineToFill(0);
		li.setMaxX(60);
		li.setMinX(0);
		li.setOnPointClickedListener(new OnPointClickedListener(){

			@Override
			public void onClick(int lineIndex, int pointIndex) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		findViewById(R.id.button1).setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		l.addPoint(new LinePoint(18,15));
		li.addLine(l);
		li.showHorizontalGrid(true);
		li.postInvalidate();
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
	}

	
	
	

}

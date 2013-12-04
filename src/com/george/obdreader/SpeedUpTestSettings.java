package com.george.obdreader;

import java.util.ArrayList;
import java.util.List;

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
import com.echo.holographlibrary.ValueTitle;

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
		li.setRangeY(0, 110);
		li.setLineToFill(-1);
		li.setMaxX(60);
		li.setMinX(0);
		li.setShowTitles(true);
		List<ValueTitle> ytitles = new ArrayList<ValueTitle>();
		ytitles.add(new ValueTitle("0",0));
		ytitles.add(new ValueTitle("10",10));
		ytitles.add(new ValueTitle("20",20));
		ytitles.add(new ValueTitle("30",30));
		ytitles.add(new ValueTitle("40",40));
		ytitles.add(new ValueTitle("50",50));
		ytitles.add(new ValueTitle("60",60));
		ytitles.add(new ValueTitle("70",70));
		ytitles.add(new ValueTitle("80",80));
		ytitles.add(new ValueTitle("90",90));
		ytitles.add(new ValueTitle("100",100));
		li.setYTitles(ytitles);
		List<ValueTitle> xtitles = new ArrayList<ValueTitle>();
		xtitles.add(new ValueTitle("0",0));
		xtitles.add(new ValueTitle("5",5));
		xtitles.add(new ValueTitle("10",10));
		xtitles.add(new ValueTitle("15",15));
		xtitles.add(new ValueTitle("20",20));
		xtitles.add(new ValueTitle("25",25));
		xtitles.add(new ValueTitle("30",30));
		xtitles.add(new ValueTitle("35",35));
		xtitles.add(new ValueTitle("40",40));
		xtitles.add(new ValueTitle("45",45));
		xtitles.add(new ValueTitle("50",50));
		xtitles.add(new ValueTitle("55",55));
		xtitles.add(new ValueTitle("60",60));
		xtitles.add(new ValueTitle("65",65));
		li.setXTitles(xtitles);
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
		li.showMinAndMaxValues(true);
		li.update();
		//li.showHorizontalGrid(true);
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		
	}

	
	
	

}

package com.george.obdreader;

import java.text.NumberFormat;

import android.app.Activity;
import android.content.ContentProviderClient;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieGraph.OnSliceClickedListener;
import com.echo.holographlibrary.PieSlice;
import com.george.obdreader.db.DatabaseProvider;
import com.george.obdreader.db.FuellingLogTable;
import com.george.obdreader.db.MaintenanceLogTable;

public class Cost extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.costs);
        float maintenance_costs = getMaintenanceSumCost();
        float fuel_costs = getFuelSumCost();
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
		((TextView)findViewById(R.id.maintenance_costs)).setText(format.format(maintenance_costs));
		((TextView)findViewById(R.id.fuel_costs)).setText(format.format(fuel_costs));
		((TextView)findViewById(R.id.costs)).setText(format.format(fuel_costs+maintenance_costs));
        
        if(fuel_costs+maintenance_costs==0){
        	findViewById(R.id.nolog).setVisibility(View.VISIBLE);
        	findViewById(R.id.pieGraph).setVisibility(View.GONE);
        }else{
        	
        	PieGraph pg = (PieGraph)findViewById(R.id.pieGraph);
        	pg.setPadding(30);
        	pg.setTextPadding(10);
        	pg.setTextSize(20);
        	pg.setTextColor(Color.GRAY);;
        	PieSlice slice = new PieSlice();
        	if(maintenance_costs>0){
        		
        		slice.setColor(Color.parseColor("#99CC00"));
        		slice.setValue(maintenance_costs);
        		slice.setTitle("维修保养费");
        		pg.addSlice(slice);
        	}
        	if(fuel_costs>0){
        		slice = new PieSlice();
        		slice.setColor(Color.parseColor("#FFBB33"));
        		slice.setValue(getFuelSumCost());
        		slice.setTitle("油费");
        		pg.addSlice(slice);
        	}
        	pg.setOnSliceClickedListener(new OnSliceClickedListener() {
                
                @Override
                public void onClick(int index) {
                   if(index==0){
                	   startActivity(new Intent(Cost.this,MaintenanceActivity.class));
                   }else{
                	   startActivity(new Intent(Cost.this,FuellingLogs.class));
                   }
                    
                }
            });
        }
//        slice = new PieSlice();
//        slice.setColor(Color.parseColor("#AA66CC"));
//        slice.setValue(8);
//        slice.setTitle("Test");
//        pg.addSlice(slice);
        
        
        
//        ArrayList<Bar> points = new ArrayList<Bar>();
//        Bar d = new Bar();
//        d.setColor(Color.parseColor("#99CC00"));
//        d.setName("Test1");
//        d.setValue(10);
//        Bar d2 = new Bar();
//        d2.setColor(Color.parseColor("#FFBB33"));
//        d2.setName("Test2");
//        d2.setValue(20);
//        points.add(d);
//        points.add(d2);
//
//        BarGraph g = (BarGraph)findViewById(R.id.barGraph);
//        //g.setBarWidth(80);
//        g.setBars(points);
//        g.setUnit("￥");
    }
    
    private float getFuelSumCost(){
	    ContentProviderClient client =  getContentResolver().acquireContentProviderClient(DatabaseProvider.AUTHORITY);
	    SQLiteDatabase dbHandle= ((DatabaseProvider)client.getLocalContentProvider()).getDbHandle();
	    Cursor cursor = dbHandle.rawQuery("SELECT sum("+FuellingLogTable.COST+") FROM "+FuellingLogTable.TABLE_NAME , null);
	    cursor.moveToFirst();
	    float cnt =  cursor.getFloat(0);
	    cursor.close();
	    cursor.deactivate();
	    client.release();
	    return cnt;
	}
    
    
    private float getMaintenanceSumCost(){
	    ContentProviderClient client =  getContentResolver().acquireContentProviderClient(DatabaseProvider.AUTHORITY);
	    SQLiteDatabase dbHandle= ((DatabaseProvider)client.getLocalContentProvider()).getDbHandle();
	    Cursor cursor = dbHandle.rawQuery("SELECT sum("+MaintenanceLogTable.COST+") FROM "+MaintenanceLogTable.TABLE_NAME , null);
	    cursor.moveToFirst();
	    float cnt =  cursor.getFloat(0);
	    cursor.close();
	    cursor.deactivate();
	    client.release();
	    return cnt;
	}

    
}

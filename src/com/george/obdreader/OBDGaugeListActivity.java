package com.george.obdreader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class OBDGaugeListActivity extends Activity {

	private GridLayout mGrid;
	
	// 定义16个按钮的文本
    String[] chars = new String[] {


    "7", "8", "9", "÷", "4", "5", "6", "×", "1", "2", "3", "-", ".", "0", "=",
            "+"


    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.obd_gauge_list);
		
		mGrid = (GridLayout) findViewById(R.id.grid);
		Button button = new Button(this);
		button.setText("Test");
		mGrid.addView(button , 3);
		

        for (int i = 0; i < chars.length; i++)
        {


            Button bn = new Button(this);


            bn.setText(chars[i]);


            bn.setId(i);


            bn.setTextSize(40);


            GridLayout.Spec rowSpec = GridLayout.spec(i / 4 + 2);


            GridLayout.Spec columnSpec = GridLayout.spec(i % 4);


            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                    rowSpec, columnSpec);


            params.setGravity(Gravity.FILL);


            mGrid.addView(bn, params);
            if (bn.getId() == i)
            {


                bn.setOnClickListener(new OnClickListener()
                {


                    @Override
                    public void onClick(View v)
                    {


                        Toast.makeText(OBDGaugeListActivity.this, "按下" + v.getId(),
                                30000).show();


                    }
                });


            }


        }

	}
	
	

}

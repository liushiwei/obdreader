package com.george.obdreader.config;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.george.obdreader.Log;
import com.george.obdreader.R;
import com.george.utils.Device;
import com.george.utils.Parameters;

public class ReportActivity extends Activity implements
		OnRatingBarChangeListener {
	private String[] values;
	private String[] starts = { "☆☆☆☆☆", "★☆☆☆☆", "★★☆☆☆", "★★★☆☆", "★★★★☆",
			"★★★★★" };
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(getApplicationContext(), R.string.net_error,
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(getApplicationContext(), R.string.submit_success,
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			}

			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);
		RatingBar ui = (RatingBar) findViewById(R.id.ui);
		ui.setOnRatingBarChangeListener(this);
		ui = (RatingBar) findViewById(R.id.fun);
		ui.setOnRatingBarChangeListener(this);
		ui = (RatingBar) findViewById(R.id.bugs);
		ui.setOnRatingBarChangeListener(this);
		values = getResources().getStringArray(R.array.report_values);
		TextView textView = (TextView) findViewById(R.id.ui_text);
		textView.setText(values[2]);
		textView = (TextView) findViewById(R.id.fun_text);
		textView.setText(values[2]);
		textView = (TextView) findViewById(R.id.bugs_text);
		textView.setText(values[2]);

		Button submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new Thread() {

					@Override
					public void run() {
						Parameters params = new Parameters();
						String option = "";
						RatingBar ui = (RatingBar) findViewById(R.id.ui);
						option += "UI:" + starts[(int) ui.getRating()] + "	"
								+ values[(int) (ui.getRating() - 1)] + "\n";
						ui = (RatingBar) findViewById(R.id.fun);
						option += "FUN:" + starts[(int) ui.getRating()] + "	"
								+ values[(int) (ui.getRating() - 1)] + "\n";
						ui = (RatingBar) findViewById(R.id.bugs);
						option += "BUGS:" + starts[(int) ui.getRating()] + "	"
								+ values[(int) (ui.getRating() - 1)] + "\n";
						EditText editText = (EditText) findViewById(R.id.email);
						String email = editText.getEditableText().toString()
								.trim();
						if (email != null && email.length() > 0)
							params.add("email", email);
						editText = (EditText) findViewById(R.id.option);
						option += "内容："
								+ editText.getEditableText().toString().trim();
						params.add("description", option);
						String respone ="";
						try {
							respone = Device.openHttpClient(
									"http://obdreader.sinaapp.com/report.php",
									Device.HTTP_METHOD_POST, params);
						} catch (Exception e) {
							handler.sendEmptyMessage(0);
							e.printStackTrace();
						}
						Log.e("ReportActivity", respone);
						if(respone.equals("success"))
						handler.sendEmptyMessage(1);
						else
						handler.sendEmptyMessage(0);
						super.run();
					}

				}.start();

			}
		});
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		TextView textView;
		switch (ratingBar.getId()) {
		case R.id.ui:
			textView = (TextView) findViewById(R.id.ui_text);
			textView.setText(values[(int) (rating - 1)]);
			break;
		case R.id.fun:
			textView = (TextView) findViewById(R.id.fun_text);
			textView.setText(values[(int) (rating - 1)]);
			break;
		case R.id.bugs:
			textView = (TextView) findViewById(R.id.bugs_text);
			textView.setText(values[(int) (rating - 1)]);
			break;
		}

	}

}

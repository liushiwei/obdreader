package com.george.obdreader;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MoneyActivity extends FragmentActivity {

	private FragmentTabHost mTabHost;

	private void setupTabHost() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.tab_host);
		setContentView(R.layout.main);
		setupTabHost();
		setTabs();
		// setupTab(new TextView(this), "Tab 1");
		// setupTab(new TextView(this), "Tab 2");
		// setupTab(new TextView(this), "Tab 3");
	}

	// private void setupTab(final View view, final String tag) {
	// View tabview = createTabView(mTabHost.getContext(), tag);
	//
	// TabSpec setContent =
	// mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(new
	// TabContentFactory() {
	// public View createTabContent(String tag) {return view;}
	// });
	// mTabHost.addTab(setContent);
	//
	// }
	//
	// private static View createTabView(final Context context, final String
	// text) {
	// View view = LayoutInflater.from(context).inflate(R.layout.tab_indicator,
	// null);
	// TextView tv = (TextView) view.findViewById(R.id.tabsText);
	// tv.setText(text);
	// return view;
	// }

	private void setTabs() {
		// addTab("Home", R.drawable.money_icon, AppleFragment.class);
		// addTab("Search", R.drawable.money_icon, FacebookFragment.class);
		mTabHost.addTab(mTabHost.newTabSpec("Apple").setIndicator("Apple"),
				AppleFragment.class, null);
		// 2
		mTabHost.addTab(mTabHost.newTabSpec("Google").setIndicator("Google"),
				FacebookFragment.class, null);
	}

	private void addTab(String labelId, int drawableId, Class<?> c) {
		TabHost.TabSpec spec = mTabHost.newTabSpec("tab" + labelId);

		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.tab_indicator, mTabHost.getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.tabs_text);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.tabs_icon);
		icon.setImageResource(drawableId);
		spec.setIndicator(tabIndicator);
		mTabHost.addTab(spec, c, null);
	}

}

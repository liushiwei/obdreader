package com.george.obdreader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class SecondIndexFragment extends Fragment {
	private static final String TAG = "SecondIndexFragment";
	private List<ResolveInfo> mList ;
	private PackageManager mPackageManager;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Log.d(TAG, "TestFragment-----onCreate");
	        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	        mainIntent.addCategory("com.george.obdreader.category.APP");

	        mPackageManager = getActivity().getPackageManager();
	        mList = mPackageManager.queryIntentActivities(mainIntent, 0);
	    }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	        Log.d(TAG, "TestFragment-----onCreateView");
	        View view = inflater.inflate(R.layout.second_index, container, false);
	        GridView gridView = (GridView) view.findViewById(R.id.gridView1);
	        gridView.setAdapter(new MyAdapter());
	        return view;

	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	        Log.d(TAG, "TestFragment-----onDestroy");
	    }
	    
	    protected List<Map<String, Object>> getData(String prefix) {
	        List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

	        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	        mainIntent.addCategory(Intent.CATEGORY_SAMPLE_CODE);

	        PackageManager pm = getActivity().getPackageManager();
	        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

	        if (null == list)
	            return myData;

	        String[] prefixPath;
	        String prefixWithSlash = prefix;
	        
	        if (prefix.equals("")) {
	            prefixPath = null;
	        } else {
	            prefixPath = prefix.split("/");
	            prefixWithSlash = prefix + "/";
	        }
	        
	        int len = list.size();
	        
	        Map<String, Boolean> entries = new HashMap<String, Boolean>();

	        for (int i = 0; i < len; i++) {
	            ResolveInfo info = list.get(i);
	            CharSequence labelSeq = info.loadLabel(pm);
	            String label = labelSeq != null
	                    ? labelSeq.toString()
	                    : info.activityInfo.name;
	            
	        }

	        Collections.sort(myData, sDisplayNameComparator);
	        
	        return myData;
	    }

	    private final static Comparator<Map<String, Object>> sDisplayNameComparator =
	        new Comparator<Map<String, Object>>() {
	        private final Collator   collator = Collator.getInstance();

	        public int compare(Map<String, Object> map1, Map<String, Object> map2) {
	            return collator.compare(map1.get("title"), map2.get("title"));
	        }
	    };
	    
	    protected Intent activityIntent(String pkg, String componentName) {
	        Intent result = new Intent();
	        result.setClassName(pkg, componentName);
	        return result;
	    }
	    
	    protected void addItem(List<Map<String, Object>> data, String name, Intent intent) {
	        Map<String, Object> temp = new HashMap<String, Object>();
	        temp.put("title", name);
	        temp.put("intent", intent);
	        data.add(temp);
	    }
	    
	    class MyAdapter extends BaseAdapter  {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mList.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
	              View row = inflater.inflate(R.layout.app_item, parent, false);
	              ImageView imageView = (ImageView) row.findViewById(R.id.mycj_icon);
	              TextView tv = (TextView) row.findViewById(R.id.mycj_app_name);
                  tv.setText(mList.get(position).loadLabel(mPackageManager).toString());
                  imageView.setBackgroundResource(mList.get(position).getIconResource());
				return row;
			}

			
	    	
	    }

}

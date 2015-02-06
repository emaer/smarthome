package org.openhab.habdroid.ui;

import java.util.ArrayList;

import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.util.MyAsyncHttpClient;
import org.openhab.habdroid.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ChangeItemActivity extends Activity implements OnClickListener{

	private String siteUrl;
	private String itemId;
	private String newName;
	
	private static final String TAG = ChangeItemActivity.class.getName();
	
	private static final String PREFIX = "org.openhab.habdroid.ui.";
	
	public static final String SITE_KEY = PREFIX + "SITE_KEY";
	public static final String GROUP_KEY = PREFIX + "GROUP_KEY";
	public static final String ITEM_KEY = PREFIX + "ITEM_KEY";
	public static final String PARENT_KEY = PREFIX + "PARENT_KEY";
	
	private EditText itemname;
	private Button btndone;
	private Button btnback;
	private Spinner parent;
	private int currPos = 0;
	private String originId = "";
	
	private OpenHABWidget w;
	private ArrayList<OpenHABWidget> groupList;
	private String []groups;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.change_item_name);
		
		Intent i = getIntent();
		if(null != i){
			 Bundle data = i.getExtras();
			siteUrl = data.getString(SITE_KEY);
			w = (OpenHABWidget)data.getSerializable(ITEM_KEY);
			groupList = (ArrayList<OpenHABWidget>)data.getSerializable(GROUP_KEY);
			originId = data.getString(PARENT_KEY);
		}
		
		if(null == siteUrl || siteUrl.length() == 0 || 
				null == w )
			finish();
		
		newName = w.getLabel();
		itemId = w.getWidgetId();
		
		initUI();
		
		initGroups();
	}
	
	private void initUI(){
		
		itemname = (EditText)findViewById(R.id.itemname);
		btndone = (Button)findViewById(R.id.btndone);
		btnback = (Button)findViewById(R.id.btnback);
		parent = (Spinner)findViewById(R.id.parent);
		
		TextView tvtitle = (TextView)findViewById(R.id.tvtitle);
		tvtitle.setText(newName);
		
		itemname.setText(newName);
		btndone.setOnClickListener(this);
		btnback.setOnClickListener(this);
		
	}
	
	private void initGroups(){
		
		if(null == groupList || groupList.size() == 0){
			parent.setVisibility(View.GONE);
			return;
		}
		parent.setVisibility(View.VISIBLE);
		groups = new String[groupList.size() + 1];
		groups[0] = "please select";
		int pos = 0;
		OpenHABWidget p;
		for (int i = 0; i < groupList.size(); i++) {
			p = groupList.get(i);
			groups[i + 1] = p.getLabel();
			if(null != originId && originId.length() !=0 
					&& p.getWidgetId().equalsIgnoreCase(originId))
				pos = i + 1;
		}
		 
		ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, groups);
		//绑定 Adapter到控件
		parent.setAdapter(_Adapter);
		parent.setSelection(pos);
		
		parent.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.d(TAG, "currPos:"+ position);
				currPos = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnback:
			goBack();
			break;
		case R.id.btndone:
			modifyItemInfo();
			break;
		default:
			break;
		}
	}
	
	private void goBack(){
		finish();
	}
	
	private void modifyItemInfo(){
		
		newName = itemname.getText().toString();
		if(null == newName || newName.length() == 0){
			Toast.makeText(this, "请输入名称", Toast.LENGTH_LONG).show();
			return;
		}
		//http://localhost:8080/config?
		//url=http%3A//localhost%3A8080/rest/sitemaps/default/default
		//&name=abc%E6%B5%8B%E8%AF%95de&itemid=0000_1&type=remane
		
//		http://localhost:8080/config?
//			uri=http%3A//localhost%3A8080/rest/sitemaps/default/default
//			&iid=0000_1
//			&pid=default_0
//			&name=abc%E4%B8%AD%E6%96%87de
		SharedPreferences settings = 
				PreferenceManager.getDefaultSharedPreferences(this);
		String manualUrl = Utils.normalizeUrl(settings.getString("default_openhab_url", ""));
		String url = manualUrl + "config";
		
		MyAsyncHttpClient asyncHttpClient = new MyAsyncHttpClient();
		asyncHttpClient.setBasicAuthCredientidals(null, null);
		
		String pid = "";
		if(currPos == 0){
			pid = "";//just modify name
		}
		else{
			OpenHABWidget w = groupList.get(currPos-1);
			pid = w.getWidgetId();
		}
		
		RequestParams params = new RequestParams();
		params.put("uri", siteUrl);
		params.put("name", newName);
		params.put("iid", itemId);
		params.put("pid", pid);
		
		Log.d(TAG, "uri:" + siteUrl);
		Log.d(TAG, "name:" + newName);
		Log.d(TAG, "iid:" + itemId);
		Log.d(TAG, "pid:" + pid);
		
		asyncHttpClient.get(url, params,  new AsyncHttpResponseHandler(){

			@Override
			public void onFailure(Throwable e) {
				Log.d(TAG, "request error:" + e.getMessage());
				Toast.makeText(ChangeItemActivity.this, "error:"
				+e.getMessage(), Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(final String content) {
				// TODO Auto-generated method stub
				System.out.println("changeresult:" + content);
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(ChangeItemActivity.this, "修改成功"+content, Toast.LENGTH_LONG).show();
						finish();
					}
				}, 1500);
				
				
			}
			
		});
		
	}
	
}

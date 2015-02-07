/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2012, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */

package org.openhab.habdroid.ui;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.openhab.habdroid.R;
import org.openhab.habdroid.model.OpenHABItem;
import org.openhab.habdroid.model.OpenHABWidget;
import org.openhab.habdroid.model.OpenHABWidgetMapping;
import org.openhab.habdroid.util.MyAsyncHttpClient;
import org.openhab.habdroid.util.MySmartImageView;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.VideoView;
import at.bookworm.widget.segcontrol.SegmentedControlButton;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * This class provides openHAB widgets adapter for list view.
 * 
 * @author Victor Belov
 *
 */

public class OpenHABWidgetAdapter extends ArrayAdapter<OpenHABWidget> {
	public static final int TYPE_GENERICITEM = 0;
	public static final int TYPE_FRAME = 1;
	public static final int TYPE_GROUP = 2;
	public static final int TYPE_SWITCH = 3;
	public static final int TYPE_TEXT = 4;
	public static final int TYPE_SLIDER = 5;
	public static final int TYPE_IMAGE = 6;
	public static final int TYPE_SELECTION = 7;
	public static final int TYPE_SECTIONSWITCH = 8;
	public static final int TYPE_ROLLERSHUTTER = 9;
	public static final int TYPE_SETPOINT = 10;
	public static final int TYPE_CHART = 11;
	public static final int TYPE_VIDEO = 12;
	public static final int TYPE_WEB = 13;
	public static final int TYPES_COUNT = 14;
	private String openHABBaseUrl = "";
	private String openHABUsername;
	private String openHABPassword;
	private ArrayList<VideoView> videoWidgetList;
	private ArrayList<MySmartImageView> refreshImageList;

	public OpenHABWidgetAdapter(Context context, int resource,
			List<OpenHABWidget> objects) {
		super(context, resource, objects);
		// Initialize video view array
		videoWidgetList = new ArrayList<VideoView>();
		refreshImageList = new ArrayList<MySmartImageView>();
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	/* TODO: This definitely needs some huge refactoring
    	 */
    	RelativeLayout widgetView;
		TextView labelTextView;
		TextView valueTextView;
    	int widgetLayout = 0;
    	String[] splitString = {};
    	OpenHABWidget openHABWidget = getItem(position);
    	switch (this.getItemViewType(position)) {
    	case TYPE_FRAME:
    		widgetLayout = R.layout.openhabwidgetlist_frameitem;
    		break;
    	case TYPE_GROUP:
    		widgetLayout = R.layout.openhabwidgetlist_groupitem;
    		break;
    	case TYPE_SECTIONSWITCH:
    		widgetLayout = R.layout.openhabwidgetlist_sectionswitchitem;
    		break;
    	case TYPE_SWITCH:
    		widgetLayout = R.layout.openhabwidgetlist_switchitem;
    		break;
    	case TYPE_ROLLERSHUTTER:
    		widgetLayout = R.layout.openhabwidgetlist_rollershutteritem;
    		break;
    	case TYPE_TEXT:
    		widgetLayout = R.layout.openhabwidgetlist_textitem;
    		break;
    	case TYPE_SLIDER:
    		widgetLayout = R.layout.openhabwidgetlist_slideritem;
    		break;
    	case TYPE_IMAGE:
    		widgetLayout = R.layout.openhabwidgetlist_imageitem;
    		break;
    	case TYPE_SELECTION:
    		widgetLayout = R.layout.openhabwidgetlist_selectionitem;
    		break;
    	case TYPE_SETPOINT:
    		widgetLayout = R.layout.openhabwidgetlist_setpointitem;
    		break;
    	case TYPE_CHART:
    		widgetLayout = R.layout.openhabwidgetlist_chartitem;
    		break;
    	case TYPE_VIDEO:
    		widgetLayout = R.layout.openhabwidgetlist_videoitem;
    		break;
    	case TYPE_WEB:
    		widgetLayout = R.layout.openhabwidgetlist_webitem;
    		break;
    	default:
    		widgetLayout = R.layout.openhabwidgetlist_genericitem;
    		break;
    	}
    	if(convertView==null) {
    		widgetView = new RelativeLayout(getContext());
    		String inflater = Context.LAYOUT_INFLATER_SERVICE;
    		LayoutInflater vi;
    		vi = (LayoutInflater)getContext().getSystemService(inflater);
    		vi.inflate(widgetLayout, widgetView, true);
    	} else {
    		widgetView = (RelativeLayout) convertView;
    	}
    	switch (getItemViewType(position)) {
    	case TYPE_FRAME:
    		labelTextView = (TextView)widgetView.findViewById(R.id.framelabel);
    		if (labelTextView != null)
    			labelTextView.setText(openHABWidget.getLabel());
    		widgetView.setClickable(false);
    		if (openHABWidget.getLabel().length() > 0) { // hide empty frames
    			widgetView.setVisibility(View.VISIBLE);
    			labelTextView.setVisibility(View.VISIBLE);
    		} else {
    			widgetView.setVisibility(View.GONE);
    			labelTextView.setVisibility(View.GONE);
    		}
    		break;
    	case TYPE_GROUP:
    		labelTextView = (TextView)widgetView.findViewById(R.id.grouplabel);
    		valueTextView = (TextView)widgetView.findViewById(R.id.groupvalue);
    		if (labelTextView != null && valueTextView != null) {
    			splitString = openHABWidget.getLabel().split("\\[|\\]");
    			labelTextView.setText(splitString[0]);
    			if (splitString.length > 1) { // We have some value
    				valueTextView.setText(splitString[1]);
    			} else {
    				// This is needed to clean up cached TextViews
    				valueTextView.setText("");
    			}
    		}
    		MySmartImageView groupImage = (MySmartImageView)widgetView.findViewById(R.id.groupimage);
    		groupImage.setImageUrl(openHABBaseUrl + "images/" +
    				openHABWidget.getIcon() + ".png");
    		break;
    	case TYPE_SECTIONSWITCH:
    		labelTextView = (TextView)widgetView.findViewById(R.id.sectionswitchlabel);
    		valueTextView = (TextView)widgetView.findViewById(R.id.sectionswitchvalue);
			splitString = openHABWidget.getLabel().split("\\[|\\]");
			if (labelTextView != null)
				labelTextView.setText(splitString[0]);
			if (splitString.length > 1 && valueTextView != null) { // We have some value
				valueTextView.setText(splitString[1]);
			} else {
				// This is needed to clean up cached TextViews
				valueTextView.setText("");
			}
    		RadioGroup sectionSwitchRadioGroup = (RadioGroup)widgetView.findViewById(R.id.sectionswitchradiogroup);
    		// As we create buttons in this radio in runtime, we need to remove all
    		// exiting buttons first
    		sectionSwitchRadioGroup.removeAllViews();
    		sectionSwitchRadioGroup.setTag(openHABWidget);
    		Iterator<OpenHABWidgetMapping> sectionMappingIterator = openHABWidget.getMappings().iterator();
    		while (sectionMappingIterator.hasNext()) {
    			/* TODO: There is some problem here, because multiply buttons inside RadioGroup
    			 * can be checked at the same time. I suspect there is some problem in parent
    			 * child relationship or in inflater
    			 */ 
    			OpenHABWidgetMapping widgetMapping = sectionMappingIterator.next();
    			SegmentedControlButton segmentedControlButton = 
    					(SegmentedControlButton)LayoutInflater.from(sectionSwitchRadioGroup.getContext()).inflate(
    							R.layout.openhabwidgetlist_sectionswitchitem_button, sectionSwitchRadioGroup, false);
    			segmentedControlButton.setText(widgetMapping.getLabel());
    			segmentedControlButton.setTag(widgetMapping.getCommand());
    			if (widgetMapping.getCommand().equals(openHABWidget.getItem().getState())) {
    				segmentedControlButton.setChecked(true);
    			} else {
    				segmentedControlButton.setChecked(false);
    			}
    			sectionSwitchRadioGroup.addView(segmentedControlButton);
    		}
    		sectionSwitchRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					OpenHABWidget radioWidget = (OpenHABWidget)group.getTag();
					SegmentedControlButton selectedButton = (SegmentedControlButton)group.findViewById(checkedId);
					if (selectedButton != null) {
						Log.i("OpenHABWidgetAdapter", "Selected " + selectedButton.getText());
						Log.i("OpenHABWidgetAdapter", "Command = " + (String)selectedButton.getTag());
//						radioWidget.getItem().sendCommand((String)selectedButton.getTag());
						sendItemCommand(radioWidget.getItem(), (String)selectedButton.getTag());
					}
				}
    		});
    		MySmartImageView sectionSwitchImage = (MySmartImageView)widgetView.findViewById(R.id.sectionswitchimage);
    		sectionSwitchImage.setImageUrl(openHABBaseUrl + "images/" +
    				openHABWidget.getIcon() + ".png");
    		break;
    	case TYPE_SWITCH:
    		labelTextView = (TextView)widgetView.findViewById(R.id.switchlabel);
    		if (labelTextView != null)
    			labelTextView.setText(openHABWidget.getLabel());
    		Switch switchSwitch = (Switch)widgetView.findViewById(R.id.switchswitch);
    		if (openHABWidget.hasItem()) {
    			if (openHABWidget.getItem().getState().equals("ON")) {
    				switchSwitch.setChecked(true);
    			} else {
    				switchSwitch.setChecked(false);
    			}
    		}
    		switchSwitch.setTag(openHABWidget.getItem());
    		switchSwitch.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent motionEvent) {
					Switch switchSwitch = (Switch)v;
					OpenHABItem linkedItem = (OpenHABItem)switchSwitch.getTag();
					if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
						if (!switchSwitch.isChecked()) {
							sendItemCommand(linkedItem, "ON");
						} else {
							sendItemCommand(linkedItem, "OFF");
						}
					return false;
				}
    		});
    		MySmartImageView switchImage = (MySmartImageView)widgetView.findViewById(R.id.switchimage);
    		switchImage.setImageUrl(openHABBaseUrl + "images/" +
    				openHABWidget.getIcon() + ".png");
    		break;
    	case TYPE_ROLLERSHUTTER:
    		labelTextView = (TextView)widgetView.findViewById(R.id.rollershutterlabel);
    		if (labelTextView != null)
    			labelTextView.setText(openHABWidget.getLabel());
    		ImageButton rollershutterUpButton = (ImageButton)widgetView.findViewById(R.id.rollershutterbutton_up);
    		ImageButton rollershutterStopButton = (ImageButton)widgetView.findViewById(R.id.rollershutterbutton_stop);
    		ImageButton rollershutterDownButton = (ImageButton)widgetView.findViewById(R.id.rollershutterbutton_down);
    		rollershutterUpButton.setTag(openHABWidget.getItem());
    		rollershutterStopButton.setTag(openHABWidget.getItem());
    		rollershutterDownButton.setTag(openHABWidget.getItem());
    		rollershutterUpButton.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent motionEvent) {
					ImageButton rollershutterButton = (ImageButton)v;
					OpenHABItem rollershutterItem = (OpenHABItem)rollershutterButton.getTag();
					if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
						sendItemCommand(rollershutterItem, "UP");
					return false;
				}
    		});
    		rollershutterStopButton.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent motionEvent) {
					ImageButton rollershutterButton = (ImageButton)v;
					OpenHABItem rollershutterItem = (OpenHABItem)rollershutterButton.getTag();
					if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
						sendItemCommand(rollershutterItem, "STOP");
					return false;
				}
    		});
    		rollershutterDownButton.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent motionEvent) {
					ImageButton rollershutterButton = (ImageButton)v;
					OpenHABItem rollershutterItem = (OpenHABItem)rollershutterButton.getTag();
					if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP)
						sendItemCommand(rollershutterItem, "DOWN");
					return false;
				}
    		});
    		MySmartImageView rollershutterImage = (MySmartImageView)widgetView.findViewById(R.id.rollershutterimage);
    		rollershutterImage.setImageUrl(openHABBaseUrl + "images/" +
    				openHABWidget.getIcon() + ".png");
    		break;
    	case TYPE_TEXT:
    		labelTextView = (TextView)widgetView.findViewById(R.id.textlabel);
    		splitString = openHABWidget.getLabel().split("\\[|\\]");
    		if (labelTextView != null)
    			labelTextView.setText(splitString[0]);
    		valueTextView = (TextView)widgetView.findViewById(R.id.textvalue);
    		if (valueTextView != null) 
    			if (splitString.length > 1) {
    				// If value is not empty, show TextView
    				valueTextView.setVisibility(View.VISIBLE);
    				valueTextView.setText(splitString[1]);
    			} else {
    				// If value is empty, hide TextView to fix vertical alignment of label
    				valueTextView.setVisibility(View.GONE);
    				valueTextView.setText("");
    			}
    		MySmartImageView textImage = (MySmartImageView)widgetView.findViewById(R.id.textimage);
    		textImage.setImageUrl(openHABBaseUrl + "images/" +
    				openHABWidget.getIcon() + ".png");
    		break;
    	case TYPE_SLIDER:
    		labelTextView = (TextView)widgetView.findViewById(R.id.sliderlabel);
    		splitString = openHABWidget.getLabel().split("\\[|\\]");
    		if (labelTextView != null)
    			labelTextView.setText(splitString[0]);
    		MySmartImageView itemImage = (MySmartImageView)widgetView.findViewById(R.id.sliderimage);
    		itemImage.setImageUrl(openHABBaseUrl + "images/" +
    				openHABWidget.getIcon() + ".png");
    		SeekBar sliderSeekBar = (SeekBar)widgetView.findViewById(R.id.sliderseekbar);
    		if (openHABWidget.hasItem()) {
    			sliderSeekBar.setTag(openHABWidget.getItem());
    			int sliderState = (int)Float.parseFloat(openHABWidget.getItem().getState());
    			sliderSeekBar.setProgress(sliderState);
    			sliderSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
						}
						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							Log.i("OpenHABWidgetAdapter", "onStartTrackingTouch position = " + seekBar.getProgress());
						}
						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							Log.i("OpenHABWidgetAdapter", "onStopTrackingTouch position = " + seekBar.getProgress());
							OpenHABItem sliderItem = (OpenHABItem)seekBar.getTag();
//							sliderItem.sendCommand(String.valueOf(seekBar.getProgress()));
							sendItemCommand(sliderItem, String.valueOf(seekBar.getProgress()));
						}
    			});
    		}
    		break;
    	case TYPE_IMAGE:
    		MySmartImageView imageImage = (MySmartImageView)widgetView.findViewById(R.id.imageimage);
    		imageImage.setImageUrl(ensureAbsoluteURL(openHABBaseUrl, openHABWidget.getUrl()), false);
    		if (openHABWidget.getRefresh() > 0) {
    			imageImage.setRefreshRate(openHABWidget.getRefresh());
    			refreshImageList.add(imageImage);
    		}
   		break;
    	case TYPE_CHART:
    		MySmartImageView chartImage = (MySmartImageView)widgetView.findViewById(R.id.chartimage);
    		OpenHABItem chartItem = openHABWidget.getItem();
    		Random random = new Random();
    		String chartUrl = "";
    		if (chartItem.getType().equals("GroupItem")) {
    			chartUrl = openHABBaseUrl + "rrdchart.png?groups=" + chartItem.getName() + 
    					"&period=" + openHABWidget.getPeriod() + "&random=" +
    					String.valueOf(random.nextInt());
    		}
    		Log.i("OpenHABWidgetAdapter", "Chart url = " + chartUrl);
    		chartImage.setImageUrl(chartUrl, false);
    		// TODO: This is quite dirty fix to make charts look full screen width on all displays
    		ViewGroup.LayoutParams chartLayoutParams = chartImage.getLayoutParams();
    		int screenWidth = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    		chartLayoutParams.height = (int) (screenWidth/1.88);
    		chartImage.setLayoutParams(chartLayoutParams);
    		if (openHABWidget.getRefresh() > 0) {
    			chartImage.setRefreshRate(openHABWidget.getRefresh());
    			refreshImageList.add(chartImage);
    		}
    		Log.i("OpenHABWidgetAdapter", "chart size = " + chartLayoutParams.width + " " + chartLayoutParams.height);
    	break;
    	case TYPE_VIDEO:
    		VideoView videoVideo = (VideoView)widgetView.findViewById(R.id.videovideo);
    		Log.i("OpenHABWidgetAdapter", "Opening video at " + openHABWidget.getUrl());
    		// TODO: This is quite dirty fix to make video look maximum available size on all screens
    		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    		ViewGroup.LayoutParams videoLayoutParams = videoVideo.getLayoutParams();
    		videoLayoutParams.height = (int)(wm.getDefaultDisplay().getWidth()/1.77);
    		videoVideo.setLayoutParams(videoLayoutParams);
    		// We don't have any event handler to know if the VideoView is on the screen
    		// so we manage an array of all videos to stop them when user leaves the page
    		if (!videoWidgetList.contains(videoVideo))
    			videoWidgetList.add(videoVideo);
    		// Start video
    		if (!videoVideo.isPlaying()) {
        		videoVideo.setVideoURI(Uri.parse(openHABWidget.getUrl()));
    			videoVideo.start();
    		}
    		Log.i("OpenHABWidgetAdapter", "Video height is " + videoVideo.getHeight());
    	break;
    	case TYPE_WEB:
    		WebView webWeb = (WebView)widgetView.findViewById(R.id.webweb);
    		if (openHABWidget.getHeight() > 0) {
    			ViewGroup.LayoutParams webLayoutParams = webWeb.getLayoutParams();
    			webLayoutParams.height = openHABWidget.getHeight() * 80;
    			webWeb.setLayoutParams(webLayoutParams);
    		}
    		webWeb.setWebViewClient(new WebViewClient());
    		webWeb.loadUrl(openHABWidget.getUrl());
    	break;
    	case TYPE_SELECTION:
    		labelTextView = (TextView)widgetView.findViewById(R.id.selectionlabel);
    		if (labelTextView != null)
    			labelTextView.setText(openHABWidget.getLabel());
    		Spinner selectionSpinner = (Spinner)widgetView.findViewById(R.id.selectionspinner);
    		ArrayList<String> spinnerArray = new ArrayList<String>();
    		Iterator<OpenHABWidgetMapping> mappingIterator = openHABWidget.getMappings().iterator();
    		while (mappingIterator.hasNext()) {
    			spinnerArray.add(mappingIterator.next().getLabel());
    		}
    		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this.getContext() ,
    				android.R.layout.simple_spinner_item, spinnerArray);
    		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		selectionSpinner.setAdapter(spinnerAdapter);
    		selectionSpinner.setTag(openHABWidget);
    		selectionSpinner.setSelection((int)Float.parseFloat(openHABWidget.getItem().getState()));
    		selectionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

    			@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int index, long id) {
					Log.i("OpenHABWidgetAdapter", "Spinner item click on index " + index);
					OpenHABWidget openHABWidget = (OpenHABWidget)parent.getTag();
					if (openHABWidget != null)
						Log.i("OpenHABWidgetAdapter", "Label selected = " + openHABWidget.getMapping(index).getLabel());
					if (!openHABWidget.getItem().getState().equals(openHABWidget.getMapping(index).getCommand()))
						sendItemCommand(openHABWidget.getItem(),
								openHABWidget.getMapping(index).getCommand());
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}    			
    		});
    		MySmartImageView selectionImage = (MySmartImageView)widgetView.findViewById(R.id.selectionimage);
    		selectionImage.setImageUrl(openHABBaseUrl + "images/" +
    				openHABWidget.getIcon() + ".png");
    		break;
    	case TYPE_SETPOINT:
    		labelTextView = (TextView)widgetView.findViewById(R.id.setpointlabel);
    		splitString = openHABWidget.getLabel().split("\\[|\\]");
    		if (labelTextView != null)
    			labelTextView.setText(splitString[0]);
    		MySmartImageView setPointImage = (MySmartImageView)widgetView.findViewById(R.id.setpointimage);
    		setPointImage.setImageUrl(openHABBaseUrl + "images/" +
    				openHABWidget.getIcon() + ".png");
    		TextView setPointValueTextView = (TextView)widgetView.findViewById(R.id.setpointvaluelabel);
    		if (setPointValueTextView != null) 
    			if (splitString.length > 1) {
    				// If value is not empty, show TextView
    				setPointValueTextView.setVisibility(View.VISIBLE);
    				setPointValueTextView.setText(splitString[1]);
    			}
    		Button setPointMinusButton = (Button)widgetView.findViewById(R.id.setpointbutton_minus);
    		Button setPointPlusButton = (Button)widgetView.findViewById(R.id.setpointbutton_plus);
    		setPointMinusButton.setTag(openHABWidget);
    		setPointPlusButton.setTag(openHABWidget);
    		setPointMinusButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i("OpenHABWidgetAdapter","Minus");
					OpenHABWidget setPointWidget = (OpenHABWidget)v.getTag();
					float currentValue = Float.valueOf(setPointWidget.getItem().getState()).floatValue();
					currentValue = currentValue - setPointWidget.getStep();
					if (currentValue < setPointWidget.getMinValue())
						currentValue = setPointWidget.getMinValue();
					sendItemCommand(setPointWidget.getItem(), String.valueOf(currentValue));

				}
    		});
    		setPointPlusButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i("OpenHABWidgetAdapter","Plus");
					OpenHABWidget setPointWidget = (OpenHABWidget)v.getTag();
					float currentValue = Float.valueOf(setPointWidget.getItem().getState()).floatValue();
					currentValue = currentValue + setPointWidget.getStep();
					if (currentValue > setPointWidget.getMaxValue())
						currentValue = setPointWidget.getMaxValue();
					sendItemCommand(setPointWidget.getItem(), String.valueOf(currentValue));
				}
    		});
    		break;
    	default:
    		labelTextView = (TextView)widgetView.findViewById(R.id.itemlabel);
    		if (labelTextView != null)
    			labelTextView.setText(openHABWidget.getLabel());
    		MySmartImageView sliderImage = (MySmartImageView)widgetView.findViewById(R.id.itemimage);
    		sliderImage.setImageUrl(openHABBaseUrl + "images/" +
    				openHABWidget.getIcon() + ".png");
    		break;
    	}
    	LinearLayout dividerLayout = (LinearLayout)widgetView.findViewById(R.id.listdivider);
    	if (dividerLayout != null) {
    		if (position < this.getCount()-1) {
    			if (this.getItemViewType(position + 1) == TYPE_FRAME) {
        			dividerLayout.setVisibility(View.GONE); // hide dividers before frame widgets
    			} else {
    				dividerLayout.setVisibility(View.VISIBLE); // show dividers for all others
    			}
    		} else { // last widget in the list, hide divider
    			dividerLayout.setVisibility(View.GONE);
    		}
    	}
    	return widgetView;
    }
    
    @Override
    public int getViewTypeCount() {
        return TYPES_COUNT;
    }
    
    @Override
    public int getItemViewType(int position) {
    	OpenHABWidget openHABWidget = getItem(position);
    	if (openHABWidget.getType().equals("Frame")) {
    		return TYPE_FRAME;
    	} else if (openHABWidget.getType().equals("Group")) {
    		return TYPE_GROUP;
    	} else if (openHABWidget.getType().equals("Switch")) {
    		if (openHABWidget.hasMappings()) {
    			return TYPE_SECTIONSWITCH;
    		} else if (openHABWidget.getItem().getType().equals("RollershutterItem")) {
    			return TYPE_ROLLERSHUTTER;
    		} else {
    			return TYPE_SWITCH;
    		}
    	} else if (openHABWidget.getType().equals("Text")) {
    		return TYPE_TEXT;
    	} else if (openHABWidget.getType().equals("Slider")) {
    		return TYPE_SLIDER;
    	} else if (openHABWidget.getType().equals("Image")) {
    		return TYPE_IMAGE;
    	} else if (openHABWidget.getType().equals("Selection")) {
    		return TYPE_SELECTION;
    	} else if (openHABWidget.getType().equals("Setpoint")) {
    		return TYPE_SETPOINT;
    	} else if (openHABWidget.getType().equals("Chart")) {
    		return TYPE_CHART;
    	} else if (openHABWidget.getType().equals("Video")) {
    		return TYPE_VIDEO;
    	} else if (openHABWidget.getType().equals("Webview")) {
    		return TYPE_WEB;
    	} else {
    		return TYPE_GENERICITEM;
    	}
    }
	
    public void setOpenHABBaseUrl(String baseUrl) {
    	openHABBaseUrl = baseUrl;
    }
    
    private String ensureAbsoluteURL(String base, String maybeRelative) {
        if (maybeRelative.startsWith("http")) {
            return maybeRelative;
        } else {
            try {
               return new URL(new URL(base), maybeRelative).toExternalForm();
            } catch (MalformedURLException e) {
               return "";
            }
        }
    }
    
    private void sendItemCommand(OpenHABItem item, String command) {
		MyAsyncHttpClient asyncHttpClient = new MyAsyncHttpClient();
		asyncHttpClient.setBasicAuth(openHABUsername, openHABPassword);
		try {
			StringEntity se = new StringEntity(command);
			asyncHttpClient.post(null, item.getLink(), se, "text/plain", new AsyncHttpResponseHandler(){

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2,
						Throwable arg3) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					// TODO Auto-generated method stub
					
				}});
		} catch (UnsupportedEncodingException e) {
		}
    }

	public String getOpenHABUsername() {
		return openHABUsername;
	}

	public void setOpenHABUsername(String openHABUsername) {
		this.openHABUsername = openHABUsername;
	}

	public String getOpenHABPassword() {
		return openHABPassword;
	}

	public void setOpenHABPassword(String openHABPassword) {
		this.openHABPassword = openHABPassword;
	}
	
	public void stopVideoWidgets() {
		for (int i=0; i<videoWidgetList.size(); i++) {
			if (videoWidgetList.get(i) != null)
				videoWidgetList.get(i).stopPlayback();
		}
		videoWidgetList.clear();
	}
	
	public void stopImageRefresh() {
		for (int i=0; i<refreshImageList.size(); i++) {
			if (refreshImageList.get(i) != null)
				refreshImageList.get(i).cancelRefresh();
		}
	}
}

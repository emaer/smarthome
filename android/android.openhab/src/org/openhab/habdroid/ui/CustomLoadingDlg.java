package org.openhab.habdroid.ui;

import org.openhab.habdroid.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomLoadingDlg extends Dialog {

	private static AnimationDrawable animDrawable = null;
	private static ImageView animV = null;
	private Runnable executingRunable = null;
	private static String myText = null;
	private static TextView loadingTV = null;

	public CustomLoadingDlg(Context context) {
		super(context);

	}

	public CustomLoadingDlg(Context context, int theme) {
		super(context, theme);

	}
	
	public void setMyText(String str){
		
		if(null != str && str.length() != 0){
			
			myText = str;
			
		}
		
		if(null != loadingTV)
			loadingTV.setText(myText);
		
	}

	public void startAnim() {

		if (null != animDrawable) {

			if (!animDrawable.isRunning()) {

				executingRunable = new Runnable() {

					@Override
					public void run() {

						animDrawable.start();

					}
				};

				animV.post(executingRunable);

			}

		}

	}

	public void stopAnim() {

		if (null != animDrawable) {

			if (animDrawable.isRunning()) {

				animDrawable.stop();

				animV.removeCallbacks(executingRunable);

			}

		}

	}

	public static class Builder {

		private Context context;
		private String text;

		public Builder(Context context) {

			this.context = context;

		}

		public Builder setText(String text) {

			this.text = text;
			myText = this.text;
			return this;

		}

		public Builder setText(int textResId) {

			this.text = context.getText(textResId).toString();
			myText = this.text;
			return this;

		}

		public CustomLoadingDlg create() {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			final CustomLoadingDlg dlg = new CustomLoadingDlg(context,
					R.style.LoadingDialog);
			View layout = inflater.inflate(R.layout.my_loading_dialog, null);

			animV = (ImageView) layout.findViewById(R.id.my_loading_dlg_anim);
			animV.setBackgroundResource(R.anim.logo_run);
			animDrawable = (AnimationDrawable) animV.getBackground();

			TextView textTV = (TextView) layout
					.findViewById(R.id.my_loading_dlg_text);
			textTV.setText(text);
			
			loadingTV = textTV;
			myText = text;

			dlg.setContentView(layout);

			return dlg;

		}

	}

}

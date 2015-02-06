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

import org.openhab.habdroid.R;
import org.openhab.habdroid.model.Constants;
import org.openhab.habdroid.util.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * This is a class to provide preferences activity for application.
 * 
 * @author Victor Belov
 *
 */

public class OpenHABPreferencesActivity extends PreferenceActivity {
	
	private static final String TAG = OpenHABPreferencesActivity.class.getName();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.preferences);
	    Preference roomPreference = getPreferenceScreen().findPreference("room_setting");
	    
	    roomPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				
				Intent listActivityIntent = new Intent(
						OpenHABPreferencesActivity.this.getApplicationContext(), 
						OpenHABRoomSettingActivity.class);
				
				Resources res = getResources();
				final String def = res.getString(R.string.settings_default_openhab_url_value);
				SharedPreferences shp = PreferenceManager.
						getDefaultSharedPreferences(OpenHABPreferencesActivity.this);
				String openhab_url = shp.getString("default_openhab_url", def);
				openhab_url = Utils.normalizeUrl(openhab_url);
				Log.d(TAG, "openhab_url:" + openhab_url);
				
				listActivityIntent.putExtra("baseURL", openhab_url);	
				startActivity(listActivityIntent);
				
				return true;
			}
		});
	}
	
	
}

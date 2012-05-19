package com.online.nutrition.dietdiary.preferences;



import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.online.nutrition.dietdiary.R;

/**
 * @author Aare Puussaar (aare.puussaar#gmail.com)
 */
public class PreferencesActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {

	//Holds setting information
    public static String KEY_FIRST_RUN = "firstRun";
    public static String KEY_EMAIL = "email";
    public static String KEY_PASSWORD = "password";
	public static String KEY_USER_ID ="userid";
	public static String KEY_USER_NOTIFIED = "notified";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
            this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		
	}

	
}

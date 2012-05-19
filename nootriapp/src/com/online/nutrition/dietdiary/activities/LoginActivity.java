package com.online.nutrition.dietdiary.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.online.nutrition.dietdiary.R;
import com.online.nutrition.dietdiary.preferences.PreferencesActivity;
import com.online.nutrition.dietdiary.server.HTTPRequestPoster;
import com.online.nutrition.dietdiary.server.ServerHelper;
import com.online.nutrition.dietdiary.service.NetworkStatusProvider;


/**
 * Login activity.
 * Displays login view and handles the login activities.
 * 
 * @author Aare Puussaar (aare.puussaar#gmail.com)
 * 
 */
public class LoginActivity extends Activity {
	private static final String t = "LoginActivity";
	
	private SharedPreferences mSharedPreferences;
	private Editor editor;
	
	// buttons
    private Button mSignInButton;
    private Button mCreateAccountButton;
    
    //fields
    private TextView mUsername;
    private TextView mPassword;
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		
		SharedPreferences.Editor editor = mSharedPreferences.edit();

		// Save the edits
		editor.commit();

	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	};

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}
	
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Log.i(t, "Starting up, displaing login view");
        
        //set view
        setContentView(R.layout.login);
        
        // get the shared preferences object
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = mSharedPreferences.edit();
        
        //load fields
        mUsername = (TextView) findViewById(R.id.login_username_textbox);
        mPassword = (TextView) findViewById(R.id.login_password_textbox);
        
        // login button. expects a result.
        mSignInButton = (Button) findViewById(R.id.login_button_sign);
        mSignInButton.setText(getString(R.string.login_button_sign_text));
        mSignInButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	//TODO: implement loader
            	if (NetworkStatusProvider.isConnected(getApplicationContext())){
            		String response = HTTPRequestPoster.sendPostRequest(ServerHelper.URL_TAG,ServerHelper.urlRequestParameters(ServerHelper.LOGIN_METHOD, new String[]{mUsername.getText().toString(),mPassword.getText().toString()}));
            		if(Integer.valueOf(response) != 0 && !response.equalsIgnoreCase(ServerHelper.SERVER_RESPONSE_ERROR)) {
            			//Log.d("RESONSE", response);
            			//remember user id and set firstrun false
            			editor.putString(PreferencesActivity.KEY_USER_ID, response);
            			editor.putBoolean(PreferencesActivity.KEY_FIRST_RUN, false);
            			editor.commit();
            			Intent i = new Intent(getApplicationContext(), DietDiaryActivity.class);
            			startActivity(i);
            			finish();
				} else {
					Toast.makeText(LoginActivity.this, getString(R.string.login_error), Toast.LENGTH_LONG).show();
				}
            	} else {
            		Toast.makeText(LoginActivity.this, getString(R.string.service_error), Toast.LENGTH_LONG).show();
            	}
                
            }
        });
        
        // create account button. expects a result.
        mCreateAccountButton = (Button) findViewById(R.id.login_button_create);
        mCreateAccountButton.setText(getString(R.string.login_button_create_text));
        mCreateAccountButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(i);
            }
        });
    }
}

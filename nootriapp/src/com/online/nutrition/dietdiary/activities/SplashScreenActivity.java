
package com.online.nutrition.dietdiary.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;

import com.online.nutrition.dietdiary.R;
import com.online.nutrition.dietdiary.application.Diary;
import com.online.nutrition.dietdiary.preferences.PreferencesActivity;

/**
 * Splash Screen activity.
 * Displays splash screen on first run.
 * 
 * @author Aare Puussaar (aare.puussaar#gmail.com)
 * 
 */
public class SplashScreenActivity extends Activity {

	// log tag
	private static final String t = "SplashScreenActivity";
    private int mSplashTimeout = 2000; // milliseconds

    private AlertDialog mAlertDialog;
    private static final boolean EXIT = true;


    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();

		SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this splash screen should be a blank slate
        //TODO: make splash screen view
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);
        // must check if the directories are not deleted
		//Log.i(t, "Starting up, creating directories");
		try {
			Diary.createDDDirs();
		} catch (RuntimeException e) {
			createErrorDialog(e.getMessage(), EXIT);
			return;
		}


        

        // get the shared preferences object
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        //get first run preference
        boolean firstRun = mSharedPreferences.getBoolean(PreferencesActivity.KEY_FIRST_RUN, true);
        
        //set notification service parameter to false(user cannot be notified yet)
        Editor editor = mSharedPreferences.edit();
        editor.putBoolean(PreferencesActivity.KEY_USER_NOTIFIED, false);
        editor.commit();
        // check for first run
        if (firstRun) {
            startSplashScreen();
        } else {
        	startActivity(new Intent(SplashScreenActivity.this, DietDiaryActivity.class));
        	finish();
        }
        

    }


    private void endSplashScreen() {
		// launch new activity and close splash screen
        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        finish();
    }


    private void startSplashScreen() {

        // create a thread that counts up to the timeout
        Thread t = new Thread() {
            int count = 0;


            @Override
            public void run() {
                try {
                    super.run();
                    while (count < mSplashTimeout) {
                        sleep(100);
                        count += 100;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endSplashScreen();
                }
            }
        };
        t.start();
    }


    private void createErrorDialog(String errorMsg, final boolean shouldExit) {
        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setIcon(android.R.drawable.ic_dialog_info);
        mAlertDialog.setMessage(errorMsg);
        DialogInterface.OnClickListener errorListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case DialogInterface.BUTTON1:
                        if (shouldExit) {
                            finish();
                        }
                        break;
                }
            }
        };
        mAlertDialog.setCancelable(false);
        mAlertDialog.setButton(getString(R.string.ok), errorListener);
        mAlertDialog.show();
    }

}

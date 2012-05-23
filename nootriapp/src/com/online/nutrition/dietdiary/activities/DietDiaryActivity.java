package com.online.nutrition.dietdiary.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.online.nutrition.dietdiary.R;
import com.online.nutrition.dietdiary.preferences.PreferencesActivity;
import com.online.nutrition.dietdiary.server.HTTPRequestPoster;
import com.online.nutrition.dietdiary.server.ServerHelper;
import com.online.nutrition.dietdiary.server.Uploader;
import com.online.nutrition.dietdiary.service.NetworkStatusProvider;

/**
 * Responsible for displaying buttons to launch the major activities.
 * 
 * @author Aare Puussaar (aare.puussar#gmail.com)
 */
public class DietDiaryActivity extends Activity {

	// log tag
	private static final String t = "DietDiaryActivity";

	// alert dialog
	private AlertDialog mAlertDialog;
	private static final boolean EXIT = true;

	// preferences parameters
	private SharedPreferences mSharedPreferences;
	private Editor editor;

	// user option
	private String userId;

	// buttons
	private Button mTakePictureButton;
	private Button mReviewHistoryButton;
	private Button mReviewStatsButton;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		// Switch for handling menu items
		switch (item.getItemId()) {

		case R.id.log_out:
			// get the shared preferences object
			mSharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			editor = mSharedPreferences.edit();
			editor.putBoolean(PreferencesActivity.KEY_FIRST_RUN, true);
			editor.commit();
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(i);
			finish();
			return true;

			// default case
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();

		SharedPreferences mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = mSharedPreferences.edit();

		// Save the edits
		editor.commit();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	/**
	 * 
	 * Called when the activity is first created.
	 * 
	 * */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		

		// TODO: sync implementation and upload runnable
		if (NetworkStatusProvider.isConnected(getApplicationContext())) {
			// get userId
			mSharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			userId = mSharedPreferences.getString(
					PreferencesActivity.KEY_USER_ID, "");
			// TODO o=n
			String notificationText = HTTPRequestPoster.sendPostRequest(
					ServerHelper.URL_TAG, "o=n");
			// //Log.d(t, notificationText);
			try {
				if (Integer.parseInt(notificationText) != 0) {
					// Response - notificationText is String or int (0), if 0
					// then do nothing,
					// else parseInt throws an exception because of text input->
					// code in catch block
				}
			} catch (Exception e) {
				//Log.d(t, "EXCEPTION: "+e.toString());
				// Start notification service implementation, step 1
				String ns = Context.NOTIFICATION_SERVICE;
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
				// notification step 2
				int icon = R.drawable.icon;
				CharSequence tickerText = notificationText;
				long when = System.currentTimeMillis();
				Notification notification = new Notification(icon, tickerText,
						when);
				// notification step 3
				Context context = getApplicationContext();
				CharSequence contentTitle = getString(R.string.app_name);
				CharSequence contentText = notificationText;
				Intent notificationIntent = new Intent(this, DietDiaryActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(
						this, 0, notificationIntent, notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL);

				notification.setLatestEventInfo(context, contentTitle,
						contentText, contentIntent);

				final int NOTIFY_ID = 1;
				
				if(!mSharedPreferences.getBoolean(PreferencesActivity.KEY_USER_NOTIFIED, false)){					
					editor = mSharedPreferences.edit();
					editor.putBoolean(PreferencesActivity.KEY_USER_NOTIFIED, true);
					editor.commit();						
					mNotificationManager.notify(NOTIFY_ID, notification);
				}
										
			}

			// Start upload
			Uploader up = new Uploader(userId);
			up.startUpload();

			// Start synchronizer
			// Synchronizer starts when Uploader is finished (called from
			// Uploader class)

		}

		// set view
		setContentView(R.layout.main);

		// enter picture taking mode button. expects a result.
		mTakePictureButton = (Button) findViewById(R.id.take_picture);
		mTakePictureButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						CameraActivity.class);
				startActivity(i);
			}
		});

		// enter history button. expects a result.
		mReviewHistoryButton = (Button) findViewById(R.id.review_history);
		mReviewHistoryButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent i = new Intent(getApplicationContext(),
						HistoryActivity2.class);
				startActivity(i);

			}
		});
		// enter stats button. expects a result.
		mReviewStatsButton = (Button) findViewById(R.id.review_stats);
		mReviewStatsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						StatsActivity.class);
				startActivity(i);

			}
		});
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
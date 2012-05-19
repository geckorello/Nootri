package com.online.nutrition.dietdiary.activities;

import java.io.File;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.online.nutrition.dietdiary.R;
import com.online.nutrition.dietdiary.application.Diary;
import com.online.nutrition.dietdiary.data.DataHelper;
import com.online.nutrition.dietdiary.preferences.PreferencesActivity;
import com.online.nutrition.dietdiary.server.Uploader;
import com.online.nutrition.dietdiary.service.NetworkStatusProvider;

public class ImageActivity extends Activity {

	//log tag
	private static final String t = "ImageActivity";
	
	// view elements
	private LinearLayout imageLayout;

	// buttons
	private ImageButton mUploadButton;
	private ImageButton mRetakeButton;
	
	// additional info
	private RadioButton mCheckBox1;
	private RadioButton mCheckBox2;
	private RadioButton mCheckBox3;
	private RadioButton mCheckBox4;
	private RadioButton mCheckBox5;
	private EditText	mComment;

	
	private String imageTime;
	private SharedPreferences mSharedPreferences;
	// user option
	private String userId;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set view
		setContentView(R.layout.image);
		
		// get the shared preferences object
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		// set just taken picture to background
		imageLayout = (LinearLayout) findViewById(R.id.imageoverlay);
		Drawable d = Drawable.createFromPath(Diary.TMPFILE_PATH);

		imageLayout.setBackgroundDrawable(d);

		// Read additional information
		mCheckBox1 = (RadioButton) findViewById(R.id.checkBox1);
		mCheckBox2 = (RadioButton) findViewById(R.id.checkBox2);
		mCheckBox3 = (RadioButton) findViewById(R.id.checkBox3);
		mCheckBox4 = (RadioButton) findViewById(R.id.checkBox4);
		mCheckBox5 = (RadioButton) findViewById(R.id.checkBox5);
		mComment = (EditText) findViewById(R.id.comment);

		
		Bundle extras = getIntent().getExtras();
		imageTime = extras.getString("time");

		mUploadButton = (ImageButton) findViewById(R.id.upload_button);
		mUploadButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// get the tempfile
				File fi = new File(Diary.TMPFILE_PATH);

				String mImageFolder = Diary.IMAGE_PATH;
				String s = mImageFolder + "/" + imageTime
						+ ".jpg";

				File nf = new File(s);
				if (!fi.renameTo(nf)) {
					//Log.e(t, "Failed to rename " + fi.getAbsolutePath());
				} else {
					//Log.i(t,"renamed " + fi.getAbsolutePath() + " to "	+ nf.getAbsolutePath());
				}
				// write to database
				DataHelper data = new DataHelper();
				ContentValues values = new ContentValues();
				values.put("comment",mComment.getText().toString());
				values.put("cb1", (mCheckBox1.isChecked()) ? 1 : 0);
				values.put("cb2", (mCheckBox2.isChecked()) ? 1 : 0);
				values.put("cb3", (mCheckBox3.isChecked()) ? 1 : 0);
				values.put("cb4", (mCheckBox4.isChecked()) ? 1 : 0);
				values.put("cb5", (mCheckBox5.isChecked()) ? 1 : 0);
				values.put("timestamp", imageTime);
				values.put("uploaded", 0);
				values.put("deleted", 0);
				//Log.i("values", values.toString());
				data.insert(values);
				data.closeConnection();
				// check if network is available
				if (NetworkStatusProvider
						.isConnected(getApplicationContext())) {
					// get userId
					userId = mSharedPreferences.getString(
							PreferencesActivity.KEY_USER_ID, "");
					// Start upload
					Uploader up = new Uploader(userId);
					up.startUpload();
				}
				// notify user
				Toast.makeText(ImageActivity.this,
						getString(R.string.upload_prompt),
						Toast.LENGTH_LONG).show();
				// close view
				finish();

			}
		});
		mRetakeButton = (ImageButton) findViewById(R.id.retake_button);
		mRetakeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), CameraActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(i);
				// close view
				finish();
			}	
		});
		
	}

}
package com.online.nutrition.dietdiary.activities;

import java.io.File;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
	private Button mUploadButton;
	private Button mRetakeButton;
	
	//radio uncheck hack
	boolean hack1 = false;
	boolean hack2 = false;
	
	// additional info
	private RadioGroup mRadioGroup1;
	private RadioGroup mRadioGroup2;
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
		mRadioGroup1 = (RadioGroup) findViewById(R.id.rg1);
		mRadioGroup2 = (RadioGroup) findViewById(R.id.rg2);
		
		mCheckBox1 = (RadioButton) findViewById(R.id.checkBox1);
		mCheckBox2 = (RadioButton) findViewById(R.id.checkBox2);
		mCheckBox3 = (RadioButton) findViewById(R.id.checkBox3);
		mCheckBox4 = (RadioButton) findViewById(R.id.checkBox4);
		mCheckBox5 = (RadioButton) findViewById(R.id.checkBox5);
		mComment = (EditText) findViewById(R.id.comment);
		
		OnClickListener radioClickListener1 = new OnClickListener()
	    {

	        public void onClick(View v)
	        {
	            if (v.getId() == mRadioGroup1.getCheckedRadioButtonId() && hack1)
	            {
	            	mRadioGroup1.clearCheck();
	            }
	            else
	            {
	                hack1 = true;
	            }
	        }
	    };

	    OnCheckedChangeListener radioCheckChangeListener1 = new OnCheckedChangeListener()
	    {

	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	        {
	            hack1 = false;
	        }
	    };
	    
	    OnClickListener radioClickListener2 = new OnClickListener()
	    {

	        public void onClick(View v)
	        {
	            if (v.getId() == mRadioGroup2.getCheckedRadioButtonId() && hack2)
	            {
	            	mRadioGroup2.clearCheck();
	            }
	            else
	            {
	                hack2 = true;
	            }
	        }
	    };

	    OnCheckedChangeListener radioCheckChangeListener2 = new OnCheckedChangeListener()
	    {

	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	        {
	            hack2 = false;
	        }
	    };

	    mCheckBox1.setOnCheckedChangeListener(radioCheckChangeListener1);
	    mCheckBox2.setOnCheckedChangeListener(radioCheckChangeListener1);
	    mCheckBox3.setOnCheckedChangeListener(radioCheckChangeListener1);

	    mCheckBox1.setOnClickListener(radioClickListener1);
	    mCheckBox2.setOnClickListener(radioClickListener1);
	    mCheckBox3.setOnClickListener(radioClickListener1);
	    
	    mCheckBox4.setOnCheckedChangeListener(radioCheckChangeListener2);
	    mCheckBox5.setOnCheckedChangeListener(radioCheckChangeListener2);
	    
	    mCheckBox4.setOnClickListener(radioClickListener2);
	    mCheckBox5.setOnClickListener(radioClickListener2);
		
		Bundle extras = getIntent().getExtras();
		imageTime = extras.getString("time");
		
		mUploadButton = (Button) findViewById(R.id.upload_button);
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
				Intent i = new Intent(getApplicationContext(),
						CameraActivity.class);
				startActivity(i);
				// close view
				finish();

			}
		});
		mRetakeButton = (Button) findViewById(R.id.retake_button);
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
package com.online.nutrition.dietdiary.activities;

import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
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
 * Responsible for displaying sign-up form.
 * 
 * @author Mihkel Vunk (mihkel.vunk#gmail.com)
 */

public class SignupActivity extends Activity {
	private static final String t = "SignupActivity";

	private SharedPreferences mSharedPreferences;
	private Editor editor;

	// textviews
	private TextView mEmail;
	private TextView mPassword;
	private TextView mRepeatPassword;

	// signup button
	private Button mSignUpButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Log.i(t, "Displaying signup view.");
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.signup);

		// get the shared preferences object
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		editor = mSharedPreferences.edit();

		// get textboxes
		mEmail = (TextView) findViewById(R.id.signup_username_textbox);
		mPassword = (TextView) findViewById(R.id.signup_password1_textbox);
		mRepeatPassword = (TextView) findViewById(R.id.signup_password2_textbox);

		mSignUpButton = (Button) findViewById(R.id.signup_button_submit);

		mSignUpButton.setText(getString(R.string.signup_button_submit));
		mSignUpButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String passWord = mPassword.getText().toString();
				if (!NetworkStatusProvider.isConnected(getApplicationContext())) {
					//Log.d("Network", "Connection unavailable");
					Toast.makeText(SignupActivity.this,
							getString(R.string.service_error),
							Toast.LENGTH_LONG).show();
				} else {
					// TODO: don't now what the response is when server has
					// crashed
					if (!checkEmail(mEmail.getText().toString())) {
						Toast.makeText(SignupActivity.this,
								R.string.signup_invalid_mail, Toast.LENGTH_LONG)
								.show();
						//Log.d("Values", "Invalid e-mail");
					} else if (TextUtils.isEmpty(passWord)
							|| passWord.length() < 6) {
						Toast.makeText(SignupActivity.this,
								R.string.signup_password_length,
								Toast.LENGTH_LONG).show();
						//Log.d("Values", "Invalid password");
					} else if (!mPassword.getText().toString()
							.equals(mRepeatPassword.getText().toString())) {
						// TODO: change box border color to read for better
						// distinction.

						mPassword.setText("");
						mRepeatPassword.setText("");
						Toast.makeText(SignupActivity.this,
								getString(R.string.password_mismatch),
								Toast.LENGTH_LONG).show();
						//Log.d("Values", "Password mismatch");
					} else {
						String response = HTTPRequestPoster.sendPostRequest(
								ServerHelper.URL_TAG, ServerHelper
										.urlRequestParameters(
												ServerHelper.REGISTER_METHOD,
												new String[] {
														mEmail.getText()
																.toString(),
														mPassword.getText()
																.toString() }));
						//Log.d("REGISTER", response);
						if (Integer.parseInt(response) != 0
								&& !response
										.equalsIgnoreCase(ServerHelper.SERVER_RESPONSE_ERROR)
								&& !response
										.equalsIgnoreCase(ServerHelper.SERVER_RESPONSE_USERNAME_ERROR)) {
							// remember user id and set firstrun false
							/*
							editor.putString(PreferencesActivity.KEY_USER_ID,
									response);
							editor.putBoolean(
									PreferencesActivity.KEY_FIRST_RUN, false);
							editor.commit();
							// Login
							Intent i = new Intent(getApplicationContext(),
									DietDiaryActivity.class);
							startActivity(i);*/
							//goto login
							finish();
							Toast.makeText(SignupActivity.this,
									getString(R.string.signup_success),
									Toast.LENGTH_LONG).show();
						} else if (response
								.equalsIgnoreCase(ServerHelper.SERVER_RESPONSE_USERNAME_ERROR)) {
							Toast.makeText(SignupActivity.this,
									getString(R.string.username_error),
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(SignupActivity.this,
									getString(R.string.service_error),
									Toast.LENGTH_LONG).show();
						}
					}
				}

			}
		});

	}

	/**
	 * For e-mail validation. Can be found at:
	 * http://stackoverflow.com/questions/4342408/email-validation-android
	 */
	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	/**
	 * Checks email
	 * 
	 * @param email - Email address
	 * @return
	 */
	private boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

}

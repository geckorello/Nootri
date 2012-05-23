/**
 * 
 */
package com.online.nutrition.dietdiary.activities;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.online.nutrition.dietdiary.R;
import com.online.nutrition.dietdiary.application.Diary;
import com.online.nutrition.dietdiary.data.DataHelper;
import com.online.nutrition.dietdiary.image.ImageManager;
import com.online.nutrition.dietdiary.preferences.PreferencesActivity;
import com.online.nutrition.dietdiary.server.Synchronizer;
import com.online.nutrition.dietdiary.service.NetworkStatusProvider;

/**
 * 
 * Class for displaying images in list
 * 
 * @author aare
 * 
 */
public class HistoryActivity2 extends ListActivity {

	private final String PATH = Diary.IMAGE_PATH + "/";
	private List<String> imageNames;
	private SimpleAdapter adapter;
	private SharedPreferences mSharedPreferences;
	private String userId;
	private List<Map<String, Object>> resourceObject;
	private List<String> breakfast;
	private List<String> lunch;
	private List<String> dinner;
	private List<String> snack;
	private List<String> drink;
	
	private Format formatter;
	private Date date;
	private String day;
	private String month;
	private String time;
	private String lastDate = "date";

	// log tag
	private static final String t = "HistoryActivity";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();

		SharedPreferences settings = getPreferences(MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = settings.edit();

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gallery);

		// get the shared preferences object
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		// define the list which holds the information of the list
		resourceObject = new ArrayList<Map<String, Object>>();

		// define the map which will hold the information for each row
		Map<String, Object> data;

		// get image names from database
		imageNames = getImagesFromDB();
		//Log.i("Image", breakfast.toString());

		int i = 0;
		for (String name : imageNames) {
			data = new HashMap<String, Object>();
			data.put("img", PATH + name + ".jpg");
			date = new Date(new Long(name) * 1000);
			formatter = new SimpleDateFormat("HH:mm");
			day = getResources().getStringArray(R.array.weekday_names)[date
					.getDay()];
			month = getResources().getStringArray(R.array.month_names)[date
					.getMonth()];
			time = formatter.format(date);
			data.put("time", time);
			if(breakfast.get(i).equalsIgnoreCase("1") || lunch.get(i).equalsIgnoreCase("1") || dinner.get(i).equalsIgnoreCase("1")){
				data.put("eat", R.drawable.ic_eat);
			} else if (snack.get(i).equalsIgnoreCase("1")) {
				data.put("eat", R.drawable.ic_snack);
			}else {
				data.put("eat", R.drawable.ic_empty);
			}
			if(drink.get(i).equalsIgnoreCase("1")){
				data.put("drink", R.drawable.ic_drink);
			}else {
				data.put("drink", R.drawable.ic_empty);
			}
			if (!lastDate.equalsIgnoreCase(day + ", " + date.getDate() + " "
					+ month)) {
				lastDate = day + ", " + date.getDate() + " " + month;
				data.put("date", lastDate);
				data.put("clock", R.drawable.ic_clock);
			} else {
				data.put("date", "");
				data.put("clock", "");
			}

			resourceObject.add(data);
			i++;
		}

		// Create list adapter
		adapter = new SimpleAdapter(this, resourceObject, R.layout.image_row,
				new String[] { "img", "clock", "date", "time", "eat", "drink" }, new int[] { R.id.img,
						R.id.clock, R.id.date, R.id.time, R.id.box1, R.id.box2 });
		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setDivider(null);
		lv.setDividerHeight(0);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder confirmation = new AlertDialog.Builder(
						HistoryActivity2.this);
				confirmation.setTitle(R.string.d_image);
				confirmation.setMessage(R.string.d_image_info);
				confirmation.setPositiveButton(R.string.d_image_conf,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								ImageManager _imMng = new ImageManager();
								_imMng.deleteImageFile(
										imageNames.get(position),
										Diary.IMAGE_PATH + "/"
												+ imageNames.get(position)
												+ ".jpg");
								if (!NetworkStatusProvider
										.isConnected(getApplicationContext())) {
									// Log.d(t, "Connection unavailable");
								} else {
									userId = mSharedPreferences
											.getString(
													PreferencesActivity.KEY_USER_ID,
													"");
									Synchronizer sync = new Synchronizer(userId);
									sync.startSync();
								}
								// Log.d(t, "File deleted");
								imageNames.remove(position);
								resourceObject.remove(position);
								adapter.notifyDataSetChanged();

							}
						});
				confirmation.setNegativeButton(R.string.d_image_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Log.d("t", "File deletion cancelled");
							}
						});
				confirmation.show();

			}
		});

	}

	/**
	 * Gets image names from database
	 * 
	 * @return imageNames - returns list on images names
	 */
	public List<String> getImagesFromDB() {
		DataHelper data = new DataHelper();
		HashMap<String, List<String>> imageData = data.getImageNames();
		List<String> imageNames = imageData.get("images");
		breakfast = imageData.get("breakfast");
		lunch = imageData.get("lunch");
		dinner = imageData.get("dinner");
		snack = imageData.get("snack");
		drink = imageData.get("drink");
		data.closeConnection();
		return imageNames;
	}

}

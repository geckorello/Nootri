/**
 * 
 */
package com.online.nutrition.dietdiary.activities;

import java.util.ArrayList;
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
	private List<String> dates;

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
		//Log.i("Image",imageNames.toString());

		int i = 0;
		for (String name : imageNames) {
			data = new HashMap<String, Object>();
			data.put("img", PATH + name + ".jpg");
			data.put("date", dates.get(i));
			resourceObject.add(data);
			i++;
		}

		// Create list adapter
		adapter = new SimpleAdapter(this, resourceObject, R.layout.image_row,
				new String[] { "img", "date" }, new int[] { R.id.img, R.id.date });
		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				AlertDialog.Builder confirmation = new AlertDialog.Builder(HistoryActivity2.this);
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
									//Log.d(t, "Connection unavailable");
								} else {
									userId = mSharedPreferences
											.getString(
													PreferencesActivity.KEY_USER_ID,
													"");
									Synchronizer sync = new Synchronizer(userId);
									sync.startSync();
								}
								//Log.d(t, "File deleted");
								imageNames.remove(position);
								resourceObject.remove(position);
								adapter.notifyDataSetChanged();
								
							}
						});
				confirmation.setNegativeButton(R.string.d_image_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								//Log.d("t", "File deletion cancelled");
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
		dates = imageData.get("dates");
		data.closeConnection();
		return imageNames;
	}

}

package com.online.nutrition.dietdiary.activities;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.online.nutrition.dietdiary.R;
import com.online.nutrition.dietdiary.data.DataHelper;

public class StatsActivity extends ListActivity {

	private HashMap<String, List<String>> stats = new HashMap<String, List<String>>();
	private DataHelper data;
	private SimpleAdapter adapter;
	private List<Map<String, Object>> resourceObject;
	
	//time and date
	private Format formatter;
	private Date date;
	private String day;
	private String month;
	private String time;
	private String lastDate = "date";
	
	// log tag
	private static final String t = "StatsActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.stats);

		// define the list which holds the information of the list
		resourceObject = new ArrayList<Map<String, Object>>();

		// define the map which will hold the information for each row
		Map<String, Object> data;

		loadImageInfo();
		List<String> dates = stats.get("dates");
		List<String> meals = stats.get("meals");
		List<String> drinks = stats.get("drinks");
		List<String> snacks = stats.get("snacks");
		List<String> breakfastTime = stats.get("breakfastTime");
		List<String> lunchTime = stats.get("lunchTime");
		List<String> dinnerTime = stats.get("dinnerTime");
		List<String> lastMealTime = stats.get("lastMealTime");

		int i = 0;
		for (String d : dates) {
			data = new HashMap<String, Object>();
			//formatter = new SimpleDateFormat("HH:mm");
			date = new Date(new Long(d) * 1000);
			day = getResources().getStringArray(R.array.weekday_names)[date
					.getDay()];
			month = getResources().getStringArray(R.array.month_names)[date
					.getMonth()];
			//time = formatter.format(date);
			data.put("date", day + ", " + date.getDate() + " " + month);
			data.put("meals_value", meals.get(i));
			data.put("drinks_value", drinks.get(i));
			data.put("snacks_value", snacks.get(i));
			data.put("breakfast_value", breakfastTime.get(i));
			data.put("lunch_value", lunchTime.get(i));
			data.put("dinner_value", dinnerTime.get(i));
			data.put("lastmeal_value", lastMealTime.get(i));
			resourceObject.add(data);
			i++;
		}
		// Create list adapter
		adapter = new SimpleAdapter(this, resourceObject, R.layout.stat_row,
				new String[] {"date", "meals_value", "drinks_value",
						"snacks_value", "breakfast_value", "lunch_value",
						"dinner_value", "lastmeal_value" }, new int[] {
						R.id.date, R.id.meals_value, R.id.drinks_value,
						R.id.snacks_value, R.id.breakfast_value,
						R.id.lunch_value, R.id.dinner_value,
						R.id.lastmeal_value });
		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setDivider(null);
		lv.setDividerHeight(0);

	}

	/**
	 * Loads image info from DB to "images"
	 */
	public void loadImageInfo() {
		data = new DataHelper();
		stats = data.getStats();
		data.closeConnection();
		// Log.d(t, "DB session successful.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
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

}

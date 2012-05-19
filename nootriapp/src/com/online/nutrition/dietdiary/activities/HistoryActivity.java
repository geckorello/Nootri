/**
 * 
 * 
 */
package com.online.nutrition.dietdiary.activities;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.nutrition.dietdiary.R;
import com.online.nutrition.dietdiary.application.Diary;
import com.online.nutrition.dietdiary.data.DataHelper;
import com.online.nutrition.dietdiary.image.ImageManager;
import com.online.nutrition.dietdiary.preferences.PreferencesActivity;
import com.online.nutrition.dietdiary.server.Synchronizer;
import com.online.nutrition.dietdiary.service.NetworkStatusProvider;
import com.online.nutrition.dietdiary.utils.FileUtils;

/**
 * Displays taken photos in gallery
 * 
 * @author Mihkel Vunk (mihkel.vunk#gmail.com)
 * 
 */

public class HistoryActivity extends Activity {

	// history objects
	private Gallery gallery;
	private ImageView imgView;
	private List<String> imageNames;
	private SharedPreferences mSharedPreferences;
	private String userId;
	private AddImgAdp addImgAdp;
	private TextView notification;

	// log tag
	private static final String t = "HistoryActivity";

	/**
	 * Gets image names from database
	 * 
	 * @return imageNames - returns list on images names
	 */
	public List<String> getImagesFromDB() {
		DataHelper data = new DataHelper();
		HashMap<String, List<String>> imageData = data.getImageNames();
		List<String> imageNames = imageData.get("images");
		data.closeConnection();
		return imageNames;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		//Log.i(t, "HistoryActivity started.");

		// get the shared preferences object
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		// get image names from database
		imageNames = getImagesFromDB();
		// find big image view
		imgView = (ImageView) findViewById(R.id.ImageView01);
		// get notification
		notification = (TextView) findViewById(R.id.noImg);

		// Check if there are images in db
		if (imageNames.isEmpty()) {
			//Log.d(t, "No images in DB");
			notification.setVisibility(View.VISIBLE);
			// Build view
		} else {
			File imageFile = new File(Diary.IMAGE_PATH + "/"
					+ imageNames.get(0) + ".jpg");
			imgView.setImageBitmap(FileUtils.getBitmapScaledToDisplay(
					imageFile, 400, 400)); //is this the right size?

			// get galleryview
			gallery = (Gallery) findViewById(R.id.examplegallery);
			//Initialize adapter
			addImgAdp = new AddImgAdp(this);
			gallery.setAdapter(addImgAdp);

			// set onclicks to every image
			gallery.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView parent, View v,
						int position, long id) {
					File imageFile = new File(Diary.IMAGE_PATH + "/"
							+ imageNames.get(position) + ".jpg");
					imgView.setImageBitmap(FileUtils.getBitmapScaledToDisplay(
							imageFile, 400, 400));
				}
			});
			//set delete dialog onlongclick
			gallery.setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView parent, View v,
						final int position, long id) {
					AlertDialog.Builder confirmation = new AlertDialog.Builder(
							HistoryActivity.this);
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
										//Log.d(t,"Connection unavailable");
									} else {
										userId = mSharedPreferences
												.getString(
														PreferencesActivity.KEY_USER_ID,
														"");
										Synchronizer sync = new Synchronizer(
												userId);
										sync.startSync();
									}
									//Log.d(t, "File deleted");
									imageNames.remove(position);
									addImgAdp.notifyDataSetChanged();
									imgView.setImageBitmap(null);
									if (imageNames.isEmpty())
										notification
												.setVisibility(View.VISIBLE);
								}
							});
					confirmation.setNegativeButton(R.string.d_image_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									//Log.d("t","File deletion cancelled");
								}
							});
					confirmation.show();
					return true;

				}
			});

		}

	}

	/**
	 * Add image adapter
	 * helps to build gallery
	 * 
	 *  @author Mihkel Vunk (mihkel.vunk#gmail.com)
	 *
	 */
	public class AddImgAdp extends BaseAdapter {
		int GalItemBg;
		private Context cont;

		/**
		 * Adapter constructor
		 * @param c - application context
		 */
		public AddImgAdp(Context c) {
			cont = c;
			TypedArray typArray = obtainStyledAttributes(R.styleable.HistoryActivity);
			GalItemBg = typArray.getResourceId(
					R.styleable.HistoryActivity_android_galleryItemBackground,
					0);
			typArray.recycle();
		}

		
		/**
		 * Get Count
		 * @return returns number of images
		 */
		public int getCount() {
			return imageNames.size();
		}

		/**
		 * Get item position
		 * @param position  - image position
		 * @return returns image in certain position
		 */
		public Object getItem(int position) {
			return position;
		}

		/**
		 * Get item position
		 * @param position - image position
		 * @return returns item position
		 */
		public long getItemId(int position) {
			return position;
		}

		/** 
		 * Get view - builds imageview
		 * @param position - image position
		 * @param convertView - converted view
		 * @param parent - parent of a view
		 * @return returns imageview
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			DataHelper data = new DataHelper();

			ImageView imgView = new ImageView(cont);

			File imageFile = new File(Diary.IMAGE_PATH + "/"
					+ imageNames.get(position) + ".jpg");
			Bitmap scaled = FileUtils.getBitmapScaledToDisplay(imageFile, 150,
					150);
			Bitmap thumb = scaled.copy(Bitmap.Config.ARGB_8888, true);

			HashMap<String, List<String>> iMgs = data.getUploadImages();
			//Log.d("t", iMgs.get("timestamp").toString());
			if (iMgs.get("timestamp").contains(imageNames.get(position))) {
				Canvas canv = new Canvas(thumb);
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap badge = BitmapFactory.decodeResource(
						cont.getResources(), R.drawable.cross, options);
				canv.drawBitmap(badge, (float) 150, (float) 105, new Paint());
			}

			imgView.setImageBitmap(thumb);
			imgView.setLayoutParams(new Gallery.LayoutParams(150, 140));
			imgView.setScaleType(ImageView.ScaleType.FIT_XY);
			imgView.setBackgroundResource(GalItemBg);
			data.closeConnection();
			return imgView;
		}
	}

}

package com.online.nutrition.dietdiary.server;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

import com.online.nutrition.dietdiary.application.Diary;
import com.online.nutrition.dietdiary.data.DataHelper;
import com.online.nutrition.dietdiary.image.ImageManager;

/**
 * 
 * Image deleting synchronizer class
 * 
 * @author Aare Puussaar (aare.puussaar#gmail.com)
 * 
 * 
 */
public class Synchronizer {
private String id;
	

	public Synchronizer(String id){
		this.id = id;
	}
	
	private class SyncTask extends AsyncTask<String, Void, String> {
        
        protected String doInBackground(String... parameters) {
        	//Remove images deleted in the phone
        	ImageManager _imMng = new ImageManager();
        	DataHelper data = new DataHelper();
        	List<String> images = data.getDeletedImages();
        	if(!images.isEmpty()){
	        	for (String name : images){
					String response = HTTPRequestPoster.sendPostRequest(
							ServerHelper.URL_TAG, ServerHelper
									.urlRequestParameters(
											ServerHelper.DELETE_METHOD,
											new String[] { id,
													name}));
					if (Integer.parseInt(response) == 1){
						_imMng.deleteDBImage(name);
					}
	        	}
        	}
        	//Remove images deleted in the server http://www.nootri.com/mobileHelper.php?o=s&uid=62
        	
        	String mImageFolder = Diary.IMAGE_PATH;
        	String [] variables = {id};
        	HashMap<String, List<String>> mImages = data.getImageNames();        	
        	////Log.d("MobileImages", mImages.get("images").toString());
        	String response = null;
			response = HTTPRequestPoster.sendPostRequest(
					ServerHelper.URL_TAG, ServerHelper
					.urlRequestParameters(
							ServerHelper.SYNC_METHOD,
							variables));
			////Log.d("RESPONSE", response);
        	try {
				JSONArray jImages = new JSONArray(response);
				// compare elements from server and in mobile db, if equal, remove from list
				for (int i=0; i < jImages.length() ; i++){
					if(mImages.get("images").contains(jImages.getJSONObject(i).get("ts").toString())){
						mImages.get("images").remove(jImages.getJSONObject(i).get("ts").toString());
					}
				}
				//Log.d("SYNC", "Found "+ mImages.get("images").size() + " deleted images.");
				// remove images, which were not on server				
				try {
					for(int i=0; i<mImages.get("images").size(); i++){
						File file = new File(mImageFolder+ "/"+ mImages.get("images").get(i).toString()+".jpg");
						boolean deleted = file.delete();
						if (deleted) {
							//Log.d("SYNC", "Deleting image " + mImages.get("images").get(i).toString() + ".");
						}
						_imMng.deleteDBImage(mImages.get("images").get(i).toString());
						
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	data.closeConnection();
			return "COMPLETE";
            
        }
	}
	
	protected boolean onPostExecute(Long result) {
		 return true;
	}
	
	public void startSync(){
		new SyncTask().execute();
	}
}

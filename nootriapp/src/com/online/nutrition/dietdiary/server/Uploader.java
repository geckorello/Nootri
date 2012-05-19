package com.online.nutrition.dietdiary.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.online.nutrition.dietdiary.application.Diary;
import com.online.nutrition.dietdiary.data.DataHelper;


/**
 * 
 * Image uploader class
 * 
 * @author Aare Puussaar (aare.puussaar#gmail.com)
 * 
 * 
 */
public class Uploader {

	private String id;
	private List<String> com; 
	private List<String> c1;
	private List<String> c2;
	private List<String> c3;
	private List<String> c4;
	private List<String> c5;
	private List<String> names;
	

	public Uploader(String id){
		this.id = id;
	}
	
	private class UploadTask extends AsyncTask<String, Void, Boolean> {
		        
        @Override
		protected void onPostExecute(Boolean result) {
        	// Calling Synchronizer 
        	Synchronizer sync = new Synchronizer(id);
        	sync.startSync();
			super.onPostExecute(result);
		}

		protected Boolean doInBackground(String... parameters) {
        	String mImageFolder = Diary.IMAGE_PATH;

        	DataHelper data = new DataHelper();
        	HashMap<String, List<String>> imageData = data.getUploadImages();
        	com = imageData.get("comments");
        	c1 = imageData.get("cb1");
        	c2 = imageData.get("cb2");
        	c3 = imageData.get("cb3");
        	c4 = imageData.get("cb4");
        	c5 = imageData.get("cb5");
        	names = imageData.get("timestamp");
        	int i = 0;
			for (String name : names) {
				 String [] variables = {id,com.get(i),c1.get(i),c2.get(i),c3.get(i),c4.get(i),c5.get(i),name};
	        	String s = mImageFolder + "/" + name + ".jpg";
	        	
	        	String response = null;
				try {
					//TODO: implement zip
					response = HTTPRequestPoster.postData(ServerHelper.URL_TAG, ServerHelper.urlRequestParameters(ServerHelper.UPLOAD_METHOD, variables),new FileInputStream(s));
					////Log.d("UPLOADER",response);
					if(response != null){
						if(Integer.parseInt(response) == 1){
							data.setUploaded(name);
						}
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				i++;
			}
			data.closeConnection();
			return true;
            
        }

	}
	
	public void startUpload(){
		new UploadTask().execute();
	}
}

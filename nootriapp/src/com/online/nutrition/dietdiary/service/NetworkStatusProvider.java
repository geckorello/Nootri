package com.online.nutrition.dietdiary.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class for checking if there is network
 * 
 * Aare Puussaar (aare.puussaar#gmail.com)
 * 
 */
public class NetworkStatusProvider {

	/**
	 * Check connection availability
	 * 
	 * @param context - appliaction context
	 * @return true if connected
	 */
	synchronized public static boolean isConnected(Context context) {
		// is there a network connection
		ConnectivityManager connMgr = (ConnectivityManager) context
		.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if(netInfo == null)
			// if no info then no connection
			return false;
		// check if connected and available
		if(!netInfo.isAvailable() || !netInfo.isConnected())
			return false;
		// check for wifi availability
		if(netInfo.getType() == ConnectivityManager.TYPE_WIFI)
			return true;
		// if there is no wifi, check for background data availability
		return connMgr.getBackgroundDataSetting();
	}
	
	/**
	 * Check network availability
	 * 
	 * @param context
	 * @return true if connected
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}

package com.online.nutrition.dietdiary.server;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Class for building server communication requests
 * 
 * @author Aare Puussaar (aare.puussaar#gmail.com)
 */
public class ServerHelper {
	
	//URL tag
	public static final String URL_TAG = "http://www.nootri.com/mobileHelper.php?";
	
	//Parameters
	public static final char LOGIN_METHOD = 'l';
	public static final char REGISTER_METHOD = 'r';
	public static final char SYNC_METHOD = 's';
	public static final char DELETE_METHOD = 'd';
	public static final char UPLOAD_METHOD = 'u';
	public static final char NOTIFICATION_METHOD = 'n';
	public static final String SERVER_RESPONSE_ERROR = "01";
	public static final String SERVER_RESPONSE_USERNAME_ERROR = "0";
	//http://www.nootri.com/mobileHelper.php?o=s&uid=62
	
	/**
	 * Method for setting up url request parameters
	 * 
	 * @param option - request method
	 * @param parameters - request parameters
	 * @return
	 */
	public static String urlRequestParameters(char option, String[] parameters){
		StringBuffer sb=new StringBuffer();
		switch (option) {
		case LOGIN_METHOD: 
			sb.append("o=");
			sb.append(LOGIN_METHOD);
			sb.append("&u=");
			sb.append(parameters[0]);
			sb.append("&p=");
			sb.append(parameters[1]);
			break;
		case REGISTER_METHOD: 
			sb.append("o=");
			sb.append(REGISTER_METHOD);
			sb.append("&u=");
			sb.append(parameters[0]);
			sb.append("&p=");
			sb.append(parameters[1]);
			break;
		case SYNC_METHOD: 
			sb.append("o=");
			sb.append(SYNC_METHOD);
			sb.append("&uid=");
			sb.append(parameters[0]);
			break;
		case DELETE_METHOD: 
			sb.append("o=");
			sb.append(DELETE_METHOD);
			sb.append("&u=");
			sb.append(parameters[0]);
			sb.append("&t=");
			sb.append(parameters[1]);
			break;
		case UPLOAD_METHOD:
			sb.append("o=");
			sb.append(UPLOAD_METHOD);
			sb.append("&u=");
			sb.append(parameters[0]);
			sb.append("&c=");
			try {
				sb.append(URLEncoder.encode(parameters[1], "ISO-8859-1"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sb.append("&c1=");
			sb.append(parameters[2]);
			sb.append("&c2=");
			sb.append(parameters[3]);
			sb.append("&c3=");
			sb.append(parameters[4]);
			sb.append("&c4=");
			sb.append(parameters[5]);
			sb.append("&c5=");
			sb.append(parameters[6]);;
			sb.append("&t=");
			sb.append(parameters[7]);
			break;
		case NOTIFICATION_METHOD:
			sb.append("o=");
			sb.append("n");
			break;
		}
		return sb.toString();
	}
}

package com.online.nutrition.dietdiary.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.util.Log;

/**
 * 
 * Handles server communication
 * 
 * @author Aare Puussaar (aare.puussaar#gmail.com)
 *
 */

public class HTTPRequestPoster {
	
	/**
	 * Static class for https 
	 * - trust every certificate
	 * 
	 * @author Aare Puussaar (aare.puussaar#gmail.com)
	 *
	 */
	static class _FakeX509TrustManager implements X509TrustManager {

	    private static TrustManager[] trustManagers;
	    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

	    public void checkClientTrusted(X509Certificate[] chain, String
	authType) throws CertificateException {
	    }

	    public void checkServerTrusted(X509Certificate[] chain, String
	authType) throws CertificateException {
	    }

	    public boolean isClientTrusted(X509Certificate[] chain) {
	        return true;
	    }

	    public boolean isServerTrusted(X509Certificate[] chain) {
	        return true;
	    }

	   
	    public X509Certificate[] getAcceptedIssuers() {
	        return _AcceptedIssuers;
	    }

	    public static void allowAllSSL() {
	        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
	        	{
		            public boolean verify(String hostname, SSLSession session) {
		                return true;
		            }

	        	});

	        SSLContext context = null;
	        if (trustManagers == null) {
	            trustManagers = new TrustManager[] { new _FakeX509TrustManager() };
	        }

	        try {
	            context = SSLContext.getInstance("TLS");
	            context.init(null, trustManagers, new SecureRandom());
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        } catch (KeyManagementException e) {
	            e.printStackTrace();
	        }

	        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
	    }

	}
	
	static {
		_FakeX509TrustManager.allowAllSSL();
	}
	
	/**
	 * Sends an HTTP GET request to a url
	 * 
	 * @param endpoint
	 *            - The URL of the server. (Example:
	 *            " http://www.yahoo.com/search")
	 * @param requestParameters
	 *            - all the request parameters (Example:
	 *            "param1=val1&param2=val2"). Note: This method will add the
	 *            question mark (?) to the request - DO NOT add it yourself
	 * @return - The response from the end point
	 */
	synchronized public static String sendPostRequest(String endpoint,
			String requestParameters) {
		// Send a POST request to the server
		HttpURLConnection conn = null;
		try {
				
			// Send data
			URL url = new URL(endpoint + requestParameters);
			conn = (HttpURLConnection) url.openConnection();
			try {
				conn.setRequestMethod("POST");
			} catch (ProtocolException e) {
				return "Protocol Exeption";
			}
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			//TODO: what timeouts to use
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setRequestProperty("Content-type", "application/octet-stream");

			
			OutputStream out = conn.getOutputStream();
			out.close();
			
				
			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()),1024);
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			
			
			return sb.toString();
		} catch (Exception e) {
			return "General Exeption";
		} finally {
			if (conn != null)
				conn.disconnect();
		}
	}

	/**
	 * Reads data from the data reader and posts it to a server via POST
	 * request. data - The data you want to send endpoint - The server's address
	 * output - writes the server's response to output
	 * 
	 * @throws Exception
	 */
	public synchronized static String postData(String endpoint,
			String requestParameters, InputStream data) {
		HttpURLConnection urlc = null;
		try {
			
			
			// Send data
			URL url = new URL(endpoint + requestParameters);
			Log.d("URL", url.toString());
			urlc = (HttpURLConnection) url.openConnection();
			try {
				urlc.setRequestMethod("POST");
			} catch (ProtocolException e) {
				return null;
			}
			urlc.setDoOutput(true);
			urlc.setDoInput(true);
			urlc.setUseCaches(false);
			urlc.setAllowUserInteraction(false);
			//TODO: what timeouts to use
			urlc.setConnectTimeout(30000); 
			urlc.setReadTimeout(30000);
			urlc.setRequestProperty("Content-type", "application/octet-stream");

			
			OutputStream out = urlc.getOutputStream();
			int length = 0;
			try {
				length = pipe(data, out);
			} catch (IOException e) {
				return null;
			} finally {
				if (out != null)
					out.close();
			}
			
			

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(urlc.getInputStream()),1024);
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			
			
			return sb.toString();

		} catch (IOException e) {
			
			return null;
		} catch(Throwable e) {
			
			return null;
		} finally {
			if (urlc != null)
				urlc.disconnect();
		}
	}
	
	/**
	 * Pipes everything from the reader to the writer via a buffer
	 */
	private static int pipe(InputStream in, OutputStream out) throws IOException {
		byte[] buf = new byte[1024];
		int len = 0;
		int read = 0;
		while ((read = in.read(buf)) >= 0) {
			len += read;
			out.write(buf, 0, read);
		}
		out.flush();
		return len;
	}

}
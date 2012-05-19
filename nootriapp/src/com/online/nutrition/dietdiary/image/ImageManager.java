package com.online.nutrition.dietdiary.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.online.nutrition.dietdiary.application.Diary;
import com.online.nutrition.dietdiary.data.DataHelper;

/**
 * This class handles images 
 * 
 * @author Aare Puussaar (aare.puussar#gmail.com)
 */
public class ImageManager {
	
	//image info
	private String imageFile;
	private int desiredWidth = 600;
	private int desiredHeight = 480;
	
	/**
	 * Method for zipping images
	 * 
	 * @param name - image name
	 * @param uri - location of the image
	 * @return true if zipped
	 */
	public boolean zipImage(String name, String uri){
		File imageZipFile = new File(Diary.CACHE_PATH,name +".zip");
		if(!imageZipFile.exists()){
			try {
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(imageZipFile));
				out.putNextEntry(new ZipEntry(name +".jpg"));
				FileInputStream in = new FileInputStream(uri);
				byte[] buf = new byte[8192];
				int count = 0;
				while ((count = in.read(buf)) != -1)
					out.write(buf,0,count);
				out.closeEntry();
				in.close();
			} catch (FileNotFoundException e1) {
				return false;
			} catch (IOException e) {
				return false;
			}
		return true;
		}
		return false;
	}

	/**
	 * Method for saveing image
	 * 
	 * @param now - timestamp and image name
	 * @param info - image info in bytes
	 * @return
	 */
	public String saveImage(String now, byte[] info){
    	//image save path
        String mImageFolder = Diary.IMAGE_PATH;
        imageFile = mImageFolder + "/" + now + ".jpg";
        //writes the stream to file
		try {
			OutputStream out = new FileOutputStream(imageFile);
			out.write(info);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageFile;
	}
	
	public String saveTempImage(byte[] info){
        //writes the stream to tempfile
		try {
			OutputStream out = new FileOutputStream(Diary.TMPFILE_PATH);
			out.write(info);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageFile;
	}
	
	/**
	 * Method for deleting image file
	 * 
	 * @param name - image name
	 * @param uri - image location
	 * @return true if deleted
	 */
	public boolean deleteImageFile(String name, String uri){
		File file = new File(uri);
		boolean deleted = file.delete();
		if(deleted){
        	DataHelper data = new DataHelper();
        	deleted = data.setDeleted(name);
        	data.closeConnection();
		}
		return deleted;
	}
	
	/**
	 * Method for calling image deleting in database
	 * 
	 * @param name - imagename
	 * @return true if row deleted
	 */
	public boolean deleteDBImage(String name){
        DataHelper data = new DataHelper();
        boolean deleted = data.deleteImage(name);
        data.closeConnection();
		return deleted;
	}
	
	/**
	 * Method for resizingi image
	 * 
	 * @param uri - image location
	 */
	public void resizeImage(String uri){
		// Get the source image's dimensions
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(uri, options);

		int srcWidth = options.outWidth;
		int srcHeight = options.outHeight;

		// Only scale if the source is big enough. This code is just trying to fit a image into a certain width.
		if(desiredWidth > srcWidth)
		    desiredWidth = srcWidth;


		/*
		// Calculate the correct inSampleSize/scale value. This helps reduce memory use. It should be a power of 2
		// from: http://stackoverflow.com/questions/477572/android-strange-out-of-memory-issue/823966#823966
		int inSampleSize = 1;
		while(srcWidth / 2 > desiredWidth){
		    srcWidth /= 2;
		    srcHeight /= 2;
		    inSampleSize *= 2;
		}

		float desiredScale = (float) desiredWidth / srcWidth;
		*/
		float desiredScale = Math.max(((float) desiredWidth) / srcWidth, ((float) desiredHeight) / srcHeight);
		// Decode with inSampleSize
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inSampleSize = (int) desiredScale;
		options.inScaled = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(uri, options);

		// Resize
		Matrix matrix = new Matrix();
		matrix.postScale(desiredScale, desiredScale);
		Bitmap scaledBitmap = Bitmap.createBitmap(sampledSrcBitmap, 0, 0, sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(), matrix, true);
		sampledSrcBitmap = null;

		// Save
		FileOutputStream out;
		try {
			out = new FileOutputStream(uri);
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		scaledBitmap = null;
	}
}

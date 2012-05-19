package com.online.nutrition.dietdiary.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Static methods used for common file operations.
 * 
 * @author Aare Puussaar (aare.puussaar#gmail.com)
 */
public class FileUtils {
    private final static String t = "FileUtils";

    
    /**
     * Method for creating folders
     * 
     * @param path - folder path
     * @return
     */
    public static boolean createFolder(String path) {
        boolean made = true;
        File dir = new File(path);
        if (!dir.exists()) {
            made = dir.mkdirs();
        }
        return made;
    }


    /**
     * Method for converting file to bytes
     * 
     * @param file - filename
     * @return bytes from file
     */
    public static byte[] getFileAsBytes(File file) {
        byte[] bytes = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);

            // Get the size of the file
            long length = file.length();
            if (length > Integer.MAX_VALUE) {
                //Log.e(t, "File " + file.getName() + "is too large");
                return null;
            }

            // Create the byte array to hold the data
            bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int read = 0;
            try {
                while (offset < bytes.length && read >= 0) {
                    read = is.read(bytes, offset, bytes.length - offset);
                    offset += read;
                }
            } catch (IOException e) {
                //Log.e(t, "Cannot read " + file.getName());
                e.printStackTrace();
                return null;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                try {
                    throw new IOException("Could not completely read file " + file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            return bytes;

        } catch (FileNotFoundException e) {
            //Log.e(t, "Cannot find " + file.getName());
            e.printStackTrace();
            return null;

        } finally {
            // Close the input stream
            try {
                is.close();
            } catch (IOException e) {
                //Log.e(t, "Cannot close input stream for " + file.getName());
                e.printStackTrace();
                return null;
            }
        }
    }


    /**
     * Getting md5 has for file
     * 
     * @param file - filename
     * @return md5 hash
     */
    public static String getMd5Hash(File file) {
        try {
            // CTS (6/15/2010) : stream file through digest instead of handing it the byte[]
            MessageDigest md = MessageDigest.getInstance("MD5");
            int chunkSize = 256;

            byte[] chunk = new byte[chunkSize];

            // Get the size of the file
            long lLength = file.length();

            if (lLength > Integer.MAX_VALUE) {
                //Log.e(t, "File " + file.getName() + "is too large");
                return null;
            }

            int length = (int) lLength;

            InputStream is = null;
            is = new FileInputStream(file);

            int l = 0;
            for (l = 0; l + chunkSize < length; l += chunkSize) {
                is.read(chunk, 0, chunkSize);
                md.update(chunk, 0, chunkSize);
            }

            int remaining = length - l;
            if (remaining > 0) {
                is.read(chunk, 0, remaining);
                md.update(chunk, 0, remaining);
            }
            byte[] messageDigest = md.digest();

            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);
            while (md5.length() < 32)
                md5 = "0" + md5;
            is.close();
            return md5;

        } catch (NoSuchAlgorithmException e) {
            //Log.e("MD5", e.getMessage());
            return null;

        } catch (FileNotFoundException e) {
            //Log.e("No Cache File", e.getMessage());
            return null;
        } catch (IOException e) {
            //Log.e("Problem reading from file", e.getMessage());
            return null;
        }

    }


    /**
     * Method for resizing bitmap for scale
     * 
     * @param f - filename
     * @param screenHeight - height parameter
     * @param screenWidth - width parameter
     * @return resized bitmap file
     */
    public static Bitmap getBitmapScaledToDisplay(File f, int screenHeight, int screenWidth) {
        // Determine image size of file
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(f.getAbsolutePath(), o);

        int heightScale = o.outHeight / screenHeight;
        int widthScale = o.outWidth / screenWidth;

        
        int scale = Math.max(widthScale, heightScale);

        // get bitmap with scale ( < 1 is the same as 1)
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
        if (b != null) {
        //Log.i(t,"Screen is " + screenHeight + "x" + screenWidth + ".  Image has been scaled down by "+ scale + " to " + b.getHeight() + "x" + b.getWidth());
        }
        return b;
    }


    /**
     * Method for copying file
     * 
     * @param sourceFile - Source file
     * @param destFile - Destination file to copy 
     */
    public static void copyFile(File sourceFile, File destFile) {
        if (sourceFile.exists()) {
            FileChannel src;
            try {
                src = new FileInputStream(sourceFile).getChannel();
                FileChannel dst = new FileOutputStream(destFile).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            } catch (FileNotFoundException e) {
                //Log.e(t, "FileNotFoundExeception while copying audio");
                e.printStackTrace();
            } catch (IOException e) {
                //Log.e(t, "IOExeception while copying audio");
                e.printStackTrace();
            }
        } else {
            //Log.e(t, "Source file does not exist: " + sourceFile.getAbsolutePath());
        }

    }
    
    
}

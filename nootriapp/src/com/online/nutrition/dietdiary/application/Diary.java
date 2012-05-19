
package com.online.nutrition.dietdiary.application;

import java.io.File;

import android.app.Application;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.online.nutrition.dietdiary.R;

/**
 * Extends the Application class to implement 
 * @author Aare Puussaar (aare.puussaar#gmail.com)
 *
 */
public class Diary extends Application {

    // Storage paths
    public static final String DD_ROOT = Environment.getExternalStorageDirectory() + "/dietdiary";
    public static final String IMAGE_PATH = DD_ROOT + "/images";
    public static final String CACHE_PATH = DD_ROOT + "/.cache";
    public static final String METADATA_PATH = DD_ROOT + "/metadata";
    public static final String TMPFILE_PATH = CACHE_PATH + "/tmp.jpg";
    
   
    private static Diary singleton = null;


    public static Diary getInstance() {
        return singleton;
    }


    /**
     * Creates required directories on the SDCard (or other external storage)
     * @throws RuntimeException if there is no SDCard or the directory exists as a non directory
     */
    public static void createDDDirs() throws RuntimeException {
        String cardstatus = Environment.getExternalStorageState();
        if (cardstatus.equals(Environment.MEDIA_REMOVED)
                || cardstatus.equals(Environment.MEDIA_UNMOUNTABLE)
                || cardstatus.equals(Environment.MEDIA_UNMOUNTED)
                || cardstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)
                || cardstatus.equals(Environment.MEDIA_SHARED)) {
            RuntimeException e =
                new RuntimeException("DD reports :: SDCard error: "
                        + Environment.getExternalStorageState());
            throw e;
        }

        String[] dirs = {
                DD_ROOT, IMAGE_PATH, CACHE_PATH, METADATA_PATH
        };

        for (String dirName : dirs) {
            File dir = new File(dirName);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    RuntimeException e =
                        new RuntimeException("DD reports :: Cannot create directory: " + dirName);
                    throw e;
                }
            } else {
                if (!dir.isDirectory()) {
                    RuntimeException e =
                        new RuntimeException("DD reports :: " + dirName
                                + " exists, but is not a directory");
                    throw e;
                }
            }
        }
    }

    @Override
    public void onCreate() {
        singleton = this;
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        super.onCreate();
    }

}

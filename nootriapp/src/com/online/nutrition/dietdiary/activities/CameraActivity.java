/**
 * 
 * 
 */
package com.online.nutrition.dietdiary.activities;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.online.nutrition.dietdiary.R;
import com.online.nutrition.dietdiary.application.Diary;
import com.online.nutrition.dietdiary.image.ImageManager;

/**
 * Class for handling picture taking and saving
 * 
 * @author Aare Puussaar (aare.puussar#gmail.com)
 */
public class CameraActivity extends Activity implements SurfaceHolder.Callback {

	// log tag
	private static final String t = "CameraActivity";
	// camera options
	private Camera camera;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private boolean previewing = false;
	private LayoutInflater controlInflater = null;

	private String now;
	private int degrees;

	// Buttons
	private Button buttonTakePicture;
	private Button buttonBackToMenu;

	// layout components
	private RelativeLayout layoutBackground;
	private LayoutParams layoutParamsControl;



	final int RESULT_SAVEIMAGE = 0;

	/**
	 * 
	 * Called when the activity is first created.
	 * 
	 * 
	 * */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hides titlebar
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.camera);

		////Log.i(t, "Cameractivity, started.");

		// set screen orientation to landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// set up surface
		getWindow().setFormat(PixelFormat.UNKNOWN);
		surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// put taking picture button overlay
		controlInflater = LayoutInflater.from(getBaseContext());
		View viewControl = controlInflater.inflate(R.layout.control, null);
		layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		this.addContentView(viewControl, layoutParamsControl);

		// get buttons
		buttonTakePicture = (Button) findViewById(R.id.takepicture);
		buttonTakePicture.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				camera.takePicture(myShutterCallback, myPictureCallback_RAW,
						myPictureCallback_JPG);
			}
		});
		
		buttonBackToMenu = (Button) findViewById(R.id.back_menu);
		buttonBackToMenu.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), DietDiaryActivity.class);
    			startActivity(i);
    			finish();
			}
		});


		// camera backgroud layout
		layoutBackground = (RelativeLayout) findViewById(R.id.background);
		layoutBackground.setOnClickListener(new RelativeLayout.OnClickListener() {
			public void onClick(View arg0) {
				buttonTakePicture.setEnabled(false);
				camera.autoFocus(myAutoFocusCallback);
			}
		});
	}

	/**
	 * AutofocusCallback
	 * 
	 * buttonTakePicture is enabled
	 * 
	 */
	AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {
		public void onAutoFocus(boolean arg0, Camera arg1) {
			buttonTakePicture.setEnabled(true);
		}
	};

	/**
	 * 
	 * ShutterCallback
	 * 
	 */
	ShutterCallback myShutterCallback = new ShutterCallback() {
		public void onShutter() {
			// TODO Auto-generated method stub
		}
	};

	/**
	 * 
	 * PictureCallback
	 * 
	 * gets the raw image
	 * 
	 */
	PictureCallback myPictureCallback_RAW = new PictureCallback() {
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 
	 * PictureCallback
	 * 
	 * gets the image and saves it to the dietdiary folder also writes image
	 * info to database and initiates the uploader
	 * 
	 */
	PictureCallback myPictureCallback_JPG = new PictureCallback() {
		public void onPictureTaken(byte[] arg0, Camera arg1) {

			buttonTakePicture.setEnabled(false);

			// get time
			now = Long.toString(System.currentTimeMillis() / 1000);
			// save temp picture and end cameraview
			ImageManager _imMng = new ImageManager();
			_imMng.saveTempImage(arg0);
			_imMng.resizeImage(Diary.TMPFILE_PATH);
			Intent i = new Intent(getApplicationContext(), ImageActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			i.putExtra("time", now);
			startActivity(i);
			finish();
		}

	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder
	 * , int, int, int)
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		//Log.d("TAG", "surfaceChanged");
		if (previewing) {
			camera.stopPreview();
			previewing = false;
		}

		if (camera != null) {
			try {
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
				previewing = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder
	 * )
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			////Log.d("TAG", "surfaceCreated method");
			camera = Camera.open();
		} catch (NoSuchMethodError e) {
			// e.printStackTrace();
			camera = Camera.open();
		}

		setCameraDisplayOrientation(this, 0, camera);

	}

	public void setCameraDisplayOrientation(Activity activity, int cameraId,
			android.hardware.Camera camera) {
		int result = 0;
		//Rotation method not working on certain phones
		// in API < 9, there is no CameraInfo() method.
		/*
		try {
			android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
			android.hardware.Camera.getCameraInfo(cameraId, info);

			int rotation = activity.getWindowManager().getDefaultDisplay()
					.getRotation();
			switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			}
			

			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (info.orientation + degrees) % 360;
				result = (360 - result) % 360; // compensate the mirror
			} else { // back-facing
				result = (info.orientation - degrees + 360) % 360;
			}
			camera.setDisplayOrientation(result);
			// if API lower, use another technique
		} catch (NoClassDefFoundError e) { 
											
			// e.printStackTrace();
			
		}*/
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
		.getDefaultDisplay();
		degrees = display.getOrientation();
		////Log.i("DEGREES", Integer.toString(degrees));
		
		if (degrees == 0) {
			result = 90;
		} else if (degrees == 3) {
			result = 180;
		}
		camera.setDisplayOrientation(result);
				Parameters params = camera.getParameters();
				params.setRotation(result);
				camera.setParameters(params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.
	 * SurfaceHolder)
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.stopPreview();
		camera.release();
		camera = null;
		previewing = false;
	}
}
package au.edu.melbuni.boldapp;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, OnClickListener {
	static final int FOTO_MODE = 0;
	private static final String TAG = "CameraTest";
	Camera camera;
	boolean previewRunning = false;
	

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Log.e(TAG, "onCreate");
		
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN
		);
		setContentView(R.layout.camera);
		surfaceView = (SurfaceView) findViewById(R.id.userPictureSurfaceView);
		surfaceView.setOnClickListener(this);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {
			if (imageData != null) {
				Intent intent = new Intent();
				
				try {
					
		        	String fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		        	fileName += "/current_user.png";
		        	
		            FileOutputStream out = new FileOutputStream(fileName);
		            BufferedOutputStream bufOut = new BufferedOutputStream(out);
		            bufOut.write(imageData);
		            
		        } catch (Exception e) {
		            Log.e("Error reading file", e.toString());
		        }
				
				camera.startPreview();
				
				setResult(FOTO_MODE, intent);
				finish();
			}
		}
	};

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		camera = Camera.open();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.e(TAG, "surfaceChanged");

		// Note: stopPreview() will crash if preview is not running.
		//
		if (previewRunning) {
			camera.stopPreview();
		}

		Camera.Parameters p = camera.getParameters();
		p.setPreviewSize(w, h);
		camera.setParameters(p);
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		camera.startPreview();
		previewRunning = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "surfaceDestroyed");
		camera.stopPreview();
		previewRunning = false;
		camera.release();
	}

	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;

	public void onClick(View view) {
		camera.takePicture(null, pictureCallback, pictureCallback);
	}

}

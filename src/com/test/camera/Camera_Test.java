package com.test.camera;

import java.io.BufferedOutputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
/* 延伸學習 */
//import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;


/* 引用Camera類別 */
import android.hardware.Camera;

/* 引用PictureCallback作為取得拍照後的事件 */
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.preference.DialogPreference;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.Toast;


/* 使Activity實作SurfaceHolder.Callback */
public class Camera_Test extends Activity implements SurfaceHolder.Callback {
	/* 建立私有Camera物件 */
	private Camera mCamera01;
	private Button mButton01, mButton02, mButton03,mButton04, mBtn_public;

	/* 作為review照下來的相片之用 */
	private ImageView mImageView01;
	private static String TAG = "Camera_Test";
	private SurfaceView mSurfaceView01;
	private SurfaceHolder mSurfaceHolder01;
	
	/*選擇哪張圖片*/
	private Integer num;
	/* 預設相機預覽模式為false */
	private boolean bIfPreview = false;
	/*鏡頭拍攝方向 landscape is true*/
	private boolean camera_state = false;
	
	/* 將照下來的圖檔儲存在此 */
	private String strCaptureFilePath = "/sdcard/CamerTest/camera_snap.jpg";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/* 使應用程式全螢幕執行，不使用title bar */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		/* 判斷記憶卡是否存在 */
		if (!checkSDCard()) {
			/* 提醒User未安裝SD記憶卡 */
			mMakeTextToast(getResources().getText(R.string.str_err_nosd)
					.toString(), true);
		} else {
			String strDirectory = "/sdcard/CameraTest";

			new File(strDirectory).mkdir();
		}
		/* 取得螢幕解析像素 */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		/* 延伸學習 */
		// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		mImageView01 = (ImageView) findViewById(R.id.imageView1);
		// mImageView0test.setAlpha(-1);
		/* 以SurfaceView作為相機Preview之用 */
		mSurfaceView01 = (SurfaceView) findViewById(R.id.mSurfaceView1);
		// 取得螢幕顯示的資料
//		int ScreenWidth = dm.widthPixels;
//		int ScreenHeight = dm.heightPixels;
		// 螢幕寬和高的Pixels
				
		// Uri.fromFile(new
		// File(Environment.getExternalStorageDirectory(),"檔案名稱"));

		/* 繫結SurfaceView，取得SurfaceHolder物件 */
		mSurfaceHolder01 = mSurfaceView01.getHolder();
		/* Activity必須實作SurfaceHolder.Callback */
		mSurfaceHolder01.addCallback(Camera_Test.this);
		/* 欲在SurfaceView上繪圖，需先lock鎖定SurfaceHolder */
		// Canvas mCanvas01 = mSurfaceHolder02.lockCanvas();
		/* 設定畫布繪製顏色 */
		// mCanvas01.drawColor(getResources().getColor(R.drawable.white));
		/* 額外的設定預覽大小設定，在此不使用 */
		// mSurfaceHolder01.setFixedSize(320, 240);
		/*
		 * 以SURFACE_TYPE_PUSH_BUFFERS(3) 作為SurfaceHolder顯示型態
		 */
		mSurfaceHolder01.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		FindView();
		Button_Action();
		
	}
	private void FindView()
	{
		mButton01 = (Button) findViewById(R.id.myButton1);
		mButton02 = (Button) findViewById(R.id.myButton2);
		mButton03 = (Button) findViewById(R.id.myButton3);
		mButton04 = (Button) findViewById(R.id.photo_from_sd);
		mBtn_public = (Button) findViewById(R.id.public_photo);
	}
	
	protected void onResume()
	{
		super.onResume();
		String image_path = null;
		SharedPreferences settings = getSharedPreferences("Camera", 0);
		image_path = settings.getString("image_path", null);
		if(image_path!=null)
    	mImageView01.setImageURI(Uri.parse(image_path));
	}
	
	private void Button_Action() {
		
		
		mBtn_public.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				LinearLayout layout = new LinearLayout(Camera_Test.this);
	    		layout.setOrientation(LinearLayout.VERTICAL);
	    		//layout.setWeightSum(50);
	    		
	    		layout.setGravity(Gravity.CENTER_HORIZONTAL);
	    //		layout.setPadding(10, 20, 10, 20);
	    		layout.setBackgroundColor(Color.argb(0, 0, 255, 0));
	   // 		Gallery gallery = (Gallery)findViewById(R.id.gallery1);
	    		 Gallery gallery = new Gallery(Camera_Test.this);
				gallery.setUnselectedAlpha(0.5f);
				gallery.setBackgroundColor(Color.argb(0, 0, 255, 0));
			//	gallery.setSelected(true);
				//gallery.setId(R.id.gallery1);
				ImageAdapter imageAdapter = new ImageAdapter(Camera_Test.this);
				// 設定圖片來源
				final Integer[] mImageIds = { R.drawable.a,R.drawable.b, R.drawable.barack_obama,
						R.drawable.diablo1,
						R.drawable.photo4, R.drawable.sample_2, };
				//rotate(mImageIds[0]);
				// 設定圖片的位置
				imageAdapter.setmImageIds(mImageIds);
				// 圖片高度
				imageAdapter.setHeight(100);
				// 圖片寬度
				imageAdapter.setWidth(200);
				gallery.setAdapter(imageAdapter);
				gallery.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView parent, View view,
							int position, long id) {
					//	Toast.makeText(Camera_Test.this, "您選的是第" + position + "張圖",
					//			Toast.LENGTH_LONG).show();
						num= mImageIds[position];
						mImageView01.setImageResource(mImageIds[position]);
				
					//	rotate(mImageIds[position]);
					}
				});
				
	    		layout.addView(gallery);
	    		
	    		//new AlertDialog.Builder(new ContextThemeWrapper(Camera_Test.this,R.style.AlertDialogCustom))
	    		Dialog dialog = new AlertDialog.Builder(Camera_Test.this)
	    		.setView(layout)
	    		.show();
	    	
	    	    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();   
	    	    lp.alpha=0.5f;
	    	    lp.y = getWindowManager().getDefaultDisplay().getHeight()/4;
	    	    lp.width = getWindowManager().getDefaultDisplay().getWidth();
//	    	    Log.i(TAG,"Screen height is "+getWindowManager().getDefaultDisplay().getHeight());
	    	    lp.screenBrightness=lp.BRIGHTNESS_OVERRIDE_FULL;
	    	    dialog.getWindow().setAttributes(lp);  
	    	    dialog.show();
				
			}
		});
		
		/* 開啟相機及Preview */
		mButton01.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Log.i(TAG,"TEST_One");
				// Log.e(TAG,"No response");
				// TODO Auto-generated method stub
				/* 自訂初始化開啟相機函數 */
				initCamera();
			}
		});

		/* 停止Preview及相機 */
		mButton02.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				/* 自訂重置相機，並關閉相機預覽函數 */
				resetCamera();
			}
		});

		/* 拍照 */
		mButton03.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				/* 當記憶卡存在才允許拍照，儲存暫存影像檔 */
				if (checkSDCard()) {
					/* 自訂拍照函數 */
					takePicture();
				} else {
					/* 記憶卡不存在顯示提示 */
					Toast.makeText(Camera_Test.this, R.string.str_err_nosd,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		mButton04.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Log.i(TAG,"TEST_One");
				// Log.e(TAG,"No response");
				// TODO Auto-generated method stub
				/* 自訂初始化開啟相機函數 */
		//		GetFile getfile = new GetFile(Camera_Test.this);
				Intent intent = new Intent();
				intent.setClass(Camera_Test.this,GetFile.class);
				startActivity(intent);
				//StartActivity(GetFile);
				
			}
		});
	}

	/*旋轉圖片九十度*/
	private Bitmap rotate(Bitmap images) {
	//	final Bitmap mySourceBmp = BitmapFactory.decodeResource(getResources(),
		//		images);
		final int width_ori = images.getWidth();
		final int height_ori = images.getHeight();

		float width = width_ori;
		float height = height_ori;

		Matrix matrix = new Matrix();
		matrix.postScale(width, height);
		matrix.setRotate(90);

		Bitmap resized_Bmp = Bitmap.createBitmap(images, 0, 0, width_ori,
				height_ori, matrix, true);
	//	BitmapDrawable myNewBitmapDrawable = new BitmapDrawable(resized_Bmp);
		//mImageView01.setImageDrawable(myNewBitmapDrawable);
		return resized_Bmp;
	}
	/*建立浮水印圖片*/
	private Bitmap createBitmap(Bitmap src, Bitmap watermark) {
	//	String tag = "createBitmap";
		//Log.d(tag, "create a new bitmap");
		if (src == null) {
			return null;
		}
		
		int w = src.getWidth();
		int h = src.getHeight();
		Log.i(TAG,"w is :"+w);
		Log.i(TAG,"h is :"+h);
		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Bitmap scaled_watermark=null;
		Canvas cv = new Canvas(newb);
	//	newb.recycle();
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = 1;
	//	BitmapFactory.decodeResource(watermark,null,options);
		//Resource res = null;
	//	Bitmap scaled_watermark = Bitmap.createBitmap(watermark, 0, 0, w, h, null, true);
		if(camera_state)
			scaled_watermark = rotate(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),num,options), h, w,true));
		else
			scaled_watermark = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),num,options), w, h,true);

			int ww = scaled_watermark.getWidth();
	int wh = scaled_watermark.getHeight();
	Log.i(TAG,"ww is :"+ww);
	Log.i(TAG,"wh is :"+wh);
		// draw src intoBitmap
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
	//	if(scaled_watermark!=null)
		cv.drawBitmap(scaled_watermark, 0, 0, null);
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	}

	/* 自訂初始相機函數 */
	private void initCamera() {
		if (!bIfPreview) {
			/* 若相機非在預覽模式，則開啟相機 */
			try {
				/*
				 * The Heap 是應用程式在手機裡執行所配置的空間 當超過預設的16 MB（每一個應用程式）時就會導致
				 * "Out of memory"的錯誤 目前看來是Cupcake(AVD 1.5)才會發生 Connect E from
				 * ICameraClient 0x.... new client (0x...) sttempting to connect
				 * - rejected
				 */

				if (mCamera01 != null && bIfPreview) {
					mCamera01.stopPreview();

					/* 延伸學習，釋放Camera物件 */
					mCamera01.release();
					mCamera01 = null;
				}

				mCamera01 = Camera.open();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}

		if (mCamera01 != null && !bIfPreview) {
			try {
		
				mCamera01.setPreviewDisplay(mSurfaceHolder01);
				/* 建立Camera.Parameters物件 */
				Camera.Parameters parameters = mCamera01.getParameters();
				//parameters.set("jpeg-quality", 50);
				parameters.set("orientation", "landscape");
				/*
				 * for following code 2.2 is useable,but 2.0 is down
				 */
				if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				    // This is an undocumented although widely known feature
				    parameters.set("orientation", "portrait");
				    // For Android 2.2 and above  (加在這)
				    mCamera01.setDisplayOrientation(90);
				    // Uncomment for Android 2.0 and above
				    // parameters.setRotation(90);
				    // parameters.set("rotation", "0");
				    camera_state=false;
				   } else {
				    // This is an undocumented although widely known feature
				    parameters.set("orientation", "landscape");
				    // For Android 2.2 and above  (加在這)
				    mCamera01.setDisplayOrientation(0);
				    camera_state=true;
				    // Uncomment for Android 2.0 and above
				   // parameters.setRotation(0);
				   }
				/* 設定相片格式為JPEG */
				parameters.setPictureFormat(PixelFormat.JPEG);
				// parameters.setPreviewSize(w, h);
				List<Camera.Size> s = parameters.getSupportedPreviewSizes();

				try {
					if (s != null) {
						for (int i = 0; i < s.size(); i++) {
							Log.i(TAG, "" + (((Camera.Size) s.get(i)).height)
									+ "/" + (((Camera.Size) s.get(i)).width));
						}
					}
					parameters.setPreviewSize(320, 240);
					// parameters.setPreviewSize(176, 144);

					/* 在2.0模擬器中，設定不支援的PreviewSize將造成Exception */
					s = parameters.getSupportedPictureSizes();
					try {
						if (s != null) {
							for (int i = 0; i < s.size(); i++) {
								Log.i(TAG, ""
										+ (((Camera.Size) s.get(i)).height)
										+ "/"
										+ (((Camera.Size) s.get(i)).width));
							}
						}
						/* 在2.0模擬器中，設定不支援的PictureSize將造成Exception */
						parameters.setPictureSize(512, 384);
						// parameters.setPictureSize(213, 350);
						/* 將Camera.Parameters設定予Camera */
						mCamera01.setParameters(parameters);
						/* setPreviewDisplay唯一的參數為SurfaceHolder */
						mCamera01.setPreviewDisplay(mSurfaceHolder01);
						/* 立即執行Preview */
						mCamera01.startPreview();
						bIfPreview = true;
						
					} catch (Exception e) {
						Log.e(TAG, e.toString());
						e.printStackTrace();
					}
				} catch (Exception e) {
					Toast.makeText(Camera_Test.this, "initCameraT error.",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				mCamera01.release();
				mCamera01 = null;
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
		}
	}

	/* 拍照擷取影像 */
	private void takePicture() {
		if (mCamera01 != null && bIfPreview) {
			/* 呼叫takePicture()方法拍照 */
				
			mCamera01.takePicture(shutterCallback, rawCallback, jpegCallback);
		}
	}

	/* 相機重置 */
	private void resetCamera() {
		if (mCamera01 != null && bIfPreview) {
			mCamera01.stopPreview();

			/* 延伸學習，釋放Camera物件 */
			mCamera01.release();
			mCamera01 = null;

			
			bIfPreview = false;
		}
	}

	private ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Shutter has closed
		}
	};

	private PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			// TODO Handle RAW image data
		}
	};

	private PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			// TODO Handle JPEG image data
	//		BitmapFactory.Options opt = new BitmapFactory.Options();
	//		opt.inSampleSize = 4;
			/* onPictureTaken傳入的第一個參數即為相片的byte */
			Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length);
			/* 建立新檔 */
			//Bitmap scaled_bm = rotate(Bitmap.createScaledBitmap(bm, 640, 960,true));
			Bitmap scaled_bm = rotate(Bitmap.createScaledBitmap(bm, 960, 640,true));
			// File myCaptureFile = new
			// File(Environment.getExternalStorageDirectory()+"/CameraTest/test.jpg");
			bm.recycle();
			System.out.println("Before start photo");
			try {
			
				//Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),
				//		num);
				Bitmap mBitmap = null;
				Bitmap srcThree = createBitmap(scaled_bm, mBitmap);// 建立浮水印於已拍下的畫面上
			
				// iv2=(ImageView) findViewById(R.id.IV2);
				// iv2.setImageBitmap(srcThree);
			
				/* 將拍照下來且儲存完畢的圖檔，顯示出來 */
				// ImageView01.setImageBitmap(bm);
				// 以下存入SDCard
				System.out.println("Picture Save Start");

				File myCaptureFile = new File(
						"/sdcard/CameraTest/camera_snap.jpg");
				if (myCaptureFile == null)
					Log.e(TAG, "fail to open new file");
				else
					Log.e(TAG, myCaptureFile.toString());
				
			
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				
				srcThree.compress(CompressFormat.JPEG, 100, bos);
				bos.flush();

				/* 結束OutputStream */
				bos.close();
				System.out.println("Save picture done");
				/* 顯示完圖檔，立即重置相機，並關閉預覽 */
				resetCamera();

				/* 再重新啟動相機繼續預覽 */
				initCamera();
			} catch (Exception e) {
				//Log.e(TAG, e.getMessage());
				Log.e(TAG, e.toString());
			}
		}
	};
	/*以下為攝像相關函式*/
	
	/* 自訂刪除檔案函數 */
	private void delFile(String strFileName) {
		try {
			File myFile = new File(strFileName);
			if (myFile.exists()) {
				myFile.delete();
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	public void mMakeTextToast(String str, boolean isLong) {
		if (isLong == true) {
			Toast.makeText(Camera_Test.this, str, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(Camera_Test.this, str, Toast.LENGTH_SHORT).show();
		}
	}

	private boolean checkSDCard() {
		/* 判斷記憶卡是否存在 */
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w,
			int h) {
		// TODO Auto-generated method stub
		// Log.i(TAG, "Surface Changed1");
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceholder) {
		// TODO Auto-generated method stub
		// Log.i(TAG, "Surface Changed2");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
		// TODO Auto-generated method stub
		/* 當Surface不存在，需要刪除圖檔 */
		try {
			delFile(strCaptureFilePath);
			mCamera01.stopPreview();
			mCamera01.release();
			mCamera01 = null;
			Log.i(TAG, "Surface Destroyed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		try {
			resetCamera();
			mCamera01.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
	}

}
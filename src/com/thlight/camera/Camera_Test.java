package com.thlight.camera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


import com.thlight.camera.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
/* 延伸學習 */
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;


/* 引用Camera類別 */
import android.hardware.Camera;

/* 引用PictureCallback作為取得拍照後的事件 */
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;


import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.Toast;


/* 使Activiy實作SurfaceHolder.Callback */
public class Camera_Test extends Activity implements SurfaceHolder.Callback 
{
	/* 建立私有Camera物件 */
	private Camera mCamera01;
	private Button mBtn_take_picture,mBtn_photo_from_sd,mBtn_public;
	
	/* 作為review照下來的相片之用 */
	private ImageView mImageView01;	
	private SurfaceView mSurfaceView01;
	private SurfaceHolder mSurfaceHolder01;
	private String image_path = null;
	
	/*選擇哪張圖片*/
	private Integer num = null;
	private int SELECT_PICTURE;
	
	/* 預設相機預覽模式為false */
	private boolean bIfPreview = false;


	/* 將照下來的圖檔儲存在此 */
	private String strCaptureFilePath = "/sdcard/CameraTest/camera_snap.jpg";

	// 設定公版圖片來源
	//final Integer[] mImageIds = { R.drawable.ts,R.drawable.a,R.drawable.b, R.drawable.barack_obama};
	Integer[] mImageIds;
	private boolean flag = false;
	private LinearLayout layout ;
	private Gallery gallery ;
	
	private static String TAG = "Camera_Test";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/* 使應用程式全螢幕執行，不使用title bar */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		Log.i(TAG,"This is onCreate");
		String[] test_string =getResources().getStringArray(R.array.test);
		if(test_string==null)
		{
			Log.e(TAG,"test_string IS NULL");
			finish();
		}
		mImageIds = new Integer[test_string.length];
		Log.i(TAG,"test_string is : "+test_string[0]);
		Log.i(TAG,"mImageIds length is : "+mImageIds.length);
		for(int i=0; i<test_string.length; i++)
		{
			mImageIds[i]=getResources().getIdentifier(test_string[i], "drawable", "com.thlight.camera");
			Log.i(TAG,"mImageIds id is : "+mImageIds[i]);
		}
		

		num = mImageIds[0];//預設公版
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
		FindView();
		Button_Action();
		
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
	
	}
	private void FindView()
	{
		layout = (LinearLayout)findViewById(R.id.linear);
		/* 延伸學習 */
		// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		gallery = (Gallery)findViewById(R.id.gallery_test);
		mImageView01 = (ImageView) findViewById(R.id.imageView1);
		
		
		/* 以SurfaceView作為相機Preview之用 */
		mSurfaceView01 = (SurfaceView) findViewById(R.id.mSurfaceView1);
		
		mBtn_take_picture = (Button) findViewById(R.id.take_picture);
		mBtn_photo_from_sd = (Button) findViewById(R.id.photo_from_sd);
		mBtn_public = (Button) findViewById(R.id.public_photo);
		
	}
	
	protected void onResume()
	{
		super.onResume();
		Log.i(TAG,"This is onResume()");
	
	}
	
	private void Button_Action() {
		
		
		mBtn_public.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
				if(!flag)
				{
					
		    //		Gallery gallery = new Gallery(Camera_Test.this);
					gallery.setUnselectedAlpha(0.7f);
					gallery.setBackgroundColor(Color.argb(0, 0, 255, 0));
				//	gallery.setSelected(true);
					//gallery.setId(R.id.gallery1);
					ImageAdapter imageAdapter = new ImageAdapter(Camera_Test.this);
					if(mImageIds==null)
					{	Log.e(TAG,"mImageIds is null");
						finish();
					}
					//rotate(mImageIds[0]);
					// 設定圖片的位置
					imageAdapter.setmImageIds(mImageIds);
					// 圖片高度
					imageAdapter.setHeight(120);
					// 圖片寬度
					imageAdapter.setWidth(120);
					gallery.setAdapter(imageAdapter);
					gallery.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(@SuppressWarnings("rawtypes") AdapterView parent, View view,
								int position, long id) {
							num= mImageIds[position];
							mImageView01.setImageResource(mImageIds[position]);
					
						}
					});
					layout.setVisibility(0);
					flag=true;
					mBtn_public.setText("回收公版");
				}
				else
					{
						mBtn_public.setText("公版");
						layout.setVisibility(4);
						flag=false;
						/*setVisibility:
						 * 
						 * 常量值为0，意思是可见的 
						 * 常量值为4，意思是不可见的 
						 * 常量值为8，意思是不可见的，而且不占用布局空间*/
					}
					
			

			}
				
		});
		
		/* 拍照 */
		mBtn_take_picture.setOnClickListener(new Button.OnClickListener() {
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
		mBtn_photo_from_sd.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				/* 自訂初始化開啟相機函數 */
		
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				
				startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
				
				resetCamera();
			}
		});
		
	}
	/*---Start---從另一個Intent抓圖片路徑檔 with startActivityForResult */
	//UPDATED
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                //OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                //MEDIA GALLERY
                image_path = getPath(selectedImageUri);

                //DEBUG PURPOSE - you can delete this if you want
                if(image_path!=null)
                    System.out.println(image_path);
                else System.out.println("selectedImagePath is null");
                if(filemanagerstring!=null)
                    System.out.println(filemanagerstring);
                else System.out.println("filemanagerstring is null");

                //NOW WE HAVE OUR WANTED STRING
                if(image_path!=null)
                    System.out.println("selectedImagePath is the right one for you!");
                else
                    System.out.println("filemanagerstring is the right one for you!");
               
                
        			Intent intent = new Intent();
        	        intent.setClass(Camera_Test.this,Temp_Photo.class);
        	        Bundle bundle = new Bundle();
        	      
        	        	
        	        Log.i(TAG,"public_photo is "+num);
        	        bundle.putInt("public_photo", num);
        	        bundle.putString("image_path", image_path);
        	        intent.putExtras(bundle);
        	       	startActivity(intent);
        	      
        		
            }
        }
        
       	
    }

    //UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }
    /*---END---從另一個Intent抓圖片路徑檔 with startActivityForResult */
    
	/*旋轉圖片九十度*/
	private Bitmap rotate(Bitmap images) 
	{
		final int width_ori = images.getWidth();
		final int height_ori = images.getHeight();

		float width = width_ori;
		float height = height_ori;

		Matrix matrix = new Matrix();
		matrix.postScale(width, height);
		matrix.setRotate(90);

		Bitmap resized_Bmp = Bitmap.createBitmap(images, 0, 0, width_ori,
				height_ori, matrix, true);
	
		return resized_Bmp;
	}
	/* 自訂初始相機函數 */
	private void initCamera() {
		Log.i(TAG,"initCamera  and bIfPreview is ..."+bIfPreview);
		if (!bIfPreview) 
		{
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
				Log.i(TAG,"Camera open...");
				mCamera01 = Camera.open();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}

		if (mCamera01 != null && !bIfPreview) {
			try {
				mSurfaceHolder01.setKeepScreenOn(true);
				mCamera01.setPreviewDisplay(mSurfaceHolder01);
				/* 建立Camera.Parameters物件 */
				Camera.Parameters parameters = mCamera01.getParameters();
				//parameters.set("jpeg-quality", 50);
				//parameters.set("orientation", "landscape");
				/*
				 * for following code 2.2 is useable,but 2.0 is down
				 */
				parameters.set("orientation", "portrait");
				mCamera01.setDisplayOrientation(90);
			/*	if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				    // This is an undocumented although widely known feature
				    parameters.set("orientation", "portrait");
				    // For Android 2.2 and above  (加在這)
				    mCamera01.setDisplayOrientation(90);
				    // Uncomment for Android 2.0 and above
				    // parameters.setRotation(90);
				    // parameters.set("rotation", "0");
				  
				   }
				else {
				    // This is an undocumented although widely known feature
				    parameters.set("orientation", "landscape");
				    // For Android 2.2 and above  (加在這)
				    mCamera01.setDisplayOrientation(0);
				    camera_state=true;
				    // Uncomment for Android 2.0 and above
				   // parameters.setRotation(0);
				   }
				*/
				
				
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
						parameters.setPictureSize(512, 384);//weird...
						// parameters.setPictureSize(213, 350);
						/* 將Camera.Parameters設定予Camera */
						mCamera01.setParameters(parameters);
						/* setPreviewDisplay唯一的參數為SurfaceHolder */
						mCamera01.setPreviewDisplay(mSurfaceHolder01);
						/* 立即執行Preview */
						mCamera01.startPreview();
						bIfPreview = true;
						Log.i(TAG,"In here?");
						
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

			Bitmap scaled_bm = rotate(Bitmap.createScaledBitmap(bm, 960, 640,true));
			
			bm.recycle();
			
			try {
				
				
			
				/* 將拍照下來且儲存完畢的圖檔，顯示出來 */
				// ImageView01.setImageBitmap(bm);
				// 以下存入SDCard

				File myCaptureFile = new File(
						"/sdcard/CameraTest/camera_snap.jpg");
				
				BufferedOutputStream bos;
				
				bos = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				
				
				scaled_bm.compress(CompressFormat.JPEG, 100, bos);
				bos.flush();
				bIfPreview=false;
				Bundle bundle = new Bundle();
	
				/* 結束OutputStream */
				bos.close();

				Intent intent = new Intent();
    	        intent.setClass(Camera_Test.this,Temp_Photo.class);
    	        Log.i(TAG,"public_photo is "+num);
    	        bundle.putInt("public_photo", num);
    	        bundle.putString("image_path", strCaptureFilePath);
    	        intent.putExtras(bundle);
    	       	startActivity(intent);
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG,"From picture taken : FileNotFoundException :" + e.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG,"From picture taken : IOException: " + e.toString());
			}
			
		}
	};
	/*以下為攝像相關函式*/
	


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
		 Log.i(TAG, "Surface Changed");
		 surfaceholder.setKeepScreenOn(true);
  
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceholder) {
		// TODO Auto-generated method stub
		 Log.i(TAG, "Surface surfaceCreated");
		 initCamera();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
		// TODO Auto-generated method stub
		/* 當Surface不存在，需要刪除圖檔 */
		Log.i(TAG, "Surface Destroyed");
		try {
			mCamera01.stopPreview();
			mCamera01.release();
			mCamera01 = null;
			bIfPreview= false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mCamera01!=null)
			mCamera01.release();
		
	}

}
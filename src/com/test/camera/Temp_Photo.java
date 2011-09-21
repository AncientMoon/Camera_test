package com.test.camera;



import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class Temp_Photo extends Activity
{
	private ImageView mImageView01;
	private ImageView TempPhoto;
	private String TAG="Camera_Test";
//	private Integer num;
	private Button mButton01,mButton02,mButton03;
	private Integer public_photo;
	private String image_path;
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/* 使應用程式全螢幕執行，不使用title bar */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.temp_photo);
		FindView();
		Button_Action();
		Bundle bundle = this.getIntent().getExtras();
		image_path = bundle.getString("image_path");
		public_photo =  bundle.getInt("public_photo");
		Log.i(TAG,"From Temp_Photo image_path is : "+image_path);
		Log.i(TAG,"From Temp_Photo public_photo is : "+public_photo);
		TempPhoto.setImageURI(Uri.parse(image_path));
		mImageView01.setImageResource(public_photo);
		
	}
	private void FindView()
	{
		mImageView01 = (ImageView)findViewById(R.id.overlap_temp_photo);
		TempPhoto = (ImageView)findViewById(R.id.Temp_Photo_view);
		mButton01 = (Button) findViewById(R.id.share);
		mButton02 = (Button) findViewById(R.id.save);
		mButton03 = (Button) findViewById(R.id.delete);
		
		
	}
	private void Button_Action() {
			
		/* 開啟相機及Preview */
		mButton01.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Log.i(TAG,"TEST_One");
				// Log.e(TAG,"No response");
				// TODO Auto-generated method stub
				/* 自訂初始化開啟相機函數 */
				//initCamera();
			}
		});

		/* 拍照 */
		mButton02.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				/* 當記憶卡存在才允許拍照，儲存暫存影像檔 */
				if (checkSDCard()) {
					/* 自訂拍照函數 */
					savePicture();
				} else {
					/* 記憶卡不存在顯示提示 */
					Toast.makeText(Temp_Photo.this, R.string.str_err_nosd,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		mButton03.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				finish();
				
			}
		});
		
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
	private void savePicture()
	{
		Bitmap src =  Bitmap.createScaledBitmap(BitmapFactory.decodeFile(image_path), 640, 960, true);
		Bitmap mBitmap = null;
		Bitmap srcThree = createBitmap(src, mBitmap);// 建立浮水印於已拍下的畫面上
		File myCaptureFile = new File(
				"/sdcard/CameraTest/combine_snap.jpg");
		if (myCaptureFile.exists())
			myCaptureFile.delete();
		if (myCaptureFile == null)
			Log.e(TAG, "fail to open new file");
		else
			Log.e(TAG, myCaptureFile.toString());
		
	
		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			srcThree.compress(CompressFormat.JPEG, 100, bos);
			bos.flush();

			/* 結束OutputStream */
			bos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
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
//			if(camera_state)
//				scaled_watermark = rotate(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),num,options), h, w,true));
//			else
				scaled_watermark = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),public_photo,options), w, h,true);

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
}

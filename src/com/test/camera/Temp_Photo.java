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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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
	private Integer num;
	private Button mButton01,mButton02,mButton03,mButton04;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temp_photo);
		FindView();
		Button_Action();
		Bundle bundle = this.getIntent().getExtras();
		String image_path = bundle.getString("image_path");
		Log.i(TAG,"image_path is : "+image_path);
		TempPhoto.setImageURI(Uri.parse(image_path));
		
		
	}
	private void FindView()
	{
		mImageView01 = (ImageView)findViewById(R.id.overlap_temp_photo);
		TempPhoto = (ImageView)findViewById(R.id.Temp_Photo_view);
		mButton01 = (Button) findViewById(R.id.save);
		mButton02 = (Button) findViewById(R.id.save);
		mButton03 = (Button) findViewById(R.id.public_photo_Fromtemp);
		mButton04 = (Button) findViewById(R.id.delete);
		
		
	}
	private void Button_Action() {
			
			
		mButton03.setOnClickListener(new Button.OnClickListener() {
	
				@Override
				public void onClick(View v) {
					
					
					LinearLayout layout = new LinearLayout(Temp_Photo.this);
		    		layout.setOrientation(LinearLayout.VERTICAL);
		    		//layout.setWeightSum(50);
		    		
		    		layout.setGravity(Gravity.CENTER_HORIZONTAL);
		    //		layout.setPadding(10, 20, 10, 20);
		    		layout.setBackgroundColor(Color.argb(0, 0, 255, 0));
		   // 		Gallery gallery = (Gallery)findViewById(R.id.gallery1);
		    		 Gallery gallery = new Gallery(Temp_Photo.this);
					gallery.setUnselectedAlpha(0.5f);
					gallery.setBackgroundColor(Color.argb(0, 0, 255, 0));
				//	gallery.setSelected(true);
					//gallery.setId(R.id.gallery1);
					ImageAdapter imageAdapter = new ImageAdapter(Temp_Photo.this);
					// 設定圖片來源
					final Integer[] mImageIds = { R.drawable.a,R.drawable.b, R.drawable.barack_obama,R.drawable.ts};
					//rotate(mImageIds[0]);
					// 設定圖片的位置
					imageAdapter.setmImageIds(mImageIds);
					// 圖片高度
					imageAdapter.setHeight(100);
					// 圖片寬度
					imageAdapter.setWidth(110);
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
		    		Dialog dialog = new AlertDialog.Builder(Temp_Photo.this)
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
		mButton04.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				
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
	private void savePicture(){
		
	}
}

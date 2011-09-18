package com.test.camera;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class GetFile extends ListActivity
{
	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = "/";
	private TextView mPath;
	private Context context;
	private String TAG = "Camera_Test";
	private String image_path  = null;
	@Override
	public void onCreate(Bundle icicle)
	{
	    super.onCreate(icicle);
	//	context = ct;
		//mPath = (TextView)findViewById;
		mPath = new TextView(this);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		//layout.setWeightSum(50);
//		layout.setPadding(10, 20, 10, 20);
//		layout.setGravity(Gravity.CENTER_HORIZONTAL);
//		layout.setPadding(10, 20, 10, 20);
		layout.setBackgroundColor(Color.argb(0, 0, 255, 0));
		layout.addView(mPath);
		getFileDir(rootPath);
	}
	  /* 取得檔案架構的method */
	  private void getFileDir(String filePath)
	  {
	    /* 設定目前所在路徑 */
	    mPath.setText(filePath);
	    
	    items=new ArrayList<String>();
	    paths=new ArrayList<String>();  
	    File f=new File(filePath);  
	    File[] files=f.listFiles();

	    if(!filePath.equals(rootPath))
	    {
	      /* 第一筆設定為[回到根目錄] */
	      items.add("Back to "+rootPath);
	      paths.add(rootPath);
	      /* 第二筆設定為[回上層] */
	      items.add("Back to ../");
	      paths.add(f.getParent());
	    }
	    /* 將所有檔案加入ArrayList中 */
	    for(int i=0;i<files.length;i++)
	    {
	      File file=files[i];
	      items.add(file.getName());
	      paths.add(file.getPath());
	    }
	    
	    /* 宣告一ArrayAdapter，使用file_row這個Layout，
	                  並將Adapter設定給此ListActivity */
	    ArrayAdapter<String> fileList = 
	       new ArrayAdapter<String>(this,R.layout.file_row, items);
	    setListAdapter(fileList);
	  }
	  
	  /* 設定ListItem被按下時要做的動作 */
	  @Override
	  protected void onListItemClick(ListView l,View v,int position,long id)
	  {
	    File file = new File(paths.get(position));
	    image_path = file.getPath();
	    if(file.canRead())
	    {
	      if (file.isDirectory())
	      {
	        /* 如果是資料夾就再進去撈一次 */
	        getFileDir(paths.get(position));
	      }
	      else
	      {
	        /* 如果是檔案，則跳出AlertDialog */
	        new AlertDialog.Builder(this)
	            .setTitle("Message")
	            .setMessage("["+file.getName()+"] is File!")
	            .setPositiveButton("OK",
	              new DialogInterface.OnClickListener()
	              {
	                
					public void onClick(DialogInterface dialog,int which)
	                {
	                	//ImageView mImageView01 ;
	                	//mImageView01= (ImageView)findViewById(R.id.public_photo);
	                	//mImageView01.setImageURI(Uri.parse(image_path));
	                	Log.i(TAG,image_path);
	                //	SharedPreferences settings = getSharedPreferences("Camera", 0);
	                	SharedPreferences settings = getSharedPreferences ("Camera", 0);
	            	    SharedPreferences.Editor PE = settings.edit();
	            	    PE.putString("image_path", image_path);
	            	    PE.commit();
	                	finish();
	                	//GetFile.f;
	                }
	              }).show();         
	      }
	    }
	    else
	    {
	      /* 跳出AlertDialog顯示權限不足 */
	      new AlertDialog.Builder(this)
	          .setTitle("Message")
	          .setMessage("權限不足!")
	          .setPositiveButton("OK",
	            new DialogInterface.OnClickListener()
	            {
	              public void onClick(DialogInterface dialog,int which)
	              {
	              }
	            }).show();     
	    }
	    
	  }
	  
	
}

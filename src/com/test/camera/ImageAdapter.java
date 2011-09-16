package com.test.camera;

	import android.content.Context;
import android.graphics.Color;
import android.util.Log;
	import android.view.View;
	import android.view.ViewGroup;
	import android.widget.BaseAdapter;
	import android.widget.Gallery;
import android.widget.ImageView;
	 
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    private Integer width;
	    private Integer height;
	    private Integer[] mImageIds;
	 private String TAG="Camera_Test";
	    public ImageAdapter(Context c) {
	        mContext = c;
	    }
	 
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	Log.e(TAG,"Best guest");
	        ImageView imageView = new ImageView(mContext);
	        Log.e(TAG,"Best guest2");
	        //設定圖片來源
	        imageView.setImageResource(mImageIds[position]);
	        imageView.setBackgroundColor(Color.argb(0, 0, 255, 0));
	        //設定圖片的寬、高
	        imageView.setLayoutParams(new Gallery.LayoutParams(width, height));
	        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
	        return imageView;
	    }
	 
	    public Integer getHeight() {
	        return height;
	    }
	 
	    public void setHeight(Integer height) {
	        this.height = height;
	    }
	 
	    public Integer[] getmImageIds() {
	        return mImageIds;
	    }
	 
	    public void setmImageIds(Integer[] mImageIds) {
	        this.mImageIds = mImageIds;
	    }
	 
	    public Integer getWidth() {
	        return width;
	    }
	 
	    public void setWidth(Integer width) {
	        this.width = width;
	    }
	 
	    public int getCount() {
	        return mImageIds.length;
	    }
	 
	    public Object getItem(int position) {
	        return position;
	    }
	 
	    public long getItemId(int position) {
	        return position;
	    }
	}


package com.karthik.mymovie.mymovie;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Kary on 13-Feb-16.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    public ImageAdapter(Context c){
        mContext = c;
    }

    @Override
    public int getCount() {
        //TODO update correct count
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else{
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(R.drawable.donut);
        return imageView;
    }
}

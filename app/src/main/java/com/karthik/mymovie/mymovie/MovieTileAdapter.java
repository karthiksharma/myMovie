package com.karthik.mymovie.mymovie;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Kary on 13-Feb-16.
 */
public class MovieTileAdapter extends ArrayAdapter<MovieTile>{

    private static final String LOG_TAG = MovieTileAdapter.class.getSimpleName();
    private Context mContext;
    public MovieTileAdapter(Context context, int resource, List<MovieTile> movieList) {
        super(context, 0, movieList);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MovieTile movieTile = getItem(position);
        ImageView imageView = (ImageView) convertView;
        if(imageView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else{
            imageView = (ImageView)convertView;
        }
        imageView.setImageResource(R.drawable.donut);

        Log.v(LOG_TAG, "Movie url is " + movieTile.url);

        Picasso.with(getContext()).load(movieTile.url).into(imageView);

        return imageView;
    }
}

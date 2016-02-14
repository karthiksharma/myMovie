package com.karthik.mymovie.mymovie;

import android.content.Context;
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
            imageView = new ImageView(getContext());
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else{
            imageView = (ImageView)convertView;
        }
        /*final String BASE_URL = "http://image.tmdb.org/t/p/";
        final String IMAGE_RESOLUTION = "w185";
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(IMAGE_RESOLUTION)
                .appendPath(movieTile.getPosterPath())
                .build();*/
        String url = "http://image.tmdb.org/t/p/w185/"+movieTile.getPosterPath();
//        Log.v(LOG_TAG, "Image url is " + url);
        Picasso.with(getContext()).load(url).into(imageView);
        return imageView;
    }
}

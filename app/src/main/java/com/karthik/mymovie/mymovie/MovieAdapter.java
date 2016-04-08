package com.karthik.mymovie.mymovie;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Karry on 4/4/2016.
 */
public class MovieAdapter extends CursorAdapter {

    public MovieAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_tile, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.poster_image);

        final String BASE_URL = context.getString(R.string.MOVIE_IMAGE_BASE_URL);
        final String IMAGE_RESOLUTION = context.getString(R.string.IMAGE_DEFAULT_RESOLUTION);

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(IMAGE_RESOLUTION)
                .appendEncodedPath(cursor.getString(2))
                .build();

//        Log.v(LOG_TAG, "Image url is " + uri.toString());

        Picasso.with(context)
                .load(uri)
                .placeholder(R.drawable.donut)
                .error(R.drawable.donut)
                .fit()
                .into(imageView);

    }
}

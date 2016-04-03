package com.karthik.mymovie.mymovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by Karry on 4/3/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 1;

    public static final String MOVIE_TABLE = "movie";

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_NAME = "movie_name";
    public static final String POSTER_PATH = "poster_path";
    public static final String MOVIE_DESC = "movie_desc";
    public static final String MOVIE_RATING = "movie_rating";
    public static final String RELEASE_DATE = "release_date";

    public static final String CONTENT_AUTHORITY = "com.karthik.mymovie.mymovie";
    public static final Uri BASE_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIE_TABLE = "CREATE TABLE " + MOVIE_TABLE + "("
                + MOVIE_ID +" TEXT PRIMARY KEY,"
                + MOVIE_NAME + " TEXT,"
                + POSTER_PATH + " TEXT,"
                + MOVIE_DESC + " TEXT,"
                + MOVIE_RATING + " TEXT,"
                + RELEASE_DATE + " TEXT"+")";
        db.execSQL(CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MOVIE_TABLE);
        onCreate(db);
    }

    public static Uri buildMovieUri(String movieId){
        return BASE_URI.buildUpon().appendPath(movieId).build();
    }
}

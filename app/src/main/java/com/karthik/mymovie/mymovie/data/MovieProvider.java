package com.karthik.mymovie.mymovie.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Karry on 4/3/2016.
 */
public class MovieProvider extends ContentProvider {

    private DatabaseHelper mDbHelper;
    public static final Uri CONTENT_URI = Uri.parse(DatabaseHelper.BASE_URI + "/" + DatabaseHelper.PATH_MOVIE);

    @Override
    public boolean onCreate() {
        mDbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = mDbHelper.getReadableDatabase().query(DatabaseHelper.MOVIE_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long movieId = db.insert(DatabaseHelper.MOVIE_TABLE, null, values);
        if(movieId > 0) {
            return Uri.parse(DatabaseHelper.BASE_URI + "/" + movieId);
        }
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        int count = 0;
        try {
            for(ContentValues cv : values){
                long movieId = db.insert(DatabaseHelper.MOVIE_TABLE, null, cv);
                if(movieId > 0)
                    count++;
            }
            db.setTransactionSuccessful();
        }
        catch (Exception e){
            return super.bulkInsert(uri, values);
        }
        finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowDeleted = db.delete(DatabaseHelper.MOVIE_TABLE, selection, selectionArgs);
        if(rowDeleted != 1){
            Log.e("MovieProvider", "Deleted record for Uri: " +uri+" . Total deleted = " + rowDeleted);
        }
        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        rowsUpdated = db.update(DatabaseHelper.MOVIE_TABLE, values, selection, selectionArgs);
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
}

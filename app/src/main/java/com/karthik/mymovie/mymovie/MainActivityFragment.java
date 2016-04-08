package com.karthik.mymovie.mymovie;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.karthik.mymovie.mymovie.data.DatabaseHelper;
import com.karthik.mymovie.mymovie.data.MovieProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivityFragment extends Fragment {

    private MovieTileAdapter mMovieAdapter;
    private ArrayList<MovieTile> mMovieList;

    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movieList", mMovieList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mMovieList = savedInstanceState.getParcelableArrayList("movieList");
        }
    }

    private void discoverMovieApi(String sortBy){
        new FetchMovieDetail().execute(sortBy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new MovieTileAdapter(getContext(),0, new ArrayList<MovieTile>());
        GridView gridView = (GridView) rootView;
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MovieTile movieItem = mMovieAdapter.getItem(position);
                if(MainActivity.isTwoPane()){
                    getFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, new DetailActivityFragment(), movieItem.getMovieId())
                    .commit();
                }
                else
                {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, movieItem.getMovieId());
                    startActivity(intent);
                }
            }
        });
        return gridView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = pref.getString(getString(R.string.sort_criteria_key), getString(R.string.default_sort_criteria));

        if(sortBy != null && sortBy.equalsIgnoreCase(getString(R.string.offline_sort_criteria))){
            displayFavorite(sortBy);
        }else {
            discoverMovieApi(sortBy);
        }
    }

    private void displayFavorite(String sortBy){
        ArrayList<MovieTile> movieList  = new ArrayList<>();
        Cursor cursor = getContext().getContentResolver().query(MovieProvider.CONTENT_URI, null, null, null,null);
        try {
            if (cursor.moveToFirst()){
                do {
                    MovieTile movieTile = new MovieTile(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MOVIE_ID)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.POSTER_PATH)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.MOVIE_NAME)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.MOVIE_DESC)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.MOVIE_RATING)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.RELEASE_DATE)));
                    movieList.add(movieTile);
                }while (cursor.moveToNext());
                mMovieList = movieList;
                mMovieAdapter.clear();
                mMovieAdapter.addAll(movieList);
            }else {
                mMovieList = new ArrayList<>();
                mMovieAdapter.clear();
                Toast.makeText(getContext(), "No Favorite Movies", Toast.LENGTH_SHORT).show();
            }
        }
        finally {
            cursor.close();
        }
    }

    class FetchMovieDetail extends AsyncTask<String, Void, ArrayList<MovieTile>>{

        private final String LOG_TAG = FetchMovieDetail.class.getSimpleName();

        @Override
        protected ArrayList<MovieTile> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String discoverMovieJson = null;
            ArrayList<MovieTile> movieList = null;
            try{
                //"http://api.themoviedb.org/3/movie/";
                final String MOVIE_API_BASE_URL = getString(R.string.MOVIE_DISCOVER_API_BASE_URL);
//                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";


                Uri baseUri = Uri.parse(MOVIE_API_BASE_URL).buildUpon()
//                        .appendQueryParameter(SORT_BY, params[0])
                        .appendPath(params[0])
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIE_API_KEY )
                        .build();

                URL url = new URL(baseUri.toString());

                /*Log.v("MAF : ", url.toString());*/

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                discoverMovieJson = buffer.toString();
                movieList = parseDiscoverMovieJson(discoverMovieJson);

            }
            catch (IOException ioEx){
                Log.e(LOG_TAG, "IO Exception occured " + ioEx);
                return null;
            }
            catch(Exception e){
                Log.e(LOG_TAG, "Exception occured while reading API " + e);
                return null;
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return movieList;
        }

        private ArrayList<MovieTile> parseDiscoverMovieJson(String discoverMovieJson) throws JSONException{
            JSONObject forecastJson = new JSONObject(discoverMovieJson);
            JSONArray jsonArr = forecastJson.getJSONArray("results");
            ArrayList<MovieTile> movieList = new ArrayList<MovieTile>(jsonArr.length());
            for(int i =0 ; i <jsonArr.length(); i++){
                JSONObject movieEle = jsonArr.getJSONObject(i);
                movieList.add(new MovieTile(movieEle.getString("id"),movieEle.getString("poster_path")));
                movieList.get(i).setMovieName(movieEle.getString("original_title"));
//                Log.v(LOG_TAG, movieList[i].toString());
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieTile> movies) {
            if(movies != null) {
                mMovieList = movies;
                mMovieAdapter.clear();
                mMovieAdapter.addAll(movies);
            }else{
                Toast.makeText(getContext(), "Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

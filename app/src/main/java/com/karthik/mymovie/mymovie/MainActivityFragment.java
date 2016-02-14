package com.karthik.mymovie.mymovie;


import android.content.Intent;
import android.content.SharedPreferences;
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

    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = pref.getString(getString(R.string.sort_criteria_key), getString(R.string.default_sort_criteria));
        discoverMovieApi(sortBy);
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
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movieItem.getMovieId());
                startActivity(intent);
            }
        });
        return gridView;
    }
    class FetchMovieDetail extends AsyncTask<String, Void, MovieTile[]>{

        private final String LOG_TAG = FetchMovieDetail.class.getSimpleName();

        @Override
        protected MovieTile[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String discoverMovieJson = null;
            MovieTile[] movieList = null;
            try{
                final String MOVIE_API_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";

                Uri baseUri = Uri.parse(MOVIE_API_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(API_KEY, "b4f2c25145dbe8726f15084197348819")
                        .build();
                URL url = new URL(baseUri.toString());

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

        private MovieTile[] parseDiscoverMovieJson(String discoverMovieJson) throws JSONException{
            JSONObject forecastJson = new JSONObject(discoverMovieJson);
            JSONArray jsonArr = forecastJson.getJSONArray("results");
            MovieTile[] movieList = new MovieTile[jsonArr.length()];
            for(int i =0 ; i <jsonArr.length(); i++){
                JSONObject movieEle = jsonArr.getJSONObject(i);
                movieList[i] = new MovieTile(movieEle.getString("id"),movieEle.getString("poster_path"));
                movieList[i].setMovieName(movieEle.getString("original_title"));
//                Log.v(LOG_TAG, movieList[i].toString());
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(MovieTile[] movies) {
            mMovieAdapter.clear();
            mMovieAdapter.addAll(movies);
        }
    }

}

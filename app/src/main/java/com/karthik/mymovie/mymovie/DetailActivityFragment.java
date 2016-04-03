package com.karthik.mymovie.mymovie;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.karthik.mymovie.mymovie.data.DatabaseHelper;
import com.karthik.mymovie.mymovie.data.MovieProvider;
import com.squareup.picasso.Picasso;

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
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private View rootView;
    private MovieTile mMovie;
    private ArrayAdapter<String> mTrailerAdapter;
    private ArrayAdapter<String> mReviewAdapter;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mMovie = savedInstanceState.getParcelable("currMovie");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("currMovie", mMovie);
    }

    private void fetchMovieDetailsFromApi(String movieId){
        new FetchMovieDetail().execute(movieId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String movieId;

        Intent intent = getActivity().getIntent();
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTrailerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        mReviewAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new ArrayList<String>());

        final ListView trailerListView = (ListView) rootView.findViewById(R.id.trailer_list);
        trailerListView.setAdapter(mTrailerAdapter);

        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = (String) trailerListView.getItemAtPosition(position);
                if(key != null){
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.youtube.com/watch?v="+key));
                    startActivity(intent);
                }
            }
        });

        ListView reviewListView = (ListView) rootView.findViewById(R.id.review_list);
        reviewListView.setAdapter(mReviewAdapter);

        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            movieId = intent.getStringExtra(Intent.EXTRA_TEXT);
            fetchMovieDetailsFromApi(movieId);
        }
        return rootView;
    }

    class FetchMovieDetail extends AsyncTask<String, Void, MovieTile>{

        private final String LOG_TAG = FetchMovieDetail.class.getSimpleName();

        @Override
        protected MovieTile doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieDetailJson = null;
            String trailerJson = null;
            String reviewJson = null;
            MovieTile currentMovie = null;
            try{
                final String MOVIE_API_BASE_URL = getString(R.string.MOVIE_DETAIL_API_BASE_URL);
                final String API_KEY = "api_key";

                Uri baseUri = Uri.parse(MOVIE_API_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIE_API_KEY)
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
                movieDetailJson = buffer.toString();
//                Log.v(LOG_TAG, " Movie Detail JSON is " + movieDetailJson);
                currentMovie = parseMovieDetailJson(movieDetailJson);

                trailerJson = movieDetailList(currentMovie.getMovieId(), "videos");
                currentMovie.setTrailerList(parseTrailerJson(trailerJson));

                reviewJson = movieDetailList(currentMovie.getMovieId(),"reviews");
                currentMovie.setReviewList(parseReviewJson(reviewJson));
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
            return currentMovie;
        }

        private List<String> parseReviewJson(String reviewJson) throws JSONException {
            JSONObject movieJsonObj = new JSONObject(reviewJson);
            JSONArray listArrJson = movieJsonObj.getJSONArray("results");
            List<String> reviewList = new ArrayList<>(listArrJson.length());
            for (int i = 0 ; i < listArrJson.length() ; i++){
                reviewList.add(listArrJson.getJSONObject(i).getString("content"));
            }
            return reviewList;
        }

        private List<String> parseTrailerJson(String trailerJson) throws JSONException {
            JSONObject movieJsonObj = new JSONObject(trailerJson);
            JSONArray listArrJson = movieJsonObj.getJSONArray("results");
            List<String> trailerList = new ArrayList<>(listArrJson.length());
            for (int i = 0 ; i < listArrJson.length() ; i++){
                trailerList.add(listArrJson.getJSONObject(i).getString("key"));
            }
            return trailerList;
        }

        //Method to fetch JSON of Trailer and REview list from API call
        private String movieDetailList(String movieId, String listType)throws IOException{

            final String MOVIE_API_BASE_URL = getString(R.string.MOVIE_DETAIL_API_BASE_URL);
            final String API_KEY = "api_key";

            Uri baseUri = Uri.parse(MOVIE_API_BASE_URL).buildUpon()
                    .appendPath(movieId)
                    .appendPath(listType)
                    .appendQueryParameter(API_KEY, BuildConfig.MOVIE_API_KEY)
                    .build();
            URL url = new URL(baseUri.toString());

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            return buffer.toString();
        }

        private MovieTile parseMovieDetailJson(String movieDetailJson)throws JSONException{

            JSONObject movieJsonObj = new JSONObject(movieDetailJson);

            MovieTile currMovie = new MovieTile();

            //Set Movie Object properties
            currMovie.setMovieId(movieJsonObj.getString("id"));
            currMovie.setMovieName(movieJsonObj.getString("original_title"));
            currMovie.setPosterPath(movieJsonObj.getString("poster_path"));
            currMovie.setMovieDesc(movieJsonObj.getString("overview"));
            currMovie.setUserRating(movieJsonObj.getDouble("vote_average") + "");
            currMovie.setReleaseDate(movieJsonObj.getString("release_date"));

            return currMovie;
        }

        @Override
        protected void onPostExecute(final MovieTile movieTile) {
            final String movieId = movieTile.getMovieId();

            final ContentValues values = new ContentValues();
            values.put(DatabaseHelper.MOVIE_ID, movieTile.getMovieId());
            values.put(DatabaseHelper.MOVIE_NAME, movieTile.getMovieName());
            values.put(DatabaseHelper.POSTER_PATH, movieTile.getPosterPath());
            values.put(DatabaseHelper.MOVIE_DESC, movieTile.getMovieDesc());
            values.put(DatabaseHelper.MOVIE_RATING, movieTile.getUserRating());
            values.put(DatabaseHelper.RELEASE_DATE, movieTile.getReleaseDate());

            ImageView posterImageView = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
            String url = "http://image.tmdb.org/t/p/w185/"+movieTile.getPosterPath();
            Picasso.with(getContext()).load(url).into(posterImageView);

            TextView textView = (TextView)rootView.findViewById(R.id.movie_title);

            textView.setText("Movie Title : " + movieTile.getMovieName());

            textView = (TextView)rootView.findViewById(R.id.movie_release_date);
            textView.setText("Movie Release Date : " + movieTile.getReleaseDate());

            textView = (TextView)rootView.findViewById(R.id.movie_vote_average);
            textView.setText("Average Rating is : " + movieTile.getUserRating() + " Stars");

            textView = (TextView)rootView.findViewById(R.id.movie_overview);
            textView.setText("Movie Plot overview : " + movieTile.getMovieDesc());

            FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor cursor = getContext().getContentResolver().query(MovieProvider.CONTENT_URI,
                            null,
                            DatabaseHelper.MOVIE_ID + " = ?",
                            new String[]{movieId},
                            null);
                    try {
                        if (cursor.moveToFirst()) {
                            Snackbar.make(view, getResources().getString(R.string.already_favorite), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            getContext().getContentResolver().insert(MovieProvider.CONTENT_URI, values);
                            Snackbar.make(view, getResources().getString(R.string.add_to_favourites), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } finally {
                        cursor.close();
                    }
                }
            });
            mMovie = movieTile;

            mReviewAdapter.clear();
            mReviewAdapter.addAll(movieTile.getReviewList());

            mTrailerAdapter.clear();
            mTrailerAdapter.addAll(movieTile.getTrailerList());
        }

    }

}

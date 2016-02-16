package com.karthik.mymovie.mymovie;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private View rootView;

    public DetailActivityFragment() {
    }

    private void fetchMovieDetailsFromApi(String movieId){
        new FetchMovieDetail().execute(movieId);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String movieId;
        Intent intent = getActivity().getIntent();
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
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

        private MovieTile parseMovieDetailJson(String movieDetailJson)throws JSONException{

            JSONObject movieJsonObj = new JSONObject(movieDetailJson);

            MovieTile currMovie = new MovieTile();

            //Set Movie Object properties
            currMovie.setMovieName(movieJsonObj.getString("original_title"));
            currMovie.setPosterPath(movieJsonObj.getString("poster_path"));
            currMovie.setMovieDesc(movieJsonObj.getString("overview"));
            currMovie.setUserRating(movieJsonObj.getDouble("vote_average") + "");
            currMovie.setReleaseDate(movieJsonObj.getString("release_date"));

            return currMovie;
        }

        @Override
        protected void onPostExecute(MovieTile movieTile) {
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

        }

    }

}

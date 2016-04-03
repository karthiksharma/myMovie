package com.karthik.mymovie.mymovie;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Kary on 13-Feb-16.
 */
public class MovieTile implements Parcelable{

//    private String url;
    private String movieId;
    private String posterPath;
    private String movieName;
    private String movieDesc;
    private String userRating;
    private String releaseDate;
    private List<String> trailerList;
    private List<String> reviewList;

    public MovieTile() {
    }

    protected MovieTile(Parcel in) {
        movieId = in.readString();
        posterPath = in.readString();
        movieName = in.readString();
        movieDesc = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
    }

    public static final Creator<MovieTile> CREATOR = new Creator<MovieTile>() {
        @Override
        public MovieTile createFromParcel(Parcel in) {
            return new MovieTile(in);
        }

        @Override
        public MovieTile[] newArray(int size) {
            return new MovieTile[size];
        }
    };

    public String getMovieDesc() {
        return movieDesc;
    }

    public void setMovieDesc(String movieDesc) {
        this.movieDesc = movieDesc;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public MovieTile(String movieId, String posterPath, String movieName, String movieDesc, String userRating, String releaseDate) {
        this.movieId = movieId;
        this.posterPath = posterPath;
        this.movieName = movieName;
        this.movieDesc = movieDesc;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
/*
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }*/

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

/*
    public MovieTile(String url){
        this.url = url;
    }
*/

    public MovieTile(String movieId, String posterPath){
        this.movieId = movieId;
        this.posterPath = posterPath;
    }

    @Override
    public String toString() {
        return " Movie id is " + movieId + " and movie poster path is " + posterPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieId);
        dest.writeString(posterPath);
        dest.writeString(movieName);
        dest.writeString(movieDesc);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
    }

    public List<String> getTrailerList() {
        return trailerList;
    }

    public void setTrailerList(List<String> trailerList) {
        this.trailerList = trailerList;
    }

    public List<String> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<String> reviewList) {
        this.reviewList = reviewList;
    }
}

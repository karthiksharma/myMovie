package com.karthik.mymovie.mymovie;

/**
 * Created by Kary on 13-Feb-16.
 */
public class MovieTile {

//    private String url;
    private String movieId;
    private String posterPath;
    private String movieName;
    private String movieDesc;
    private String userRating;
    private String releaseDate;

    public MovieTile() {
    }

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
}

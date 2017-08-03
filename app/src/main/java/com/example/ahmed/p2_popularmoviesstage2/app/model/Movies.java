package com.example.ahmed.p2_popularmoviesstage2.app.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.ahmed.p2_popularmoviesstage2.app.ui.MainFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ahmed on 10/08/2016.
 */
public class Movies implements Parcelable {

    private int movie_id;
    private String original_title;
    private String poster_path;
    private String backdrop_path;
    private String overview;
    private int vote_count;
    private double vote_average;
    private String release_date;

    public Movies() {

    }

    public Movies(JSONObject movie) throws JSONException {
        this.movie_id = movie.getInt("id");
        this.original_title = movie.getString("original_title");
        this.poster_path = movie.getString("poster_path");
        this.backdrop_path = movie.getString("backdrop_path");
        this.overview = movie.getString("overview");
        this.vote_count = movie.getInt("vote_count");
        this.vote_average = movie.getDouble("vote_average");
        this.release_date = movie.getString("release_date");
    }

    public Movies(Cursor cursor) {
        this.movie_id = cursor.getInt(MainFragment.COL_MOVIE_ID);
        this.original_title = cursor.getString(MainFragment.COL_ORIGINAL_TITLE);
        this.poster_path = cursor.getString(MainFragment.COL_POSTER_PATH);
        this.backdrop_path = cursor.getString(MainFragment.COL_BACKDROP_PATH);
        this.overview = cursor.getString(MainFragment.COL_OVERVIEW);
        this.vote_count = cursor.getInt(MainFragment.COL_VOTE_COUNT);
        this.vote_average = cursor.getDouble(MainFragment.COL_VOTE_AVERAGE);
        this.release_date = cursor.getString(MainFragment.COL_RELEASED_DATE);

    }

    private Movies(Parcel in) {
        movie_id = in.readInt();
        original_title = in.readString();
        poster_path = in.readString();
        backdrop_path = in.readString();
        overview = in.readString();
        vote_count = in.readInt();
        vote_average = in.readDouble();
        release_date = in.readString();
    }

    public int getMovie_id() {
        return movie_id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public int getVote_count() {
        return vote_count;
    }

    public double getVote_average() {
        return vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movie_id);
        dest.writeString(original_title);
        dest.writeString(poster_path);
        dest.writeString(backdrop_path);
        dest.writeString(overview);
        dest.writeInt(vote_count);
        dest.writeDouble(vote_average);
        dest.writeString(release_date);
    }

    public static final Creator<Movies> CREATOR
            = new Creator<Movies>() {
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };
}

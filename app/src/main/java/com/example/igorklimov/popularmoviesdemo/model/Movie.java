package com.example.igorklimov.popularmoviesdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Igor Klimov on 11/4/2015.
 */
public class Movie implements Parcelable {
    private String postersUrl;
    private String title;
    private String releaseDate;
    private String vote;
    private String plot;
    private int[] genres;

    public Movie(String posterUrl, String title, String releaseDate, String vote,
                 String plot, int[] genres) {
        this.postersUrl = posterUrl;
        this.title = title;
        this.releaseDate = releaseDate;
        this.vote = vote;
        this.plot = plot;
        this.genres = genres;
    }

    private Movie(Parcel in) {
        this.postersUrl = in.readString();
        this.title = in.readString();
        this.releaseDate = in.readString();
        this.vote = in.readString();
        this.plot = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postersUrl);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(vote);
        dest.writeString(plot);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }

    };

    public String getPostersUrl() {
        return postersUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVote() {
        return vote;
    }

    public String getPlot() {
        return plot;
    }

    public int[] getGenres() {
        return genres;
    }
}

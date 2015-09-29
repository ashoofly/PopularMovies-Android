package com.optaros.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

/**
 * Created by ahsu on 9/29/15.
 */
public class TMDBMovie implements Parcelable {

    public String movieTitle;
    public String thumbnailURL;
    public String posterURL;
    public String synopsis;
    public int userRating;
    public String releaseDate;

    public TMDBMovie() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieTitle);
        dest.writeString(thumbnailURL);
        dest.writeString(posterURL);
        dest.writeString(synopsis);
        dest.writeInt(userRating);
        dest.writeString(releaseDate);
    }

    protected TMDBMovie(Parcel in) {
        movieTitle = in.readString();
        thumbnailURL = in.readString();
        posterURL = in.readString();
        synopsis = in.readString();
        userRating = in.readInt();
        releaseDate = in.readString();
    }

    public static final Creator<TMDBMovie> CREATOR = new Creator<TMDBMovie>() {
        @Override
        public TMDBMovie createFromParcel(Parcel in) {
            return new TMDBMovie(in);
        }

        @Override
        public TMDBMovie[] newArray(int size) {
            return new TMDBMovie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

}

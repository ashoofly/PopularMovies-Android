package com.optaros.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by ahsu on 10/1/15.
 */
public class MovieContract {

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_THUMBNAIL_URI = "thumbnail";
    }

}

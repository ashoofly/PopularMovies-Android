package com.optaros.popularmovies.data;

import android.test.AndroidTestCase;

/**
 * Created by ahsu on 10/2/15.
 */
public class Testdb extends AndroidTestCase {
    public static final String LOG_TAG = Testdb.class.getSimpleName();


    void deleteDB() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    public void setUp() {

    }


}

package com.optaros.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.HashSet;

import com.optaros.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by ahsu on 10/2/15.
 */
public class Testdb extends AndroidTestCase {
    public static final String LOG_TAG = Testdb.class.getSimpleName();


    void deleteDB() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteDB();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String>tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        deleteDB();
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: Database has not been created correctly", c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());
        assertTrue("Error: Database did not have required tables.", tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")", null);

        assertTrue("Error: Unable to query db for table info.", c.moveToFirst());

        final HashSet<String> movieColumnHashSet =
                new HashSet<String>(Arrays.asList(MovieEntry.COLUMN_TITLE,
                                                  MovieEntry.COLUMN_RATING,
                                                  MovieEntry.COLUMN_SYNOPSIS,
                                                  MovieEntry.COLUMN_RELEASE_DATE,
                                                  MovieEntry.COLUMN_THUMBNAIL_URI));

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: Database doesn't contain all the required movie columns",
                   movieColumnHashSet.isEmpty());
        db.close();
    }

    public void testMovieTable() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createExampleMovieValues();

        long movieRowId;
        movieRowId = db.insert(MovieEntry.TABLE_NAME, null, testValues);
        assertTrue(movieRowId != -1);

        Cursor cursor = db.query(MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: No records found from movie query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Location query validation failed", cursor, testValues);

    }


}

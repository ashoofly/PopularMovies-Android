package com.optaros.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Created by ahsu on 10/2/15.
 */
public class TestUtilities {

    public static ContentValues createExampleMovieValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_RATING, 8);
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2015-06-25");
        testValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "Jack & Jill went up a hill.");
        testValues.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL_URI, "/file/to/some/place.png");
        testValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "The Example Movie");
        return testValues;
    }

    public static void validateCurrentRecord(String error, Cursor cursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry :valueSet) {
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() + "' did not match the expected value '"
                + expectedValue + "'." + error, expectedValue, cursor.getString(idx));
        }
    }
}

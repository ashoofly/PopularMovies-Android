package com.optaros.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.optaros.popularmovies.data.MovieContract;
import com.optaros.popularmovies.data.MovieDbHelper;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private TMDBMovie movieObject;
    private MovieDbHelper mDbHelper;

    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if (intent != null && intent.hasExtra(MainActivityFragment.DETAIL_INTENT_EXTRA)) {
            movieObject = intent.getParcelableExtra(MainActivityFragment.DETAIL_INTENT_EXTRA);
            ((TextView) rootView.findViewById(R.id.movie_title)).setText(movieObject.movieTitle);
            new DownloadImageTask((ImageView) rootView.findViewById(R.id.thumbnail))
                    .execute(movieObject.thumbnailURL);
            ((TextView) rootView.findViewById(R.id.release_date)).setText(formatDate(movieObject.releaseDate));
            ((TextView) rootView.findViewById(R.id.user_rating))
                    .setText("Rating: " + Integer.toString(movieObject.userRating)
                            + "/10\n(" + Integer.toString(movieObject.numRatings) + " ratings)");
            ((TextView) rootView.findViewById(R.id.synopsis)).setText(movieObject.synopsis);
            final Button button = (Button) rootView.findViewById(R.id.favorite_button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    saveMovieToFavorites();
                }
            });


        }
        return rootView;
    }

    private void saveMovieToFavorites() {
        mDbHelper = new MovieDbHelper(getActivity());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movieObject.movieTitle);
        values.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movieObject.synopsis);
        //temp put this here. changing to download image and getting real URI.
        values.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL_URI, movieObject.thumbnailURL);
        values.put(MovieContract.MovieEntry.COLUMN_RATING, movieObject.userRating);
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieObject.releaseDate);
        long newRowId;
        newRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

        List<String> columnNames = Arrays.asList(MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_SYNOPSIS,
                MovieContract.MovieEntry.COLUMN_THUMBNAIL_URI,
                MovieContract.MovieEntry.COLUMN_RATING,
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE);


        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        int idx;
        do {
            for (String name : columnNames) {
                idx = cursor.getColumnIndex(name);
                Log.v(LOG_TAG, cursor.getString(idx));
            }
        } while (cursor.moveToNext());
    }

    private String formatDate(String origDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        Date testDate = null;
        try {
            testDate = sdf.parse(origDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
        String newFormat = formatter.format(testDate);
        return newFormat;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

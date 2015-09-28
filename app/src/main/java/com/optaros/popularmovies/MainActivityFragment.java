package com.optaros.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private ImageAdapter mAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        Log.v(LOG_TAG, "onStart() called...");
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView)(rootView.findViewById(R.id.gridview));
        mAdapter = new ImageAdapter(getActivity(), R.layout.grid_item_view,
                R.id.item_image, new ArrayList<String>());
        gridview.setAdapter(mAdapter);




        return rootView;
    }

    private void updateMovies() {
        FetchMovies moviesTask = new FetchMovies();
        moviesTask.execute("popularity.desc", getResources().getString(R.string.tmdb_apikey));
    }

    public class FetchMovies extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length != 2) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(API_KEY, params[1])
                        .build();

                URL url = new URL(builtUri.toString());

                // Log.v(LOG_TAG, "Built URI " + builtUri.toString());


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movie JSON String: " + moviesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviePosterURLsFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mAdapter.clear();
                for (String posterURL : result) {
                   mAdapter.add(posterURL);
                }
            }
            else
                super.onPostExecute(result);
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getMoviePosterURLsFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER_URL = "poster_path";

            //base URL strings
            final String BASE_URL = "http://image.tmdb.org/t/p/";
            final String POSTER_SIZE = "w500";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);
            String[] resultStrs = new String[moviesArray.length()];


            for(int i = 0; i < moviesArray.length(); i++) {
                // Get the JSON object representing the day
                JSONObject movie = moviesArray.getJSONObject(i);
                String posterURL = movie.getString(TMDB_POSTER_URL);
                String fullURL = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(POSTER_SIZE)
                        .appendPath(posterURL.substring(1))
                        .build().toString();
                resultStrs[i] = fullURL;
            }
//            for (String s : resultStrs) {
//                Log.v(LOG_TAG, "Poster url: " + s);
//            }
            return resultStrs;

        }
    }


    public final class PosterImageView extends ImageView {
        public PosterImageView(Context context) {
            super(context);
        }

        public PosterImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        private static final float RATIO = 0.675f / 1.0f;

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
            int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

            int maxWidth = (int) (heightWithoutPadding * RATIO);
            int maxHeight = (int) (widthWithoutPadding / RATIO);

            if (widthWithoutPadding > maxWidth) {
                width = maxWidth + getPaddingLeft() + getPaddingRight();
            } else {
                height = maxHeight + getPaddingTop() + getPaddingBottom();
            }

            setMeasuredDimension(width, height);
        }
    }

    public class ImageAdapter extends ArrayAdapter<String> {

        public ImageAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            PosterImageView view = (PosterImageView) convertView;
            //ImageView view = (ImageView) convertView;
            Context context = getActivity();
            if (view == null) {
                view = new PosterImageView(context);
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            String url = (String) this.getItem(position);
            Picasso.with(context).load(url).into(view);

            return view;
        }
    }
}

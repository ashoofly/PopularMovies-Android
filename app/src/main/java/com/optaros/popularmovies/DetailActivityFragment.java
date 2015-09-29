package com.optaros.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private TMDBMovie movieObject;

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
            ((TextView) rootView.findViewById(R.id.thumbnail)).setText(movieObject.thumbnailURL);
            ((TextView) rootView.findViewById(R.id.release_date)).setText(movieObject.releaseDate);
            ((TextView) rootView.findViewById(R.id.user_rating)).setText(Integer.toString(movieObject.userRating));
            ((TextView) rootView.findViewById(R.id.synopsis)).setText(movieObject.synopsis);
        }
        return rootView;
    }
}

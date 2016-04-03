package com.example.abhishek.moviemaster2;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {setHasOptionsMenu(true); }
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static final String MOVIE_SHARE_HASHTAG = " #MovieApp";
    public String mMovieStr;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        String mMovieOverview,mMovieTitle,mMoviePopularity,mMovieVoteAverage,mMoviePosterPath,mMovie_imdb_id,mMovie_release_date;
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        String [] MovieArray = intent.getStringArrayExtra("movie-array");
        mMovieTitle=MovieArray[0];
        mMoviePosterPath=MovieArray[1];
        mMovie_imdb_id="imdb_id : "+MovieArray[2];
        mMovie_release_date="Release Date : "+MovieArray[3];
        mMoviePopularity="Popularity : "+MovieArray[4];
        mMovieVoteAverage="User Rating : "+MovieArray[5]+"/10";
        mMovieOverview=MovieArray[6];

        mMovieStr=mMovieTitle+"\n"+mMovie_imdb_id+"\n"+mMovie_release_date+"\n"+mMovieVoteAverage;
        ((TextView) rootView.findViewById(R.id.detail_title)).setText(mMovieTitle);
        ((TextView) rootView.findViewById(R.id.detail_imdb_id)).setText(mMovie_imdb_id);
        ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(mMovie_release_date);
        ((TextView) rootView.findViewById(R.id.detail_popularity)).setText(mMoviePopularity);
        ((TextView) rootView.findViewById(R.id.detail_vote_count)).setText(mMovieVoteAverage);
        ((TextView) rootView.findViewById(R.id.detail_overview)).setText(mMovieOverview);
        //((TextView) rootView.findViewById(R.id.detail_poster)).setText(mMoviePosterPath);

        ImageView imageView=(ImageView) rootView.findViewById(R.id.poster);
        Picasso.with(getActivity())
                .load(mMoviePosterPath)
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.failed)
                .into(imageView);
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
    {
        //inflater.inflate(R.menu.menu_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareMovieIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
       shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mMovieStr + "\n"+MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }
}

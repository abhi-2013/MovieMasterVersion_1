package com.example.abhishek.moviemaster2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivityFragment extends Fragment
{
    public String API_KEY="Your API_KEY";

    public MainActivityFragment() { }

    private GridView gridView;
    private GridViewAdapter gridViewAdapter;

    public String[] Movie_title=new String [20];
    public String[] Movie_overview=new String[20];
    public String[] Movie_release_date=new String[20];
    public String[] Movie_imdb_id=new String[20];
    public String[] Movie_popularity=new String[20];
    public String[] Movie_vote_average=new String[20];
    public String[] Movie_poster_path=new String[20];


    private void updateMovie()
    {
        FetchMovieData MovieData=new FetchMovieData();
        MovieData.execute("Movies");
    }
    public void onStart()
    {
        super.onStart();
        updateMovie();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView=inflater.inflate(R.layout.fragment_main,container,false);

        gridView=(GridView) rootView.findViewById(R.id.gridView);
        gridViewAdapter=new GridViewAdapter(getActivity(),Movie_poster_path);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                String movie_title=Movie_title[position];
                String movie_overview = Movie_overview[position];
                String movie_poster_path = Movie_poster_path[position];
                String movie_popularity=Movie_popularity[position];
                String movie_vote_average= Movie_vote_average[position];
                String movie_imdb_id=Movie_imdb_id[position];
                String movie_release_date=Movie_release_date[position];
                String[] movie_arr={movie_title,movie_poster_path,movie_imdb_id,movie_release_date,movie_popularity,movie_vote_average,movie_overview};
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("movie-array",movie_arr);
                startActivity(intent);
            }
        });
        return rootView;
    }
    public class FetchMovieData extends AsyncTask<String,Void,String []>
    {
        private final String LOG_TAG=FetchMovieData.class.getSimpleName();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unitType = sharedPrefs.getString(getString(R.string.pref_sort_via_key), getString(R.string.pref_sort_via_most_popular));

        private String[] getMovieDataFromJson(String MovieJsonStr) throws JSONException
        {
            // These are the names of the JSON objects that need to be extracted.

            final String OWM_RESULTS = "results";
            final String OWM_TITLE="title";
            final String OWM_RELEASE_DATE="release_date";
            final String OWM_OVERVIEW="overview";
            final String OWM_POPULARITY="popularity";
            final String OWM_VOTE_AVERAGE="vote_average";
            final String OWM_POSTER_PATH="poster_path";
            final String OWM_IMDB_ID="id";


            JSONObject MovieJson = new JSONObject(MovieJsonStr);
            JSONArray MovieArray = MovieJson.getJSONArray(OWM_RESULTS);

            int numMovies=MovieArray.length();
            String[] resultStrs = new String[numMovies];
            for(int i = 0; i < MovieArray.length(); i++)
            {
                String title,overview,popularity,vote_average,poster_path,imdb_id,release_date;

                JSONObject Movie = MovieArray.getJSONObject(i);

                release_date=Movie.getString(OWM_RELEASE_DATE);
                title = Movie.getString(OWM_TITLE);
                overview=Movie.getString(OWM_OVERVIEW);
                popularity=Movie.getString(OWM_POPULARITY);
                vote_average=Movie.getString(OWM_VOTE_AVERAGE);
                poster_path=Movie.getString(OWM_POSTER_PATH);
                imdb_id=Movie.getString(OWM_IMDB_ID);

                Movie_title[i]=title;
                Movie_overview[i]=overview;
                Movie_imdb_id[i]=imdb_id;
                Movie_release_date[i]=release_date;
                Movie_popularity[i]=popularity;
                Movie_vote_average[i]=vote_average;
                Movie_poster_path[i]="http://image.tmdb.org/t/p/w92/"+poster_path.substring(1);
                resultStrs[i] =title;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Movie Title: " + s);
            }
            return resultStrs;

        }
        @Override
        protected String[] doInBackground(String... params)
        {
            if (params.length == 0)
            {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String MovieJsonStr = null;
            String order="popularity.desc";
            try {
                URL url;
                if(unitType.equals(getString(R.string.pref_sort_via_most_popular)))
                {
                    url = new URL("https://api.themoviedb.org/3/discover/movie?api_key="+API_KEY+
                            "&sort_by=popularity.desc");

                }
                else{
                    url = new URL("https://api.themoviedb.org/3/discover/movie?api_key="+API_KEY+
                            "&certification_country=US&certification=R&sort_by=vote_average.desc");
                }

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                {
                    return null;// Nothing to do.
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                MovieJsonStr = buffer.toString();
                Log.v(LOG_TAG,"Movie JSON String : "+MovieJsonStr);
            } catch (IOException e) {
                Log.e("MovieFragment", "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping to parse it.
                return null;
            }
            finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MovieFragment", "Error closing stream", e);
                    }
                }
            }
            //System.out.print(MovieJsonStr);
            try {
                return getMovieDataFromJson(MovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }
}

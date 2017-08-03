package com.example.ahmed.p2_popularmoviesstage2.app.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.ahmed.p2_popularmoviesstage2.app.R;
import com.example.ahmed.p2_popularmoviesstage2.app.adapters.MovieGridAdapter;
import com.example.ahmed.p2_popularmoviesstage2.app.database.MovieContract;
import com.example.ahmed.p2_popularmoviesstage2.app.model.Movies;

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
public class MainFragment extends Fragment {

    private MovieGridAdapter mMovieGridAdapter;

    private static final String SORT_SETTING_KEY = "sort_setting";
    private static final String popular = "popular";
    private static final String top_rate = "top_rated";
    private static final String favorite = "favorite";
    private static final String MOVIES_KEY = "movies";

    private String mView = popular;

    private ArrayList<Movies> mMovies = null;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASED_DATE
    };


    public static final int COL_MOVIE_ID = 1;
    public static final int COL_ORIGINAL_TITLE = 2;
    public static final int COL_POSTER_PATH = 3;
    public static final int COL_BACKDROP_PATH = 4;
    public static final int COL_OVERVIEW = 5;
    public static final int COL_VOTE_COUNT = 6;
    public static final int COL_VOTE_AVERAGE = 7;
    public static final int COL_RELEASED_DATE = 8;


    public MainFragment() {
    }

    public interface Callback {
        void onItemSelected(Movies movies);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main, menu);

        MenuItem show_popular = menu.findItem(R.id.view_popular);
        MenuItem show_top_rate = menu.findItem(R.id.view_top_rate);
        MenuItem show_favorite = menu.findItem(R.id.view_favorite);

        if (mView.contentEquals(popular)) {
            if (!show_popular.isChecked()) {
                show_popular.setChecked(true);
            }
        } else if (mView.contentEquals(top_rate)) {
            if (!show_top_rate.isChecked()) {
                show_top_rate.setChecked(true);
            }
        } else if (mView.contentEquals(favorite)) {
            if (!show_favorite.isChecked()) {
                show_favorite.setChecked(true);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.view_popular:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mView = popular;
                updateMovies(mView);
                return true;
            case R.id.view_top_rate:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mView = top_rate;
                updateMovies(mView);
                return true;
            case R.id.view_favorite:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mView = favorite;
                updateMovies(mView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        GridView mGridView = (GridView) view.findViewById(R.id.movies_grid_view);

        mMovieGridAdapter = new MovieGridAdapter(getActivity(), new ArrayList<Movies>());

        mGridView.setAdapter(mMovieGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movies movies = mMovieGridAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movies);
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORT_SETTING_KEY)) {
                mView = savedInstanceState.getString(SORT_SETTING_KEY);
            }

            if (savedInstanceState.containsKey(MOVIES_KEY)) {
                mMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
                mMovieGridAdapter.setData(mMovies);
            } else {
                updateMovies(mView);
            }
        } else {
            updateMovies(mView);
        }

        return view;
    }

    private void updateMovies(String sort_by) {
        if (!sort_by.contentEquals(favorite)) {
            new FetchMoviesTask().execute(sort_by);
        } else {
            new FetchFavoriteMoviesTask(getActivity()).execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!mView.contentEquals(popular)) {
            outState.putString(SORT_SETTING_KEY, mView);
        }
        if (mMovies != null) {
            outState.putParcelableArrayList(MOVIES_KEY, mMovies);
        }
        super.onSaveInstanceState(outState);
    }



    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movies>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private List<Movies> getMoviesDataFromJson(String jsonStr) throws JSONException {
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            List<Movies> results = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                Movies moviesModel = new Movies(movie);
                results.add(moviesModel);
            }

            return results;
        }

        @Override
        protected List<Movies> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(API_KEY, getString(R.string.api_key))
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                new AlertDialogFragment().show(getActivity().getSupportFragmentManager(), "Internet Connection");
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
                return getMoviesDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movies> movies) {
            if (movies != null) {
                if (mMovieGridAdapter != null) {
                    mMovieGridAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }
    }

    public class FetchFavoriteMoviesTask extends AsyncTask<Void, Void, List<Movies>> {

        private Context mContext;

        public FetchFavoriteMoviesTask(Context context) {
            mContext = context;
        }

        private List<Movies> getFavoriteMoviesDataFromCursor(Cursor cursor) {
            List<Movies> results = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Movies movies = new Movies(cursor);
                    results.add(movies);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }

        @Override
        protected List<Movies> doInBackground(Void... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
            return getFavoriteMoviesDataFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<Movies> movies) {
            if (movies != null) {
                if (mMovieGridAdapter != null) {
                    mMovieGridAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }
    }
}

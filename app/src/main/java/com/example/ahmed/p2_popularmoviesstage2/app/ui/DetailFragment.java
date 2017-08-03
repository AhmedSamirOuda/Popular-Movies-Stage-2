package com.example.ahmed.p2_popularmoviesstage2.app.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed.p2_popularmoviesstage2.app.R;
import com.example.ahmed.p2_popularmoviesstage2.app.adapters.ReviewsAdapter;
import com.example.ahmed.p2_popularmoviesstage2.app.adapters.TrailersAdapter;
import com.example.ahmed.p2_popularmoviesstage2.app.database.MovieContract;
import com.example.ahmed.p2_popularmoviesstage2.app.model.Movies;
import com.example.ahmed.p2_popularmoviesstage2.app.model.Reviews;
import com.example.ahmed.p2_popularmoviesstage2.app.model.Trailers;
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
public class DetailFragment extends Fragment {

    public static final String TAG = DetailFragment.class.getSimpleName();

    static final String Movie_detail = "detail";

    private Movies mMovies;

    private CardView mReviewsCardview;
    private CardView mTrailersCardview;

    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;

    private Toast mToast;

    private static Context context;

    public Context getContext() {
        return DetailFragment.context;
    }


    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    //to get image url
    public static String getImageUrl(int width, String fileName) {
        return "http://image.tmdb.org/t/p/w" + Integer.toString(width) + fileName;
    }


    public static int favoriteChoose(Context context, int id) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{Integer.toString(id)}, null
        );
        int rowNumber = cursor.getCount();
        cursor.close();
        return rowNumber;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mMovies != null) {
            inflater.inflate(R.menu.fragment_detail, menu);
            final MenuItem favorite = menu.findItem(R.id.favorite);
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    return favoriteChoose(getActivity(), mMovies.getMovie_id());
                }

                @Override
                protected void onPostExecute(Integer added) {
                    favorite.setIcon(added == 1 ? R.drawable.star_on : R.drawable.star_off);
                }
            }.execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.favorite:
                if (mMovies != null) {
                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(Void... params) {
                            return favoriteChoose(getActivity(), mMovies.getMovie_id());
                        }

                        @Override
                        protected void onPostExecute(Integer remove) {
                            //remove from favorite
                            if (remove == 1) {
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {
                                        return getActivity().getContentResolver().delete(
                                                MovieContract.MovieEntry.CONTENT_URI,
                                                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                                new String[]{Integer.toString(mMovies.getMovie_id())}
                                        );
                                    }

                                    @Override
                                    protected void onPostExecute(Integer rowsDeleted) {
                                        item.setIcon(R.drawable.star_off);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(), "Movie removed from your favorites", Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }

                            //add to favorit
                            else {
                                new AsyncTask<Void, Void, Uri>() {
                                    @Override
                                    protected Uri doInBackground(Void... params) {
                                        ContentValues values = new ContentValues();

                                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovies.getMovie_id());
                                        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mMovies.getOriginal_title());
                                        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mMovies.getPoster_path());
                                        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, mMovies.getBackdrop_path());
                                        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovies.getOverview());
                                        values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, mMovies.getVote_count());
                                        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mMovies.getVote_average());
                                        values.put(MovieContract.MovieEntry.COLUMN_RELEASED_DATE, mMovies.getRelease_date());

                                        return getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                                                values);
                                    }

                                    @Override
                                    protected void onPostExecute(Uri returnUri) {
                                        item.setIcon(R.drawable.star_on);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(), "Movie added to your favorites", Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                        }
                    }.execute();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovies = arguments.getParcelable(DetailFragment.Movie_detail);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ScrollView mDetailLayout = (ScrollView) rootView.findViewById(R.id.detail_layout);
        if (mMovies != null) {
            mDetailLayout.setVisibility(View.VISIBLE);
        } else {
            mDetailLayout.setVisibility(View.INVISIBLE);
        }

        if (mMovies != null) {
            ImageView mBackdrop_path = (ImageView) rootView.findViewById(R.id.backdrop);
            String image_url = getImageUrl(342, mMovies.getBackdrop_path());
            Picasso.with(getContext()).load(image_url).into(mBackdrop_path);

            ImageView mPoster_path = (ImageView) rootView.findViewById(R.id.poster);
            String image_url2 = getImageUrl(342, mMovies.getPoster_path());
            Picasso.with(getContext()).load(image_url2).into(mPoster_path);

            TextView mMovie_title = (TextView) rootView.findViewById(R.id.movie_title);
            mMovie_title.setText(mMovies.getOriginal_title());

            TextView mOverview = (TextView) rootView.findViewById(R.id.overview);
            mOverview.setText(mMovies.getOverview());

            TextView mRelease_date = (TextView) rootView.findViewById(R.id.release_date);
            mRelease_date.setText(mMovies.getRelease_date());

            TextView mVote_count = (TextView) rootView.findViewById(R.id.vote_count);
            mVote_count.setText(Integer.toString(mMovies.getVote_count()));

            TextView mVote_average = (TextView) rootView.findViewById(R.id.vote_average);
            mVote_average.setText(Double.toString(round(mMovies.getVote_average(), 1)) + "/10");

            RatingBar mRatingStarView = (RatingBar) rootView.findViewById(R.id.rating_Bar);
            double d = mMovies.getVote_average();
            float f = (float) d;
            mRatingStarView.setRating(f);
        }


        mReviewsCardview = (CardView) rootView.findViewById(R.id.detail_reviews_cardview);
        mTrailersCardview = (CardView) rootView.findViewById(R.id.detail_trailers_cardview);


        NestedListView mTrailersView = (NestedListView) rootView.findViewById(R.id.trailers_data);
        mTrailersAdapter = new TrailersAdapter(getActivity(), new ArrayList<Trailers>());
        mTrailersView.setAdapter(mTrailersAdapter);

        mTrailersView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Trailers trailers = mTrailersAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailers.getKey()));
                startActivity(intent);
            }
        });

        ListView mReviewsView = (ListView) rootView.findViewById(R.id.reviews_data);
        mReviewsAdapter = new ReviewsAdapter(getActivity(), new ArrayList<Reviews>());
        mReviewsView.setAdapter(mReviewsAdapter);

        return rootView;
    }


    /*
     * to approximate double
     *
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mMovies != null) {
            new FetchTrailersTask().execute(Integer.toString(mMovies.getMovie_id()));
            new FetchReviewsTask().execute(Integer.toString(mMovies.getMovie_id()));
        }
    }


    public class FetchTrailersTask extends AsyncTask<String, Void, List<Trailers>> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        private List<Trailers> getTrailersDataFromJson(String jsonStr) throws JSONException {
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            List<Trailers> results = new ArrayList<>();

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                if (trailer.getString("site").contentEquals("YouTube")) {
                    Trailers trailersModel = new Trailers(trailer);
                    results.add(trailersModel);
                }
            }

            return results;
        }


        @Override
        protected List<Trailers> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
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
                Log.e(LOG_TAG, "Error ", e);
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
                return getTrailersDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(List<Trailers> trailerses) {
            if (trailerses != null) {
                if (trailerses.size() > 0) {
                    mTrailersCardview.setVisibility(View.VISIBLE);
                    if (mTrailersAdapter != null) {
                        mTrailersAdapter.clear();
                        for (Trailers trailer : trailerses) {
                            mTrailersAdapter.add(trailer);
                        }
                    }
                }
            }
        }
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, List<Reviews>> {

        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        private List<Reviews> getReviewsDataFromJson(String jsonStr) throws JSONException {
            JSONObject reviewJson = new JSONObject(jsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray("results");

            List<Reviews> results = new ArrayList<>();

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                results.add(new Reviews(review));
            }

            return results;
        }

        @Override
        protected List<Reviews> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String jsonStr = null;

            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
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
                Log.e(LOG_TAG, "Error in Inter net connection ", e);
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
                return getReviewsDataFromJson(jsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(List<Reviews> reviewses) {
            if (reviewses != null) {
                if (reviewses.size() > 0) {
                    mReviewsCardview.setVisibility(View.VISIBLE);
                    if (mReviewsAdapter != null) {
                        mReviewsAdapter.clear();
                        for (Reviews review : reviewses) {
                            mReviewsAdapter.add(review);
                        }
                    }
                }
            }
        }
    }
}

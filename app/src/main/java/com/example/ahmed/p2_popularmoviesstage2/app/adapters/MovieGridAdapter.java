package com.example.ahmed.p2_popularmoviesstage2.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.ahmed.p2_popularmoviesstage2.app.R;
import com.example.ahmed.p2_popularmoviesstage2.app.model.Movies;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ahmed on 10/08/2016.
 */
public class MovieGridAdapter extends BaseAdapter {


    private final Movies mLock = new Movies();
    private List<Movies> mMovies;
    private final LayoutInflater mInflater;
    private final Context mContext;
    public Context getContext() {
        return mContext;
    }


    public MovieGridAdapter(Context context, List<Movies> objects) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMovies = objects;
    }

    public void add(Movies object) {
        synchronized (mLock) {
            mMovies.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mLock) {
            mMovies.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(List<Movies> data) {
        clear();
        for (Movies movies : data) {
            add(movies);
        }
    }

    public static class ViewHolder {
        public final ImageView grid_image;

        public ViewHolder(View view) {
            grid_image = (ImageView) view.findViewById(R.id.grid_item_image);

        }
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Movies getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = mInflater.inflate(R.layout.movie_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final Movies movies = getItem(position);

        String image_url = "http://image.tmdb.org/t/p/w185" + movies.getPoster_path();

        viewHolder = (ViewHolder) view.getTag();

        Picasso.with(getContext()).load(image_url).into(viewHolder.grid_image);


        return view;
    }
}

package com.example.ahmed.p2_popularmoviesstage2.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahmed.p2_popularmoviesstage2.app.R;
import com.example.ahmed.p2_popularmoviesstage2.app.model.Trailers;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ahmed on 21/08/2016.
 */
public class TrailersAdapter extends BaseAdapter {


    private final LayoutInflater mInflater;
    private final Trailers mLock = new Trailers();

    private List<Trailers> mtrailers;

    private final Context mContext;
    public Context getContext() {
        return mContext;
    }

    public TrailersAdapter(Context context, List<Trailers> objects) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mtrailers = objects;
    }

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView nameView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageOFtrailre);
            nameView = (TextView) view.findViewById(R.id.nameOftrailer);
        }
    }

    public void add(Trailers object) {
        synchronized (mLock) {
            mtrailers.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mLock) {
            mtrailers.clear();
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mtrailers.size();
    }

    @Override
    public Trailers getItem(int position) {
        return mtrailers.get(position);
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
            view = mInflater.inflate(R.layout.trailer_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final Trailers trailers = getItem(position);

        viewHolder = (ViewHolder) view.getTag();

        String yt_thumbnail_url = "http://img.youtube.com/vi/" + trailers.getKey() + "/0.jpg";
        Picasso.with(getContext()).load(yt_thumbnail_url).into(viewHolder.imageView);

        viewHolder.nameView.setText(trailers.getName());

        return view;
    }



}

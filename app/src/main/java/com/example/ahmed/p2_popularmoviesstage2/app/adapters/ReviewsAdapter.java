package com.example.ahmed.p2_popularmoviesstage2.app.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ahmed.p2_popularmoviesstage2.app.R;
import com.example.ahmed.p2_popularmoviesstage2.app.model.Reviews;

import java.util.List;

/**
 * Created by Ahmed on 26/08/2016.
 */
public class ReviewsAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final Reviews mLock = new Reviews();

    private List<Reviews> mReviewses;
    public ReviewsAdapter(Context context, List<Reviews> objects) {

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mReviewses = objects;
    }

    public static class ViewHolder {
        public final TextView author;
        public final TextView content;

        public ViewHolder(View view) {
            author = (TextView) view.findViewById(R.id.autherOfreview);
            content = (TextView) view.findViewById(R.id.contentOfreview);
        }
    }

    public void add(Reviews object) {
        synchronized (mLock) {
            mReviewses.add(object);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mLock) {
            mReviewses.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mReviewses.size();
    }

    @Override
    public Reviews getItem(int position) {
        return mReviewses.get(position);
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
            view = mInflater.inflate(R.layout.review_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final Reviews reviews = getItem(position);

        viewHolder = (ViewHolder) view.getTag();

        viewHolder.author.setText(reviews.getAuthor());
        viewHolder.content.setText(Html.fromHtml(reviews.getContent()));

        return view;
    }

}

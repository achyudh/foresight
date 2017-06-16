package com.example.rkumar32.foresight;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.rkumar32.foresight.utility.PlaceReview;

import java.util.List;

/**
 * Created by rkumar32 on 6/16/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
    private Context mContext;
    private List<PlaceReview> myDataset;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mNameView;
        public TextView mReviewView;
        public TextView mDateView;
        public RatingBar mRatingBar;
        public ViewHolder(View v) {
            super(v);
            mNameView = (TextView) v.findViewById(R.id.user_name_textview);
            mRatingBar = (RatingBar) v.findViewById(R.id.user_review_ratingbar);
            mDateView = (TextView) v.findViewById(R.id.date_textveiw);
            mReviewView = (TextView) v.findViewById(R.id.user_review_textview);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewAdapter(List<PlaceReview> myDataset, Context context) {
        this.myDataset = myDataset;
        mContext = context;
        Log.d("Adapter", "constructor called " + myDataset.size());
    }

    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_row, parent, false);


        ViewHolder vh = new ViewHolder(rootView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Log.d("Adapter", position + "");
        holder.mReviewView.setText(myDataset.get(position).reviewText);
        holder.mDateView.setText(myDataset.get(position).date);
        holder.mRatingBar.setRating(myDataset.get(position).rating);
        holder.mNameView.setText(myDataset.get(position).userName);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myDataset.size();
    }


}

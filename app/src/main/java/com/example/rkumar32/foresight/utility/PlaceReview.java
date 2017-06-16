package com.example.rkumar32.foresight.utility;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aramk on 6/16/17.
 */

public class PlaceReview implements Parcelable {
    public String userName;
    public float rating;
    public String reviewText;
    public String date;

    public PlaceReview() {

    }

    public PlaceReview(String userName, float rating, String reviewText, String date) {
        this.userName = userName;
        this.rating = rating;
        this.reviewText = reviewText;
        this.date = date;
    }

    protected PlaceReview(Parcel in) {
        userName = in.readString();
        rating = in.readFloat();
        reviewText = in.readString();
        date = in.readString();
    }

    public static final Creator<PlaceReview> CREATOR = new Creator<PlaceReview>() {
        @Override
        public PlaceReview createFromParcel(Parcel in) {
            return new PlaceReview(in);
        }

        @Override
        public PlaceReview[] newArray(int size) {
            return new PlaceReview[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userName);
        parcel.writeFloat(rating);
        parcel.writeString(reviewText);
        parcel.writeString(date);
    }
}

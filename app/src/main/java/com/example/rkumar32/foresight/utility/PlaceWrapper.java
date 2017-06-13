package com.example.rkumar32.foresight.utility;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;

import java.net.URI;

/**
 * Created by rkumar32 on 6/13/17.
 */

public class PlaceWrapper implements Parcelable {
    private String name;
    private String address;
    private String id;
    private String phoneNumber;
    private Uri url;
    private float rating;

    public PlaceWrapper(Place place) {
        name = place.getName().toString();
        address = place.getAddress().toString();
        id = place.getId();
        phoneNumber = place.getPhoneNumber().toString();
        url = place.getWebsiteUri();
        rating = place.getRating();
    }

    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Uri getUrl() {
        return url;
    }

    public float getRating() {
        return rating;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(address);
        out.writeString(id);
        out.writeString(phoneNumber);
        out.writeParcelable(url, 0);
        out.writeFloat(rating);
    }

    public static final Parcelable.Creator<PlaceWrapper> CREATOR
            = new Parcelable.Creator<PlaceWrapper>() {
        public PlaceWrapper createFromParcel(Parcel in) {
            return new PlaceWrapper(in);
        }

        public PlaceWrapper[] newArray(int size) {
            return new PlaceWrapper[size];
        }
    };

    private PlaceWrapper(Parcel in) {
        name = in.readString();
        address = in.readString();
        id = in.readString();
        phoneNumber = in.readString();
        url = in.readParcelable(Uri.class.getClassLoader());
        rating = in.readFloat();
    }
}

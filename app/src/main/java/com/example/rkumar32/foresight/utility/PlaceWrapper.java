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
    public String name;
    public String address;
    public String id;
    public String phoneNumber;
    public String url;
    public float rating;
    public int hasRampEntrance;
    public int hasElevator;
    public int hasRestroom;
    public int hasParking;
    public int hasAccNav;

    public PlaceWrapper() {

    }

    public PlaceWrapper(Place place) {
        name = place.getName().toString();
        address = place.getAddress().toString();
        id = place.getId();
        if (place.getPhoneNumber() != null)
            phoneNumber = place.getPhoneNumber().toString();
        else
            phoneNumber = "";
        if (place.getWebsiteUri() != null)
            url = place.getWebsiteUri().toString();
        else
            url = "";
        rating = place.getRating();
        hasRampEntrance = 0;
        hasElevator = 0;
        hasRestroom = 0;
        hasParking = 0;
        hasAccNav = 0;
    }

    public PlaceWrapper(PlaceWrapper place) {
        name = place.name;
        address = place.address;
        id = place.id;
        phoneNumber = place.phoneNumber;
        url = place.url;
        rating = place.rating;
        hasRampEntrance = place.hasRampEntrance;
        hasElevator = place.hasElevator;
        hasRestroom = place.hasRestroom;
        hasParking = place.hasParking;
        hasAccNav = place.hasAccNav;
    }


    public PlaceWrapper(String name, String id, String phoneNumber, String address, String url, int hasRampEntrance, int hasElevator, int hasRestroom, int hasParking, int hasAccNav) {
        this(id, name, hasRampEntrance, hasElevator, hasRestroom, hasParking, hasAccNav);
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.url = url;
    }

    public PlaceWrapper(String name, String id, int hasRampEntrance, int hasElevator, int hasRestroom, int hasParking, int hasAccNav) {
        this.id = id;
        this.name = name;
        this.hasRampEntrance = hasRampEntrance;
        this.hasElevator = hasElevator;
        this.hasRestroom = hasRestroom;
        this.hasParking = hasParking;
        this.hasAccNav = hasAccNav;
    }

    public PlaceWrapper(String name, String id) {
        this.id = id;
        this.name = name;
        this.hasRampEntrance = 0;
        this.hasElevator = 0;
        this.hasRestroom = 0;
        this.hasParking = 0;
        this.hasAccNav = 0;
    }

    private PlaceWrapper(Parcel in) {
        name = in.readString();
        address = in.readString();
        id = in.readString();
        phoneNumber = in.readString();
        url = in.readString();
        rating = in.readFloat();
        hasRampEntrance = in.readInt();
        hasElevator = in.readInt();
        hasRestroom = in.readInt();
        hasParking = in.readInt();
        hasAccNav = in.readInt();

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(address);
        out.writeString(id);
        out.writeString(phoneNumber);
        out.writeString(url);
        out.writeFloat(rating);
        out.writeInt(hasRampEntrance);
        out.writeInt(hasElevator);
        out.writeInt(hasRestroom);
        out.writeInt(hasParking);
        out.writeInt(hasAccNav);
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

}
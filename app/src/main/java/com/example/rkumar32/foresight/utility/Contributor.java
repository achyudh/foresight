package com.example.rkumar32.foresight.utility;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aramk on 6/15/17.
 */

public class Contributor implements Parcelable {

    public String name;
    public String email;
    public String uid;
    public int points;

    public Contributor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Contributor(String username, String email, String uid, int points) {
        this.name = username;
        this.email = email;
        this.points = points;
        this.uid = uid;
    }

    protected Contributor(Parcel in) {
        name = in.readString();
        email = in.readString();
        uid = in.readString();
        points = in.readInt();
    }

    public static final Creator<Contributor> CREATOR = new Creator<Contributor>() {
        @Override
        public Contributor createFromParcel(Parcel in) {
            return new Contributor(in);
        }

        @Override
        public Contributor[] newArray(int size) {
            return new Contributor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(uid);
        parcel.writeInt(points);
    }
}

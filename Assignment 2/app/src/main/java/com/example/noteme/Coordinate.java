package com.example.noteme;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class Coordinate implements Serializable {

    // Instance variables
    private long ID;
    private String address;
    private float latitude;
    private float longitude;

    // Default constructor
    Coordinate() {}
    Coordinate(float latitude, float longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    Coordinate(String address, float latitude, float longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    Coordinate(long id, String address, float latitude, float longitude) {
        this.ID = id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {this.longitude = longitude;}

    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

    public void updateAddress(Context context) {

        StringBuilder builder = new StringBuilder();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = "Not Found";

        try {
            List<Address> addressList = geocoder.getFromLocation(
                     latitude, longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {

                    String addy = " " + address.getAddressLine(i) + ",";
                    builder.append(addy);
                }

                builder.deleteCharAt(builder.length() - 1);

                this.address = builder.toString();
            }
        } catch (IOException e) {
            // Handle the exception
        }
    }

}

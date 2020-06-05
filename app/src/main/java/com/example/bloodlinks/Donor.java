package com.example.bloodlinks;

import android.location.Location;

import java.util.Comparator;

public class Donor {
    private String name,email,mobile,bloodgroup,gender,location;
    private Double longitude,latitude;
    public static double longi,lati;

    public Donor(){

    }

    public Donor(String name, String email,String mobile,String bloodgroup, Double latitude, Double longitude, String location, String gender){

        this.name=name;
        this.email=email;
        this.mobile=mobile;
        this.bloodgroup=bloodgroup;
        this.latitude=latitude;
        this.longitude=longitude;
        this.location=location;
        this.gender=gender;

    }



    public String getMobile() {
        return mobile;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String toString(){
        return "["+longitude+"]";
    }


    public static Comparator<Donor> StuNameComparator = new Comparator<Donor>() {

        public int compare(Donor s1, Donor s2) {

            Location locationA = new Location("point A");

            locationA.setLatitude(lati);
            locationA.setLongitude(longi);

            Location locationB = new Location("point B");

            locationB.setLatitude(s1.getLatitude());
            locationB.setLongitude(s1.getLongitude());

            Location locationC = new Location("point C");

            locationC.setLatitude(s2.getLatitude());
            locationC.setLongitude(s2.getLongitude());



            float distance = locationA.distanceTo(locationB);
            float distance1 = locationA.distanceTo(locationC);

            //ascending order
            return Float.compare(distance,distance1);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }};

}

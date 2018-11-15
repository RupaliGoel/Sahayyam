package com.example.rupali.sos;

public class Emergency {
    public int Emergency_Id;
    public String Emergency_Name;
    public String Emergency_Desc;
    public String User_Email;
    public String Emergency_Image;
    public double Emergency_Lat;
    public double Emergency_Long;
    public double Emergency_Distance;

    public double getEmergency_Distance(){
        return Emergency_Distance;
    }
}

package org.sp.attendance.models;



public class DatabaseModel {

    private String timeStamp;
    private String userName;
    private String deviceID;

    public DatabaseModel(){}

    public String  getTimeStamp(){
        return timeStamp;
    }

    public void setTimeStamp(final String timeStamp){
        this.timeStamp = timeStamp;
    }

    public String getUsername(){
        return userName;
    }

    public void setUsername(final String username){
        this.userName = username;
    }

    public String getDeviceID(){
        return deviceID;
    }

    public void setDeviceID(final String deviceID){
        this.deviceID = deviceID;
    }

}

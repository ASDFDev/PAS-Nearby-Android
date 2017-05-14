package org.sp.attendance.utils;


import java.util.Map;

class DatabaseModel {

    private Map<String,String> timeStamp;
    private String userName;
    private String deviceID;

    DatabaseModel(){}

    public Map<String, String> getTimeStamp(){
        return timeStamp;
    }

    void setTimeStamp(final Map<String, String> timeStamp){
        this.timeStamp = timeStamp;
    }

    public String getUsername(){
        return userName;
    }

    void setUsername(final String username){
        this.userName = username;
    }

    public String getDeviceID(){
        return deviceID;
    }

    void setDeviceID(final String deviceID){
        this.deviceID = deviceID;
    }

}

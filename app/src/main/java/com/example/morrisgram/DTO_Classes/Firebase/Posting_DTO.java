package com.example.morrisgram.DTO_Classes.Firebase;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Posting_DTO  {
    public Posting_DTO(String userUID, String userNickName, String body,String PostedTime) {
        this.UserUID = userUID;
        this.UserNickName = userNickName;
        this.Body = body;
        this.PostedTime = PostedTime;
    }

    private String UserUID;
    private String UserNickName;
    private String Body;
    private String PostedTime;


    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("UserUID",UserUID);
        result.put("UserNicName",UserNickName);
        result.put("Body",Body);
        result.put("PostedTime",PostedTime);
        return result;
    }
}

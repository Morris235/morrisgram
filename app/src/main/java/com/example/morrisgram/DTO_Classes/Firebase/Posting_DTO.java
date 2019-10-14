package com.example.morrisgram.DTO_Classes.Firebase;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Posting_DTO  {
    public Posting_DTO(String userUID, String userNickName, String body,String PostedTime, String likeCount, String replyCount, String posterKey) {
        this.UserUID = userUID;
        this.UserNickName = userNickName;
        this.Body = body;
        this.PostedTime = PostedTime;
        this.LikeCount = likeCount;
        this.ReplyCount = replyCount;
        this.PosterKey = posterKey;
    }

    public String getUserUID() {
        return UserUID;
    }

    public void setUserUID(String userUID) {
        UserUID = userUID;
    }

    public String getUserNickName() {
        return UserNickName;
    }

    public void setUserNickName(String userNickName) {
        UserNickName = userNickName;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getPostedTime() {
        return PostedTime;
    }

    public void setPostedTime(String postedTime) {
        PostedTime = postedTime;
    }

    public String getLikeCount() {
        return LikeCount;
    }

    public void setLikeCount(String likeCount) {
        LikeCount = likeCount;
    }

    public String getReplyCount() {
        return ReplyCount;
    }

    public void setReplyCount(String replyCount) {
        ReplyCount = replyCount;
    }
    public String getPosterKey() {
        return PosterKey;
    }
    private String UserUID;
    private String UserNickName;
    private String Body;
    private String PostedTime;
    private String LikeCount;
    private String ReplyCount;

    public void setPosterKey(String posterKey) {
        PosterKey = posterKey;
    }

    private String PosterKey;

    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("UserUID",UserUID);
        result.put("UserNicName",UserNickName);
        result.put("Body",Body);
        result.put("PostedTime",PostedTime);
        result.put("PosterKey",PosterKey);
        result.put("LikeCount",LikeCount);
        result.put("ReplyCount",ReplyCount);
        return result;
    }
}

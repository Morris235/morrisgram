package com.example.morrisgram.DTO_Classes.Firebase;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Posting_DTO  {
    public Posting_DTO(String userUID, String userNickName, String body, String PostedTime, Object likeCount, Object replyCount, String posterKey) {
        this.UserUID = userUID;
        this.UserNickName = userNickName;
        this.Body = body;
        this.PostedTime = PostedTime;
        this.LikeCount = likeCount;
        this.ReplyCount = replyCount;
        this.PosterKey = posterKey;
    }

    public Posting_DTO(){
    }

    public String getUserUID() {
        return UserUID;
    }

    public void setUserUID(String userUID) {
        this.UserUID = userUID;
    }

    public String getUserNickName() {
        return UserNickName;
    }

    public void setUserNickName(String userNickName) {
        this.UserNickName = userNickName;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        this.Body = body;
    }

    public String getPostedTime() {
        return PostedTime;
    }

    public void setPostedTime(String postedTime) {
        this.PostedTime = postedTime;
    }

    public Object getLikeCount() {
        return LikeCount;
    }

    public void setLikeCount(int likeCount) {
        this.LikeCount = likeCount;
    }

    public Object getReplyCount() {
        return ReplyCount;
    }

    public void setReplyCount(int replyCount) {
        this.ReplyCount = replyCount;
    }
    public String getPosterKey() {
        return PosterKey;
    }

    public void setPosterKey(String posterKey) {
        this.PosterKey = posterKey;
    }

    private String UserUID;
    private String UserNickName;
    private String Body;
    private String PostedTime;
    private Object LikeCount;
    private Object ReplyCount;
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

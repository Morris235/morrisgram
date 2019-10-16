package com.example.morrisgram.DTO_Classes.Firebase;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Posting_DTO  {

    public Posting_DTO(){
    }

    public Posting_DTO(String userUID, String userNickName, String body, String PostedTime, String likeCount, String replyCount, String posterKey) {
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
    public String getPosterKey() {
        return PosterKey;
    }
    public void setPosterKey(String posterKey) {
        this.PosterKey = posterKey;
    }
    public String getReplyCount() {
        return ReplyCount;
    }
    public void setReplyCount(String replyCount) {
        ReplyCount = replyCount;
    }
    public String getLikeCount() {
        return LikeCount;
    }
    public void setLikeCount(String likeCount) {
        LikeCount = likeCount;
    }

    private String UserUID; //유저 프로필 사진 Uri
    private String UserNickName; //유저 닉네임
    private String Body; //게시물 글 내용
    private String PostedTime; //게시물 게시 시간
    private String PosterKey; //게시물 사진 uri
    private String LikeCount; //좋아요
    private String ReplyCount; //댓글

    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("UserUID",UserUID);
        result.put("UserNickName",UserNickName);
        result.put("Body",Body);
        result.put("PostedTime",PostedTime);
        result.put("PosterKey",PosterKey);
        result.put("LikeCount",LikeCount);
        result.put("ReplyCount",ReplyCount);
        return result;
    }
}

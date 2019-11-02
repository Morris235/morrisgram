package com.example.morrisgram.DTOclass.Firebase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class PostingDTO {

    public PostingDTO(){
    }

//    public PostingDTO(String userUID, String userNickName, String body, String postedTime, HashMap<String,Object> likeCount, String posterKey, String timestemp) {
//        this.userUID = userUID;
//        this.userNickName = userNickName;
//        this.body = body;
//        this.postedtime = postedTime;
//        this.likeCount = likeCount;
//        this.posterkey = posterKey;
//        this.tImeStemp = timestemp;
//    }

    public PostingDTO(String userUID, String userNickName, String body, String postedTime, String posterKey, Object timestemp) {
        this.userUID = userUID;
        this.userNickName = userNickName;
        this.body = body;
        this.postedtime = postedTime;
//        this.likeCount = likeCount;
        this.posterkey = posterKey;
    }

    public String getUserUID() {
        return userUID;
    }
    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
    public String getUserNickName() {
        return userNickName;
    }
    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPostedtime() {
        return postedtime;
    }

    public void setPostedtime(String postedtime) {
        this.postedtime = postedtime;
    }

    public String getPosterkey() {
        return posterkey;
    }

    public void setPosterkey(String posterkey) {
        this.posterkey = posterkey;
    }

    public String gettImeStemp() {
        return tImeStemp;
    }

    public void settImeStemp(String tImeStemp) {
        this.tImeStemp = tImeStemp;
    }

//    public HashMap<String,Long> getLikeCount() {
//        return (HashMap<String, Long>) likeCount;
//    }
//    public void setLikeCount(HashMap<String,Long> likeCount) {
//        this.likeCount = likeCount;
//    }

    private String userUID; //유저 프로필 사진 Uri
    private String userNickName; //유저 닉네임
    private String body; //게시물 글 내용
    private String postedtime; //게시물 게시 시간
    private String posterkey; //게시물 사진 uri
    private String tImeStemp; //데이터 정렬에 사용

//    public String getReplyCount() {
//        return replyCount;
//    }
//    public void setReplyCount(String replyCount) {
//        this.replyCount = replyCount;
//    }
//
//    private String replyCount;

    //왜인지는 모르겠으나 LikeCount라고 l을 대문자 L로 작성하면 파베 데이터스냅샷 오류 발생...getter와 setter부분도 주희 요망
//    private HashMap<String,Long> likeCount; //좋아요 = 해쉬맵으로 바꿔볼까??
//    public Map<String, Boolean> likes = new HashMap<>();

    //파베에 업드로할 해쉬맵 틀
    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("UserUID",userUID);
        result.put("UserNickName",userNickName);
        result.put("Body",body);
        result.put("PostedTime",postedtime);
        result.put("PosterKey",posterkey);
//        result.put("like",likeCount); //좋아요 중간노드
        result.put("TimeStemp",tImeStemp);
        return result;
    }
}

package com.example.morrisgram.ClassesDataSet.Firebase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class PostingDTO {

    public PostingDTO(){
    }

    public PostingDTO(String userUID, String userNickName, String body, String postedTime, long likeCount, String posterKey, String timestemp) {
        this.userUID = userUID;
        this.userNickName = userNickName;
        this.body = body;
        this.postedtime = postedTime;
        this.likeCount = likeCount;
        this.posterkey = posterKey;
        this.tImeStemp = timestemp;
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

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        likeCount = likeCount;
    }

    private String userUID; //유저 프로필 사진 Uri
    private String userNickName; //유저 닉네임
    private String body; //게시물 글 내용
    private String postedtime; //게시물 게시 시간
    private String posterkey; //게시물 사진 uri
    private String tImeStemp; //데이터 정렬에 사용
    //왜인지는 모르겠으나 LikeCount라고 l을 대문자 L로 작성하면 파베 데이터스냅샷 오류 발생...getter와 setter부분도 주위 요망 하아 ㅅㅂ
    public long likeCount; //좋아요
    //파베 데이터타입 치환문제가 중점 개념확실히 점검....
    public Map<String, Boolean> likes = new HashMap<>();

    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("UserUID",userUID);
        result.put("UserNickName",userNickName);
        result.put("Body",body);
        result.put("PostedTime",postedtime);
        result.put("PosterKey",posterkey);
        result.put("likeCount",likeCount);
        result.put("TimeStemp",tImeStemp);
        return result;
    }
}

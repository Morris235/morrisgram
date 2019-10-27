package com.example.morrisgram.DTOclass.Firebase;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class FollowDTO {

    public FollowDTO(String follower, String following) {
        this.follower = follower;
        this.following = following;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    private String follower;
    private String following;

    //파베에 업드로할 해쉬맵 틀
    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("Follower",follower);
        result.put("Following",following);
        return result;
    }
}

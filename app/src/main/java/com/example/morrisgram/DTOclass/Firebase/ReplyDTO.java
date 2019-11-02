package com.example.morrisgram.DTOclass.Firebase;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ReplyDTO {

    public ReplyDTO(){
    }

    public ReplyDTO(String replyBody, String replyUseruid) {
        this.replyBody = replyBody;
        this.replyUseruid = replyUseruid;
    }

    public String getReplyBody() {
        return replyBody;
    }

    public void setReplyBody(String replyBody) {
        this.replyBody = replyBody;
    }

    public String getReplyUseruid() {
        return replyUseruid;
    }

    public void setReplyUseruid(String replyUseruid) {
        this.replyUseruid = replyUseruid;
    }

   private String replyBody;
   private String replyUseruid;

    //파베에 업드로할 해쉬맵 틀
    @Exclude
    public Map<String,Object> toMap() {
        HashMap<String,Object> result = new HashMap<>();
        result.put("ReplyBody",replyBody);
        result.put("ReplyUserUid",replyUseruid);
        return result;
    }
}

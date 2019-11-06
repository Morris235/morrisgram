package com.example.morrisgram.DTOclass.AlarmDTO;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class AlarmDTO {

   //void constructor for firebase
   public AlarmDTO(){
   }


   public AlarmDTO(String AlarmUserUID, String AlarmBody, String AlarmPosterUID) {
      this.AlarmUserUID = AlarmUserUID;
      this.AlarmBody = AlarmBody;
      this.AlarmPosterUID = AlarmPosterUID;
   }

   public String getAlarmUserUID() {
      return AlarmUserUID;
   }

   public void setRAlarmUserUID(String AlarmUserUID) {
      this.AlarmUserUID = AlarmUserUID;
   }

   public String getAlarmBody() {
      return AlarmBody;
   }

   public void setAlarmBody(String AlarmBody) {
      this.AlarmBody = AlarmBody;
   }

   public String getAlarmPosterUID() {
      return AlarmPosterUID;
   }

   public void setAlarmPosterUID(String AlarmPosterUID) {
      this.AlarmPosterUID = AlarmPosterUID;
   }

   private String AlarmUserUID;
   private String AlarmBody;
   private String AlarmPosterUID;

   //파베에 업드로할 해쉬맵 틀
   @Exclude
   public Map<String,Object> toMap() {
      HashMap<String,Object> result = new HashMap<>();
      result.put("AlarmUserUID",AlarmUserUID);
      result.put("AlarmBody",AlarmBody);
      result.put("AlarmPosterUID",AlarmPosterUID);
      return result;
   }
}

package com.lastsemester.beatrun;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class FirebasePost {
    public String BPM_RATE;

    public FirebasePost(){

    }

    public FirebasePost(String BPM_RATE){
        this.BPM_RATE = BPM_RATE;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("BPM_RATE", BPM_RATE);
        return result;
    }
}

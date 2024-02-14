package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import android.content.Context;
import android.net.Uri;

public class videoStorage {

    private Uri[] userVideo_array = new Uri[5];
    private  Uri[] originalVideo_array = new Uri[5];

    public void videoStorage(){

    }

    public void addUserVideo(Uri savedVideo){
       for(int i=0; i<5; i++){
           if(userVideo_array[i] == null){
               userVideo_array[i] = savedVideo;
               break;
           }
       }
    }

    public void addOriginalVideo(Uri savedVideo){
        for(int i=0; i<5; i++){
            if(originalVideo_array[i] == null){
                originalVideo_array[i] = savedVideo;
                break;
            }
        }
    }




}

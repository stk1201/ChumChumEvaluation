package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import android.content.Context;
import android.net.Uri;

public class videoStorage {
    //動画をストックする場所。
    //ユーザが踊っている動画と本家のダンス動画をそれぞれの配列に保存する。
    //Uriが動画用の型である。

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

    //動画を取り出すメゾットを後で作成予定
    //TensorFlowでbufferした動画もストックする予定
    //5本の動画が保存できる設定なので抽出するときに抽出する動画を間違いないようにプログラミングする必要がある。
}

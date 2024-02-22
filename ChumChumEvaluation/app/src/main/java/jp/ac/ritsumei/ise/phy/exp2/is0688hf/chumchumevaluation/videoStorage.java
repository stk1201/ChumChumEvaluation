package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import android.content.Context;
import android.net.Uri;

public class videoStorage {
    //動画をストックする場所。
    //ユーザが踊っている動画と本家のダンス動画をそれぞれの配列に保存する。
    //Uriが動画用の型である。

    private Uri[] userVideo_array;//ユーザの動画を保存する配列。
    private  Uri[] originalVideo_array;//本家の動画を保存する配列。

    public videoStorage(){
        userVideo_array = new Uri[6];//要素0に評価する動画を、それ以外には過去の動画を挿入する。
        originalVideo_array = new Uri[6];//要素0に評価する動画を、それ以外には過去の動画を挿入する。
    }

    public void addUserVideo(Uri savedVideo){
        userVideo_array[0] = savedVideo;
        System.out.println("video is okay");
    }

    public void addOriginalVideo(Uri savedVideo){
        originalVideo_array[0] = savedVideo;
    }

    public Uri getVideo(int flag){//flagが0のときにユーザの動画を1のときに本家の動画を取得するメゾット。
        if(flag == 0){
            return userVideo_array[0];
        }
        else{
            return originalVideo_array[0];
        }
    }

    //TensorFlowでbufferした動画もストックする予定
    //5本の動画が保存できる設定なので抽出するときに抽出する動画を間違いないようにプログラミングする必要がある。
}

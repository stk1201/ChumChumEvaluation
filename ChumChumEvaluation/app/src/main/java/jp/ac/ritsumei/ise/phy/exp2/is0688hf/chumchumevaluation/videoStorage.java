package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import android.content.Context;
import android.net.Uri;

public class videoStorage {
    //動画をストックする場所。
    //ユーザが踊っている動画と本家のダンス動画をそれぞれの配列に保存する。
    //Uriが動画用の型である。

    private Context context;
    private static videoStorage storage;
    private Uri[] userVideo_array = new Uri[6];//要素0に評価する動画を、それ以外には過去の動画を挿入する。;
    private Uri[] originalVideo_array = new Uri[6];//要素0に評価する動画を、それ以外には過去の動画を挿入する。;

    private videoStorage(Context context){
        this.context = context.getApplicationContext();
    }

    public static synchronized videoStorage getInstance(Context context){
        if(storage == null){
            storage = new videoStorage(context);
        }
        return storage;
    }

    public void addUserVideo(Uri savedVideo){
        userVideo_array[0] = savedVideo;
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

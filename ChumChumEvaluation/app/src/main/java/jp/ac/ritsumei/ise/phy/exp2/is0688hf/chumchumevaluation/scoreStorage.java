package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import android.content.Context;

public class scoreStorage {
    private Context context;
    private static scoreStorage storage;
    private float scoreArray[][] = new float[6][10];
    //5は保存できる数になる。10はスコアの数である。
    //scoreArray[][9]をでデータが入力しているかどうかを区別にする。0がデータなし。1がデータあり。
    //Video_array[0]が作業場になっているのでそれと互換性を保つために数字を合わせてscoreArray[0][]にデータを入力しない。
    private scoreStorage(Context context){
        this.context = context.getApplicationContext();
    }

    public static synchronized scoreStorage getInstance(Context context){
        if(storage == null){
            storage = new scoreStorage(context);
        }
        return storage;
    }

    public void addScore(float score[]){
        if(scoreArray[5][9] == 1){
            for(int n=2; n<6; n++){
                scoreArray[n-1] = scoreArray[n];
            }
            scoreArray[5] = score;
        }
        for(int n=1; n<6; n++){
            if(scoreArray[n][9] == 0){
                scoreArray[n]=score;
                scoreArray[n][9] = 1;
                break;
            }
        }
    }
}

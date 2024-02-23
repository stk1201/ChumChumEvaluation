package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import android.content.Context;

public class Coodinate {
    //TensorFlowで取得した座標を保存する場所。
    //ロード画面で保存し、スコア表示のところでスコア計算のために抽出する。

    private Context context;
    private static Coodinate coordinate;
    private float[][][] user_coordinate = new float[17][2][];
    private float[][][] original_coordinate = new float[17][2][];
    //double[n][][]はパーツを示している。
    //鼻は0、左目は1、右目は2、左耳は3、右耳は4、左肩は5、右肩は6、左肘は7、右肘は8、左手首は9、右手首は10、左腰は11、右腰は12、左膝は13、右膝は14、左足首は15、右足首は16
    // double[][0][]にはそのパーツのx座標の配列が、double[][1][]にはそのパーツのy座標の配列が挿入されている。
    //double[][][t]は時間を示す。

    private Coodinate(Context context){
        this.context = context.getApplicationContext();
    }

    public static synchronized Coodinate getInstance(Context context){
        if( coordinate == null){
            coordinate = new Coodinate(context);
        }
        return coordinate;
    }

    public void addCoordinate(int flag, float data[][]){
        //flagによって挿入する配列先が変わる。
        // 0はユーザの座標配列に挿入し、1は本家の座標の配列を挿入する。
        //partはパーツを表している。数字とパーツの関係は上記のdouble[n][][]と同じ
       switch (flag){
           case 0://ユーザの配列に挿入
               for(int frame = 0; frame < data.length; frame++){
                   for(int part = 0; part < 17; part+=3){//part = x座標、part+1 = y座標、part+2 = スコア
                       //x座標の入力
                       user_coordinate[part][0] = new float[data.length];
                       user_coordinate[part][0][frame] = data[frame][part];
                       //y座標の入力
                       user_coordinate[part][1] = new float[data.length];
                       user_coordinate[part][1][frame] = data[frame][part+1];
                   }
               }

           break;
           case 1://本家の配列に挿入
               for(int frame = 0; frame < data.length; frame++){
                   for(int part = 0; part < 17; part+=3){//part = x座標、part+1 = y座標、part+2 = スコア
                       //x座標の入力
                       original_coordinate[part][0] = new float[data.length];
                       original_coordinate[part][0][frame] = data[frame][part];
                       //y座標の入力
                       original_coordinate[part][1] = new float[data.length];
                       original_coordinate[part][1][frame] = data[frame][part+1];
                   }
               }
       }

    }

    public float[][][] outCoordinate(int flag) {
        //flagによって抽出する配列が変わる。
        // 0はユーザの座標配列に抽出し、1は本家の座標の配列を抽出する。
        switch (flag) {
            case 0://ユーザの座標配列の抽出
                return user_coordinate;
            case 1://本家の座標配列の抽出
                return original_coordinate;
            default://0か1以外の数字が引数になったらnullを返す。
                return null;

        }
    }
}

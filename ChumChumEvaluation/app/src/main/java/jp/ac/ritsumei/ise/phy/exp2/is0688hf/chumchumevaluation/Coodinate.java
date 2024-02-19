package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

public class Coodinate {
    //TensorFlowで取得した座標を保存する場所。
    //ロード画面で保存し、スコア表示のところでスコア計算のために抽出する。
    private double[][][] user_coordinate = new double[17][2][];
    private double[][][] original_coordinate = new double[17][2][];
    //double[n][][]はパーツを示している。
    //鼻は0、左目は1、右目は2、左耳は3、右耳は3、左肩は4、右肩は5、左肘は6、右肘は7、左手首は8、右手首は9、左腰は10、右腰は11、左膝は12、右膝は13、左足首は14、右足首は15
    // double[][0][]にはそのパーツのx座標の配列が、double[][1][]にはそのパーツのy座標の配列が挿入されている。
    //double[][][t]は時間を示す。

    public void coordinate(){

    }

    public void addCoordinate(int flag, int part, double[] x, double[] y){
        //flagによって挿入する配列先が変わる。
        // 0はユーザの座標配列に挿入し、1は本家の座標の配列を挿入する。
        //partはパーツを表している。数字とパーツの関係は上記のdouble[n][][]と同じ
       switch (flag){
           case 0://ユーザの配列に挿入
               user_coordinate[part][0] = new double[x.length];
               user_coordinate[part][0] = x;
               user_coordinate[part][1] = new double[y.length];
               user_coordinate[part][1] = y;
           break;
           case 1://本家の配列に挿入
               original_coordinate[part][0] = new double[x.length];
               original_coordinate[part][0] = x;
               original_coordinate[part][1] = new double[y.length];
               original_coordinate[part][1] = y;

       }

    }

    public double[][][] outCoordinate(int flag) {
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

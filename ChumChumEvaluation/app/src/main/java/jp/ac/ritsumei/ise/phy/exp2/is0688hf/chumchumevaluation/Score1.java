package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import java.lang.Math;
public class Score1 extends AppCompatActivity {
    //総合スコアを表示する場所。
    //ロード画面でTensorFlowのバッファーがかけられた後に出てくる画面になる。

    private Coodinate coordinate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score2);

        coordinate = new Coodinate();//Coodinateクラスの作成
    }

    //各パーツの座標をCoodinateクラスから抽出する。
    //double[n][][]はパーツを示している。
    //鼻は0、左目は1、右目は2、左耳は3、右耳は3、左肩は4、右肩は5、左肘は6、右肘は7、左手首は8、右手首は9、左腰は10、右腰は11、左膝は12、右膝は13、左足首は14、右足首は15
    // double[][0][]にはそのパーツのx座標の配列が、double[][1][]にはそのパーツのy座標の配列が挿入されている。
    //double[][][t]は時間を示す。
    private double user_coordinate[][][] = coordinate.outCoordinate(0);//ユーザの座標を入力する。
    private double original_coordinate[][][] = coordinate.outCoordinate(1);//オリジナルの座標を入力する。

//    ベクトル算出関数
private static double[][][]calculateVector(double[][] point1, double[][] point2,int num) {
    double[][] vector = new double[2][num];
    vector[0][num] = point2[0][num] - point1[0][num];//x方向
    vector[1][num] = point2[1][num] - point1[1][num];//y方向
    return vector;
}
// 三角形のベクトル計算
private static double[][]calculateVectors(double[][] startPoint1, double[][] startPoint2, double[][] endPoint,int num,int parts) {
    double[][]CulculatedVectors = new double[2][2];
    for (int i = 0; i < parts; i++) {
        double[][] posi1 = new double[1][2];
        double[][] posi2 = new double[1][2];
        posi1= calculateVector(startPoint1, endPoint,num);
        double Xposi1=posi1[0][0];
        double Yposi1=posi1[1][0];
        posi1= calculateVector(startPoint2, endPoint,num);
        double Xposi2=posi1[0][0];
        double Yposi2=posi1[1][0];

        CulculatedVectors[0][0] = Xposi1;// startPoint1からendPoint1へのベクトル
        CulculatedVectors[0][1] =Yposi1;
        CulculatedVectors[1][0] = Xposi2;// startPoint2からendPoint1へのベクトル
        CulculatedVectors[1][1] =Yposi2;

    }
    return CulculatedVectors;
}

//コサイン計算関数
private static double calculateCosine(double[][][] vectors) {
    // ベクトル1の要素
    double x1 = vectors[0][1][0] - vectors[0][0][0];
    double y1 = vectors[0][1][1] - vectors[0][0][1];

    // ベクトル2の要素
    double x2 = vectors[1][1][0] - vectors[1][0][0];
    double y2 = vectors[1][1][1] - vectors[1][0][1];

    // ベクトルの大きさを計算
    double magnitude1 = Math.sqrt(x1 * x1 + y1 * y1);
    double magnitude2 = Math.sqrt(x2 * x2 + y2 * y2);

    // 内積計算
    double dotProduct = x1 * x2 + y1 * y2;

    // 各ベクトルの大きさの積で割る
    double cosine = dotProduct / (magnitude1 * magnitude2);

    return cosine;
}
//スコア合算
private static double scoring(double value1,double value2) {
    double normalizedValue1 = value1+1;// 値を0から1の範囲に正規化する
    double normalizedValue2 = value2+1;// 値を0から1の範囲に正規化する
    double AddValue=normalizedValue1+normalizedValue2;
    double score=(AddValue/4)*100;
    return score;//0~100の値を返す
}

//スコア導出関数
    private static double normalizeAndScale(double value) {
        double normalizedValue = value+1;// 値を0から1の範囲に正規化する
        if (normalizedValue<=1||normalizedValue>2) {//内積がマイナス，変な値の時
            return 0;//スコア0
        }else {//1~2の値をとっている時
            return 100 * normalizedValue;//0~100の値を返す
        }
    }
//    スコア表示関数
//    待ち画面への遷移







}

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
    //double[0][]にはそのパーツのx座標の配列が、double[1][]にはそのパーツのy座標の配列が挿入されている。
    private double nose[][] = coordinate.outCoordinate(0);
    private double leftEye[][] = coordinate.outCoordinate(1);
    private double rightEye[][] = coordinate.outCoordinate(2);
    private double leftEar[][] = coordinate.outCoordinate(3);
    private double rightEar[][] = coordinate.outCoordinate(4);
    private double leftShoulder[][] = coordinate.outCoordinate(5);
    private double rightShoulder[][] = coordinate.outCoordinate(6);
    private double leftElbow[][] = coordinate.outCoordinate(7);
    private double rightElbow[][] = coordinate.outCoordinate(8);
    private double leftWrist[][] = coordinate.outCoordinate(9);
    private double rightWrist[][] = coordinate.outCoordinate(10);
    private double leftWaist[][] = coordinate.outCoordinate(11);
    private double rightWaist[][] = coordinate.outCoordinate(12);
    private double leftKnee[][] = coordinate.outCoordinate(13);
    private double rightKnee[][] = coordinate.outCoordinate(14);
    private double leftAnkle[][] = coordinate.outCoordinate(15);
    private double rightAnkle[][] = coordinate.outCoordinate(16);

//    ベクトル算出関数
private static double[][]calculateVector(double[][] point1, double[][] point2) {
    double[][] vector = new double[1][2];
    vector[0][0] = point2[0][0] - point1[0][0];//x方向
    vector[0][1] = point2[0][1] - point1[0][1];//y方向
    return vector;
}
// 三角形のベクトル計算
private static double[][][] calculateVectors(double[][] startPoint, double[][] endPoint1, double[][] endPoint2) {
    double[][][] vectors = new double[2][1][2];// ベクトルの配列を初期化
    vectors[0] = calculateVector(startPoint, endPoint1);// startPointからendPoint1へのベクトル
    vectors[1] = calculateVector(startPoint, endPoint2);// startPointからendPoint2へのベクトルを計算
    return vectors;
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

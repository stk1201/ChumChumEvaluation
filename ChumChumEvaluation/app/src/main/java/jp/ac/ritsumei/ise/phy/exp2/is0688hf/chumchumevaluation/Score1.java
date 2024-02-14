package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Score1 extends AppCompatActivity {
    //この画面は総合スコアの表示するところ。ロード画面の後にくる。
    //ここではTensorFlowで取得した座標を基に一致率を求める。
    //なお、座標はCoodinate.javaにストックされている。

    private Coodinate coordinate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score1);

       coordinate = new Coodinate();//Coodinate.javaのクラスの作成
    }
    //各パーツごとに座標を挿入。double[0][]にはx座標の配列が、double[1][]にはy座標の配列が挿入される。
    private double[][] nose= coordinate.outCoordinate(0);
    private double[][] leftEye= coordinate.outCoordinate(1);
    private double[][] rightEye= coordinate.outCoordinate(2);
    private double[][] leftEar= coordinate.outCoordinate(3);
    private double[][] rightEar= coordinate.outCoordinate(4);
    private double[][] leftShoulder= coordinate.outCoordinate(5);
    private double[][] rightShoulder= coordinate.outCoordinate(6);
    private double[][] leftElbow= coordinate.outCoordinate(7);
    private double[][] rightElbow= coordinate.outCoordinate(8);
    private double[][] leftWrist= coordinate.outCoordinate(9);
    private double[][] rightWrist= coordinate.outCoordinate(10);
    private double[][] leftWaist= coordinate.outCoordinate(11);
    private double[][] rightWaist= coordinate.outCoordinate(12);
    private double[][] leftKnee= coordinate.outCoordinate(13);
    private double[][] rightKnee= coordinate.outCoordinate(14);
    private double[][] leftAnkle= coordinate.outCoordinate(15);
    private double[][] rightAnkle= coordinate.outCoordinate(16);


}
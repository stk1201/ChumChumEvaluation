package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import java.lang.Math;
public class Score1 extends AppCompatActivity {
    //総合スコアを表示する場所。
    //ロード画面でTensorFlowのバッファーがかけられた後[0][0]に出てくる画面になる。

    private Coodinate coordinate;
    //各パーツの座標をCoodinateクラスから抽出する。
    //double[n][][]はパーツを示している。
    //鼻は0、左目は1、右目は2、左耳は3、右耳は4、左肩は5、右肩は6、左肘は7、右肘は8、左手首は9、右手首は10、左腰は11、右腰は12、左膝は13、右膝は14、左足首は15、右足首は16
    // double[][0][]にはそのパーツのx座標の配列が、double[][1][]にはそのパーツのy座標の配列が挿入されている。
    //double[][][t]は時間を示す。
    private double user_coordinate[][][];
    private double original_coordinate[][][] ;
//    private double user_coordinate[][][] = coordinate.outCoordinate(0);//ユーザの座標を入力する。
//    private double original_coordinate[][][] = coordinate.outCoordinate(1);//オリジナルの座標を入力する。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score1);

        coordinate = coordinate.getInstance(this);//Coodinateクラスの作成
        float user_coordinate[][][] = coordinate.outCoordinate(0);//ユーザの座標を入力する。
        float original_coordinate[][][] = coordinate.outCoordinate(1);//オリジナルの座標を入力する。

        //ベクトル計算
        double[][][][] userVector = new double[17][17][2][user_coordinate[0][0].length];//
        double[][][][] originalVector = new double[17][17][2][user_coordinate[0][0].length];

        calculateVector(user_coordinate,1,5,userVector);//ユーザー右肩からの方向ベクトル
        calculateVector(user_coordinate,1,userVector);//ユーザー左肩からの方向ベクトル
        calculateVector(original_coordinate,5,originalVector);//オリジナル右肩からの方向ベクトル
        calculateVector(original_coordinate,4,originalVector);//オリジナル左肩からの方向ベクトル


        //コサイン計算
        double [][]Cos1= new double[15][user_coordinate[0][0].length];//右肩
        double [][]Cos2= new double[15][user_coordinate[0][0].length];//左肩
        calculateCosine(userRightVector,originalRightVector,Cos1);
        calculateCosine(userLeftVector,originalLeftVector,Cos2);

        //スコア付
        double preScore[][]=new double[15][user_coordinate[0][0].length];
        double Score[]=new double[preScore[0].length];
        double FinalScore[]=new double[3];
        Scoring(Cos1, Cos2, preScore, Score, FinalScore);
    }

    //    ある基準点からのベクトル関数
    private static double[][][][]calculateVector(double coordinate[][][],int measurepoint, int basepoint,double Vector[][][][]) { //measurepointは測定したい点、// basepointは基準点

        for (int j = 0; j < Vector[0][0][0].length; j++) {//時間ごとの繰り返し
            double x1 = coordinate[measurepoint][0][j] - coordinate[basepoint][0][j];//x方向ベクトル
            double y1 = coordinate[measurepoint][1][j] - coordinate[basepoint][1][j];//y方向ベクトル
            double magnitude1 = Math.sqrt(x1 * x1 + y1 * y1);//正規化
            Vector[measurepoint][basepoint][0][j]=x1/magnitude1;
            Vector[measurepoint][basepoint][1][j]=y1/magnitude1;
        }

        return Vector;
    }

    // 三角形のコサイン計算
    private static double[][]calculateCosine(double userVector[][][],double originalVector[][][], double Cosin1or2[][]) {
        for (int i = 0; i < 16; i++) {//パーツごと
            for (int j = 0; j < userVector[0][0].length; j++) {//時間ごと
                double x1 = userVector[i][0][j];
                double y1 =userVector[i][1][j];
                double x2 = originalVector[i][0][j];
                double y2 =originalVector[i][1][j];
                double dotProduct = x1 * x2 + y1 * y2; //(-1<dotProduct<1)
                dotProduct=dotProduct+1;//(1<dotProduct<2)
                Cosin1or2[i][j]=dotProduct;
            }
        }
        return Cosin1or2;
    }
    //スコア合算二つの基準点からプレスコアを導出(それぞれのパーツと時間)

    private static double[] Scoring(double Cos1[][],double Cos2[][],double preScore[][],double Score[],double FinalScore[]) {
        for (int i = 0; i < 16; i++) {//パーツごと
            for (int j = 0; j < Cos1[0].length; j++) {//時間ごと
                double cos1 = Cos1[i][j];
                double cos2 = Cos2[i][j];
                double AddValue = cos1 + cos2;
                preScore[i][j] = AddValue;
            }
        }
        double score=0;
        double average;
        double bestScore=0;

        for (int i = 0; i < preScore[0].length; i++) {//時間ごとに得点を出す
            for (int j = 0; j < 16; j++) {//パーツごと
                Score[i] = Score[i] + preScore[i][j];
            }
            if(Score[i]>bestScore) {//採点中の最高点ならばbestScore書き換え
                bestScore = Score[i];
                FinalScore[0] = i;//ベストスコアの時刻
                FinalScore[1] = bestScore * 25;//ベストスコアを代入
            }
        }for (int i= 0; i < Score.length; i++) {//合計点を出す
            score=score+Score[i];
        }
        average=score/(Score.length);//平均計算
        FinalScore[3]=average*25;//最終結果を代入

        return FinalScore;
        }



    // プログレスバー








}

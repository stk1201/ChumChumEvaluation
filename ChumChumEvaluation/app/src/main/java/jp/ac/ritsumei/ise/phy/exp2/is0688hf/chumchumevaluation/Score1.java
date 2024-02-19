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

    private static double MAIN(double user_coordinate[][][]){
        return 0;
    }

//    ある基準点からのベクトル関数
double[][][] userVector = new double[15][2][user_coordinate.length];
double[][][] originalVector = new double[15][2][user_coordinate.length];
private static double[][][]calculateVector(double user_coordinate[][][], int RightOrLeft,double userOrOriginalVector[][][]) {
    for (int i=0;i<userOrOriginalVector.length;i++) {//パーツごとの繰り返し
        for (int j = 0; j < userOrOriginalVector.length; j++) {//時間ごとの繰り返し
            double x1 = user_coordinate[i][0][j] - user_coordinate[RightOrLeft][0][j];//x方向ベクトル
            double y1 = user_coordinate[i][1][j] - user_coordinate[RightOrLeft][1][j];//y方向ベクトル
            double magnitude1 = Math.sqrt(x1 * x1 + y1 * y1);//正規化
            userOrOriginalVector[i][0][j]=x1/magnitude1;
            userOrOriginalVector[i][1][j]=y1/magnitude1;
        }
    }
    return userOrOriginalVector;
}
// 三角形のコサイン計算
double [][]Cosine1= new double[15][user_coordinate.length];
double [][]Cosine2= new double[15][user_coordinate.length];

private static double[][]calculateVectors(double userVector[][][],double originalVector[][][], double Cosin1or2[][]) {
    for (int i = 0; i < 16; i++) {//パーツごと
        for (int j = 0; j < userVector.length; j++) {//時間ごと
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
double preScore[][]=new double[15][user_coordinate.length];
private static double[][] preScoring(double Cosin1[][],double Cosin2[][],double preScore[][]) {
    for (int i = 0; i < 16; i++) {//パーツごと
        for (int j = 0; j < Cosin1.length; j++) {//時間ごと
            double cos1=Cosin1[i][j];
            double cos2=Cosin2[i][j];
            double AddValue = cos1+cos2;
            preScore[i][j]=AddValue;
        }
    }
    return preScore;
}    //スコア付け関数
double Score[][]=new double[2][2];
private static double[][] scoring(double preScore[][],double Score[][]){
    double score=0;
    double average;
    double bestScore=0;
        for (int i = 0; i < 16; i++) {//パーツごと
            for (int j = 0; j < preScore.length; j++) {//時間ごと
                score=score+preScore[i][j];//スコア総和
                if(preScore[i][j]>bestScore){//採点中の最高点ならば
                    bestScore=preScore[i][j];
                    Score[1][0]=bestScore;
                    Score[1][1]=j;
                }
            }
        }
        average=score/(preScore.length*15);
        Score[0][0]=average;
        return Score;
    }



//    スコア表示関数
//    待ち画面への遷移







}

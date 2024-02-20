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
    //鼻は0、左目は1、右目は2、左耳は3、右耳は3、左肩は4、右肩は5、左肘は6、右肘は7、左手首は8、右手首は9、左腰は10、右腰は11、左膝は12、右膝は13、左足首は14、右足首は15
    // double[][0][]にはそのパーツのx座標の配列が、double[][1][]にはそのパーツのy座標の配列が挿入されている。
    //double[][][t]は時間を示す。
    private double user_coordinate[][][] = coordinate.outCoordinate(0);//ユーザの座標を入力する。;
    private double original_coordinate[][][] = coordinate.outCoordinate(1);//オリジナルの座標を入力する。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score1);

        coordinate = new Coodinate();//Coodinateクラスの作成

        //ベクトル計算
        double[][][] userRightVector = new double[15][2][user_coordinate[0][0].length];//
        double[][][] userLeftVector = new double[15][2][user_coordinate[0][0].length];//
        double[][][] originalRightVector = new double[15][2][original_coordinate[0][0].length];
        double[][][] originalLeftVector = new double[15][2][original_coordinate[0][0].length];

        calculateVector(user_coordinate,5,userRightVector);//ユーザー右肩からの方向ベクトル
        calculateVector(user_coordinate,4,userLeftVector);//ユーザー左肩からの方向ベクトル
        calculateVector(original_coordinate,5,originalRightVector);//オリジナル右肩からの方向ベクトル
        calculateVector(original_coordinate,4,originalLeftVector);//オリジナル左肩からの方向ベクトル

        //コサイン計算
        double [][]Cosine1= new double[15][user_coordinate[0][0].length];//右肩
        double [][]Cosine2= new double[15][user_coordinate[0][0].length];//左肩
        calculateCosine(userRightVector,originalRightVector,Cosine1);
        calculateCosine(userLeftVector,originalLeftVector,Cosine2);

        //スコア付前処理
        double preScore[][]=new double[15][user_coordinate[0][0].length];
        preScoring(Cosine1,Cosine2,preScore);

        //スコア付
        double Score[][]=new double[2][2];
        scoring(preScore,Score);

    }

    //ある基準点からのベクトル関数
    private static double[][][]calculateVector(double coordinate[][][], int RightOrLeft,double vector[][][]) {
        for (int i=0;i<16;i++) {//パーツごとの繰り返し
            for (int j = 0; j < vector[0][0].length; j++) {//時間ごとの繰り返し
                double x = coordinate[i][0][j] - coordinate[RightOrLeft][0][j];//x方向ベクトル
                double y = coordinate[i][1][j] - coordinate[RightOrLeft][1][j];//y方向ベクトル
                double magnitude1 = Math.sqrt(x * x + y * y);//正規化
                vector[i][0][j]=x/magnitude1;
                vector[i][1][j]=y/magnitude1;
            }
        }
        return vector;
    }

    // 三角形のコサイン計算
    private static double[][]calculateCosine(double userVector[][][],double originalVector[][][], double Cosin1or2[][]) {
        for (int i = 0; i < 16; i++) {//パーツごと
            for (int j = 0; j < userVector[0][0].length; j++) {//時間ごと
                double x1 = userVector[i][0][j];
                double y1 =userVector[i][1][j];
                double x2 = originalVector[i][0][j];
                double y2 =originalVector[i][1][j];
                double dotProduct = x1 * x2 + y1 * y2; //ユーザのベクトルと本家のベクトルでcosを求める。範囲は(-1<dotProduct<1)
                dotProduct=dotProduct+1;//範囲を(0<dotProduct<2)に変更する。
                Cosin1or2[i][j]=dotProduct;
            }
        }
        return Cosin1or2;
    }
    //スコア合算二つの基準点からプレスコアを導出(それぞれのパーツと時間)
    double preScore[][]=new double[15][user_coordinate.length];
    private static double[][] preScoring(double Cosin1[][],double Cosin2[][],double preScore[][]) {
        for (int i = 0; i < 16; i++) {//パーツごと
            for (int j = 0; j < Cosin1[0].length; j++) {//時間ごと
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
                for (int j = 0; j < preScore[0].length; j++) {//時間ごと
                    score=score+preScore[i][j];//スコア総和
                    if(preScore[i][j]>bestScore){//採点中が最高点ならばbestScore書き換え
                        bestScore=preScore[i][j];
                        Score[1][0]=bestScore*25;//ベストスコアの更新&100点換算
                        Score[1][1]=j;//ベストスコア更新時の時刻
                    }
                }
            }
            average=score/(preScore[0].length*15);//平均計算
            Score[0][0]=average*25;//平均の100点換算
            return Score;
        }


    // プログレスバー








}

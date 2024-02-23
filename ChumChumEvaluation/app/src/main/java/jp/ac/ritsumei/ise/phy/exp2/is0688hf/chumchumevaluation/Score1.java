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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score1);

        coordinate = coordinate.getInstance(this);//Coodinateクラスの作成
        float user_coordinate[][][] = coordinate.outCoordinate(0);//ユーザの座標を入力する。
        float original_coordinate[][][] = coordinate.outCoordinate(1);//オリジナルの座標を入力する。

        //ベクトル計算
        float userVector[][][][] = new float[17][17][2][user_coordinate[0][0].length];//
        float originalVector[][][][] = new float[17][17][2][user_coordinate[0][0].length];

        //コサイン計算
        float pregrade[][][] = new float[17][17][user_coordinate[0][0].length];//pregradeは100点変換前の点数


        int mp_label[] = {1,2,3,4,7,9,13,15,8,10,14,16};

        for (int i=0; i<12; i++) {
            if (i<8) {
                calculateVector(user_coordinate,mp_label[i],5,userVector);
                calculateVector(original_coordinate,mp_label[i],5,originalVector);
                calculateCosine(mp_label[i],5,userVector,originalVector,pregrade);
            }
            if (i<4 || 7<i) {
                calculateVector(user_coordinate,mp_label[i],6,userVector);
                calculateVector(original_coordinate,mp_label[i],6,originalVector);
                calculateCosine(mp_label[i],6,userVector,originalVector,pregrade);
            }
            if (i>3 && i<8) {
                calculateVector(user_coordinate,mp_label[i],11,userVector);
                calculateVector(original_coordinate,mp_label[i],11,originalVector);
                calculateCosine(mp_label[i],11,userVector,originalVector,pregrade);
            }
            if (i>7) {
                calculateVector(user_coordinate,mp_label[i],12,userVector);
                calculateVector(original_coordinate,mp_label[i],12,originalVector);
                calculateCosine(mp_label[i],12,userVector,originalVector,pregrade);
            }
        }

        float total_pregrade[]=new float[user_coordinate[0][0].length];
        float upper_pregrade[]=new float[user_coordinate[0][0].length];
        float lower_pregrade[]=new float[user_coordinate[0][0].length];
        float head_pregrade[]=new float[user_coordinate[0][0].length];
        float total_grade=0;
        float upper_grade=0;
        float lower_grade=0;
        float head_grade=0;

        for (int t=0; t<user_coordinate[0][0].length; t++) {
            for (int j=0; j<17; j++) {
                for (int i=0; i<5; i++) {
                    head_pregrade[t] += pregrade[i][j][t];
                    total_pregrade[t] += pregrade[i][j][t];
                }
                for (int i=7; i<11; i++) {
                    upper_pregrade[t] += pregrade[i][j][t];
                    total_pregrade[t] += pregrade[i][j][t];
                }
                for (int i=13; i<17; i++) {
                    lower_pregrade[t] += pregrade[i][j][t];
                    total_pregrade[t] += pregrade[i][j][t];
                }
            }
        }

        for (int t=0; t<user_coordinate[0][0].length; t++) {
            total_grade += total_pregrade[t];
            upper_grade += upper_pregrade[t];
            lower_grade += lower_pregrade[t];
            head_grade += head_pregrade[t];
        }

        total_grade = (100*total_grade)/(48*user_coordinate[0][0].length);
        upper_grade = (100*upper_grade)/(16*user_coordinate[0][0].length);
        lower_grade = (100*lower_grade)/(16*user_coordinate[0][0].length);
        head_grade = (100*head_grade)/(16*user_coordinate[0][0].length);

        /*
        //スコア付
        float preScore[][]=new float[15][user_coordinate[0][0].length];
        float Score[]=new float[preScore[0].length];
        float FinalScore[]=new float[3];
        Scoring(Cos1, Cos2, preScore, Score, FinalScore);
         */
    }

    //    ある基準点からのベクトル関数
    private static float[][][][]calculateVector(float coordinate[][][],int measurepoint, int basepoint,float Vector[][][][]) { //measurepointは測定したい点、// basepointは基準点

        for (int j = 0; j < Vector[0][0][0].length; j++) {//時間ごとの繰り返し
            float x1 = coordinate[measurepoint][0][j] - coordinate[basepoint][0][j];//x方向ベクトル
            float y1 = coordinate[measurepoint][1][j] - coordinate[basepoint][1][j];//y方向ベクトル
            float magnitude1 = (float) Math.sqrt(x1 * x1 + y1 * y1);//正規化
            Vector[measurepoint][basepoint][0][j]=x1/magnitude1;
            Vector[measurepoint][basepoint][1][j]=y1/magnitude1;
        }

        return Vector;
    }

    // 三角形のコサイン計算
    private static float[][][]calculateCosine(int measurepoint, int basepoint,float userVector[][][][],float originalVector[][][][], float Cosin[][][]) {

        for (int j = 0; j < userVector[0][0][0].length; j++) {//時間ごと
            float x1 = userVector[measurepoint][basepoint][0][j];
            float y1 = userVector[measurepoint][basepoint][1][j];
            float x2 = originalVector[measurepoint][basepoint][0][j];
            float y2 = originalVector[measurepoint][basepoint][1][j];
            float dotProduct = x1 * x2 + y1 * y2; //(-1<dotProduct<1)
            dotProduct = dotProduct+1;//(0<dotProduct<2)
            Cosin[measurepoint][basepoint][j] = dotProduct;
        }

        return Cosin;
    }
    //スコア合算二つの基準点からプレスコアを導出(それぞれのパーツと時間)

    private static float[] Scoring(float Cos1[][],float Cos2[][],float preScore[][],float Score[],float FinalScore[]) {
        for (int i = 0; i < 16; i++) {//パーツごと
            for (int j = 0; j < Cos1[0].length; j++) {//時間ごと
                float cos1 = Cos1[i][j];
                float cos2 = Cos2[i][j];
                float AddValue = cos1 + cos2;
                preScore[i][j] = AddValue;
            }
        }
        float score=0;
        float average;
        float bestScore=0;

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

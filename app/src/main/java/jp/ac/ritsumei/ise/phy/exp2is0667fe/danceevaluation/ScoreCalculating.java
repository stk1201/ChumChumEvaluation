package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Context;

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

import java.util.Arrays;
import java.util.List;

public class ScoreCalculating {
    private Context context;
    private List<PoseLandmarkerResult> userResult;
    private List<PoseLandmarkerResult> originalResult;
    //基準点
    private Integer[] basePoints = {11, 12, 23, 24};
    //基準点以外で使用する座標
    private Integer[] measurePoints = {13,15,17,19,21,25,27,29,31,7,8,14,16,18,20,22,26,28,30,32};

    public ScoreCalculating(Context context) {
        this.context = context.getApplicationContext();
    }

    public void setPoseLandmarkerResultList(List<PoseLandmarkerResult> userResult, List<PoseLandmarkerResult> originalResult){
        this.userResult = userResult;
        this.originalResult = originalResult;
    }

    public float[] Scoring(){
        //ベクトル
        float[][][][] userVector = new float[userResult.size()][basePoints.length][measurePoints.length][2];
        float[][][][] originalVector = new float[originalResult.size()][basePoints.length][measurePoints.length][2];

        //コサイン
        float[][][] cosinArray =  new float[userVector.length][userVector[0].length][userVector[0][0].length];

        for(int n = 0; n < measurePoints.length; n++){
            //左肩を基準に耳と左半身を計算
            if(n < 11){
                calculateVector(userResult, basePoints[0], measurePoints[n], userVector);
                calculateVector(originalResult, basePoints[0], measurePoints[n], originalVector);

                calculateCosin(userVector, originalVector, basePoints[0], measurePoints[n], cosinArray);
            }
            //右肩を基準に耳と右半身を計算
            if(n > 8){
                calculateVector(userResult, basePoints[1], measurePoints[n], userVector);
                calculateVector(originalResult, basePoints[1], measurePoints[n], originalVector);

                calculateCosin(userVector, originalVector, basePoints[1], measurePoints[n], cosinArray);
            }
            //左腰を基準に左半身を計算
            if(n < 9){
                calculateVector(userResult, basePoints[2], measurePoints[n], userVector);
                calculateVector(originalResult, basePoints[2], measurePoints[n], originalVector);

                calculateCosin(userVector, originalVector, basePoints[2], measurePoints[n], cosinArray);
            }
            //右腰を基準に右半身を計算
            if(n > 10){
                calculateVector(userResult, basePoints[3], measurePoints[n], userVector);
                calculateVector(originalResult, basePoints[3], measurePoints[n], originalVector);

                calculateCosin(userVector, originalVector, basePoints[3], measurePoints[n], cosinArray);
            }
        }

        return TotalizeByEachTime(cosinArray);
    }

    private void calculateVector(List<PoseLandmarkerResult> coordinate, int basePoint, int measurePoint, float[][][][] vector){
        for(int t = 0; t < coordinate.size(); t++){
            //座標取得
            float mearsurePointX = coordinate.get(t).landmarks().get(0).get(measurePoint).x();
            float mearsurePointY = coordinate.get(t).landmarks().get(0).get(measurePoint).y();
            float basePointX = coordinate.get(t).landmarks().get(0).get(basePoint).x();
            float basePointY = coordinate.get(t).landmarks().get(0).get(basePoint).y();

            //各方向ベクトル
            float vectorX = mearsurePointX - basePointX;
            float vectorY = mearsurePointY - basePointY;

            //正規化
            float magnitude = (float) Math.sqrt(vectorX * vectorX + vectorY * vectorY);

            int basePointIndex = Arrays.asList(basePoints).indexOf(basePoint);
            int measurePointIndex = Arrays.asList(measurePoints).indexOf(measurePoint);

            if (basePointIndex != -1 && measurePointIndex != -1) {
                vector[t][basePointIndex][measurePointIndex][0] = vectorX/magnitude;
                vector[t][basePointIndex][measurePointIndex][1] = vectorY/magnitude;
            }
        }
    }

    private void calculateCosin(float[][][][] userVector, float[][][][] originalVector, int basePoint, int measurePoint, float[][][] Cosin){
        int basePointIndex = Arrays.asList(basePoints).indexOf(basePoint);
        int measurePointIndex = Arrays.asList(measurePoints).indexOf(measurePoint);

        if (basePointIndex != -1 && measurePointIndex != -1) {
            for(int t = 0; t < userVector.length; t++){
                float userVectorX = userVector[t][basePointIndex][measurePointIndex][0];
                float userVectorY = userVector[t][basePointIndex][measurePointIndex][1];
                float originalVectorX = originalVector[t][basePointIndex][measurePointIndex][0];
                float originalVectorY = originalVector[t][basePointIndex][measurePointIndex][1];

                float dotProduct = userVectorX * originalVectorX + userVectorY * originalVectorY; //(-1<dotProduct<1)
                dotProduct = dotProduct+1;//(0<dotProduct<2)

                if(Float.isNaN(dotProduct)){
                    //計算不可の場合
                    Cosin[t][basePointIndex][measurePointIndex] = 1;
                }
                else {
                    Cosin[t][basePointIndex][measurePointIndex] = dotProduct;
                }
            }
        }
    }

    private float[] TotalizeByEachTime(float[][][] cosin){
        float[] eachTimeResult = new float[cosin.length];

        for(int t=0; t < cosin.length; t++){
            float total = 0;

            for(int b=0; b < cosin[0].length; b++){
                for(int m=0; m < cosin[0][0].length; m++){
                    total = total + cosin[t][b][m];
                }
            }

            float percentage = (total * 100)/(cosin[0].length * cosin[0][0].length);
            eachTimeResult[t] = percentage;
        }

        return eachTimeResult;
    }
}

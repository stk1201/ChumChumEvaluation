package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {
    //ポーズ推定&スコア計算
    //スコアはインスタンス作成してそこに保存
    //エラーハンドリング必要！！！

    private Uri userVideo;
    private Uri originalVideo;

    /*
    座標リスト。
    List<PoseLandmarkerResult>はフレームごとのPoseLandmarkerResultのリストである。
    List<PoseLandmarkerResult>.get(frame).landmarks().get(0)によって正規化された座標、33箇所を取得できる。
    landmarks()が正規化座標を表しているがリストになっているので.get(0)で正規化座標リストの取得が必要である。
    33箇所の番号は以下のURLを参考
    https://ai.google.dev/edge/mediapipe/solutions/vision/pose_landmarker/index?hl=ja&_gl=1*1n9gh6j*_up*MQ..*_ga*NzE1MzcwMjcwLjE3MjY2MjA3Nzk.*_ga_P1DBVKWT6V*MTcyNjYyMDc3OC4xLjAuMTcyNjYyMDg3Ny4wLjAuMTk5NzE5Nzc4Ng..#models
     */

    private List<PoseLandmarkerResult> userVideoResult;
    private List<PoseLandmarkerResult> originalVideoResult;

    //基準点
    private Integer[] basePoints = {11, 12, 23, 24};
    //基準点以外で使用する座標
    private Integer[] measurePoints = {13,15,17,19,21,25,27,29,31,7,8,14,16,18,20,22,26,28,30,32};

    //テスト時のみpublic
    //時間ごとのスコア配列
    public float[] eachTimeScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent preIntent = getIntent();

        userVideo = Uri.parse(preIntent.getStringExtra("USER_VIDEO_KEY"));
        originalVideo = Uri.parse(preIntent.getStringExtra("ORIGINAL_VIDEO_KEY"));

        if(userVideo != null && originalVideo != null){
            poseEstimation();
        }

        //何もなかったら結果1画面に遷移
//        Intent intent = new Intent(this, Result1Activity.class);
//        startActivity(intent);
    }

    //座標抽出
    private void  poseEstimation(){
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Handler handler = new Handler(Looper.getMainLooper());
        CountDownLatch latch = new CountDownLatch(2);// CountDownLatchで2つの処理が終わるまで待つ

        // ユーザ動画のポーズ推定
        executor.execute(() -> {
            try {
                userVideoResult = detection(userVideo);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown(); // 完了したらカウントダウン
            }
        });

        // オリジナル動画のポーズ推定
        executor.execute(() -> {
            try {
                originalVideoResult = detection(originalVideo);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown(); // 完了したらカウントダウン
            }
        });

        // 両方の処理が完了した後に実行する処理
        new Thread(() -> {
            try {
                latch.await();
                handler.post(() -> {
                    // 両方の処理が完了後の処理
                    scoring();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private PoseLandmarker createModel(){
        final String modelName = "pose_landmarker_heavy.task";
        final BaseOptions.Builder baseOptionsBuilder = BaseOptions.builder().setModelAssetPath(modelName);
        final BaseOptions baseOptions = baseOptionsBuilder.build();
        final float minPoseDetectionConfidence = 0.5f;//姿勢検出に必要な最小信頼スコア
        final float minPoseTrackingConfidence = 0.5f;//ポーズの有無に関する最小信頼スコア
        final float minPosePresenceConfidence = 0.5f;//ポーズ トラッキングの最小信頼スコア
        final int maxNumPoses = 1;//最大ポーズ数

        final PoseLandmarker.PoseLandmarkerOptions.Builder optionsBuilder =
                PoseLandmarker.PoseLandmarkerOptions.builder()
                        .setBaseOptions(baseOptions)
                        .setMinPoseDetectionConfidence(minPoseDetectionConfidence)
                        .setMinTrackingConfidence(minPoseTrackingConfidence)
                        .setMinPosePresenceConfidence(minPosePresenceConfidence)
                        .setNumPoses(maxNumPoses)
                        .setRunningMode(RunningMode.VIDEO);

        final PoseLandmarker.PoseLandmarkerOptions options = optionsBuilder.build();
        PoseLandmarker poseLandmarker = PoseLandmarker.createFromOptions(this, options);
        return poseLandmarker;
    }

    private List<PoseLandmarkerResult> detection(Uri video) throws IOException {
        PoseLandmarker poseLandmarker = createModel();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, video);

        int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        int interval = 1000;//1 sec
        List<PoseLandmarkerResult> results = new ArrayList<>();

        for(int t = 0; t <= duration; t += interval){
            Bitmap frameAtTime = retriever.getFrameAtTime(t);//ms

            if (frameAtTime != null) {
                // ARGB_8888形式に変換
                Bitmap argb8888Frame = frameAtTime.copy(Bitmap.Config.ARGB_8888, false);
                // MPImageに変換
                MPImage mpImage = new BitmapImageBuilder(argb8888Frame).build();

                PoseLandmarkerResult result = poseLandmarker.detectForVideo(mpImage, t);
                results.add(result);
            }
        }

        retriever.release();

        return results;
    }

    //スコアリング
    private void scoring(){
        //ベクトル
        float[][][][] userVector = new float[userVideoResult.size()][basePoints.length][measurePoints.length][2];
        float[][][][] originalVector = new float[originalVideoResult.size()][basePoints.length][measurePoints.length][2];

        //コサイン
        float[][][] cosinArray =  new float[userVector.length][userVector[0].length][userVector[0][0].length];

        for(int n = 0; n < measurePoints.length; n++){
            //左肩を基準に耳と左半身を計算
            if(n < 11){
                calculateVector(userVideoResult, basePoints[0], measurePoints[n], userVector);
                calculateVector(originalVideoResult, basePoints[0], measurePoints[n], originalVector);

                calculateCosin(userVector, originalVector, basePoints[0], measurePoints[n], cosinArray);
            }
            //右肩を基準に耳と右半身を計算
            if(n > 8){
                calculateVector(userVideoResult, basePoints[1], measurePoints[n], userVector);
                calculateVector(originalVideoResult, basePoints[1], measurePoints[n], originalVector);

                calculateCosin(userVector, originalVector, basePoints[1], measurePoints[n], cosinArray);
            }
            //左腰を基準に左半身を計算
            if(n < 9){
                calculateVector(userVideoResult, basePoints[2], measurePoints[n], userVector);
                calculateVector(originalVideoResult, basePoints[2], measurePoints[n], originalVector);

                calculateCosin(userVector, originalVector, basePoints[2], measurePoints[n], cosinArray);
            }
            //右腰を基準に右半身を計算
            if(n > 10){
                calculateVector(userVideoResult, basePoints[3], measurePoints[n], userVector);
                calculateVector(originalVideoResult, basePoints[3], measurePoints[n], originalVector);

                calculateCosin(userVector, originalVector, basePoints[3], measurePoints[n], cosinArray);
            }
        }

        eachTimeScores = TotalizeByEachTime(cosinArray);
        Log.d("PoseLandmarker", "最終結果:" + eachTimeScores[0]);
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
                    Log.d("PoseLandmarker", "コサイン：" +  cosin[t][b][m]);
                    total = total + cosin[t][b][m];
                }
            }

            float percentage = (total * 100)/(cosin[0].length * cosin[0][0].length);
            eachTimeResult[t] = percentage;
        }

        return eachTimeResult;
    }
}
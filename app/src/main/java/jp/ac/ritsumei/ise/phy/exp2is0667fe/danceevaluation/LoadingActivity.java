package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
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
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {
    //ポーズ推定&スコア計算
    //スコアはインスタンス作成してそこに保存
    //エラーハンドリング必要！！！

    //画像表示
    private ImageView imageView;
    private int[] imageResources = {R.drawable.loading1, R.drawable.loading2, R.drawable.loading3, R.drawable.loading4}; // 画像のリソースID配列
    private int currentIndex = 0; // 現在の画像のインデックス
    private Handler handler;
    private Runnable runnable;
    private static final long INTERVAL = 1000; // 1秒ごとに画像を変更する

    //ポーズ推定材料
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
    private float[] eachTimeScores;
    ResultStocker resultStocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

//        imageView = findViewById(R.id.loadingview); // ImageViewの取得
//
//        handler = new Handler();
//
//        // 1秒ごとに画像を更新するRunnableを作成
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                updateImage(); // 画像を更新
//                handler.postDelayed(this, INTERVAL); // 1秒後に再び実行
//            }
//        };
//
//        // 最初の画像を表示
//        updateImage();

        //インスタンス作成
        resultStocker = resultStocker.getInstance(this);

        Intent preIntent = getIntent();

        userVideo = Uri.parse(preIntent.getStringExtra("USER_VIDEO_KEY"));
        originalVideo = Uri.parse(preIntent.getStringExtra("ORIGINAL_VIDEO_KEY"));

        if(userVideo != null && originalVideo != null){
            poseEstimation();
        }

    }

    //座標抽出
    private void  poseEstimation(){
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Handler handler = new Handler(Looper.getMainLooper());
        CountDownLatch latch = new CountDownLatch(2);// CountDownLatchで2つの処理が終わるまで待つ

        // ユーザ動画のポーズ推定
        executor.execute(() -> {
            try {
                DetectPoseLandmarker detectPoseLandmarker = new DetectPoseLandmarker(this);
                userVideoResult = detectPoseLandmarker.detection(userVideo);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                latch.countDown(); // 完了したらカウントダウン
            }
        });

        // オリジナル動画のポーズ推定
        executor.execute(() -> {
            try {
                DetectPoseLandmarker detectPoseLandmarker = new DetectPoseLandmarker(this);
                originalVideoResult = detectPoseLandmarker.detection(originalVideo);
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
                    //スコア計算
                    Scoring();

                    resultStocker.setReachTimeScore(eachTimeScores);

                    //マーカー
                    try {
                        Marker();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //結果1画面に遷移
                    if(eachTimeScores != null && eachTimeScores.length != 0){
                        Intent intent = new Intent(this, Result1Activity.class);
                        startActivity(intent);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //スコアリング
    private void Scoring(){
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
                    total = total + cosin[t][b][m];
                }
            }

            float percentage = (total * 100)/(cosin[0].length * cosin[0][0].length);
            eachTimeResult[t] = percentage;
        }

        return eachTimeResult;
    }

    //マーカー
    private void Marker() throws IOException {
        if(userVideoResult != null && originalVideoResult != null && eachTimeScores != null){
            int maxScoreTime = 0;
            int minScoreTime = 0;
            
            for(int t=1; t < eachTimeScores.length; t++){
                if(eachTimeScores[maxScoreTime] < eachTimeScores[t]){
                    maxScoreTime = t;
                } else if ( eachTimeScores[minScoreTime] > eachTimeScores[t] ) {
                    minScoreTime = t;
                }
            }


            Bitmap userBestShot = DrawVideoFrame(userVideo, maxScoreTime, 1);

            Bitmap originalBestShot = DrawVideoFrame(originalVideo, maxScoreTime, 2);

            resultStocker.setBestShot(userBestShot, originalBestShot);

            Bitmap userWorstShot = DrawVideoFrame(userVideo, minScoreTime, 1);
            Bitmap originalWorstShot = DrawVideoFrame(originalVideo, minScoreTime, 2);

            resultStocker.setWorstShot(userWorstShot, originalWorstShot);
        }
    }


    private Bitmap DrawVideoFrame(Uri video, int time, int resultType) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, video);

        Bitmap videoFrame = retriever.getFrameAtTime(time * 1000000, MediaMetadataRetriever.OPTION_CLOSEST);
        retriever.release();

        int frameHeight = videoFrame.getHeight();
        int frameWidth = videoFrame.getWidth();

        MarkEachFrame markEachFrame = new MarkEachFrame(this, null);
        markEachFrame.clear();
        markEachFrame.setBitmap(videoFrame);


        switch (resultType){
            case 1:
                markEachFrame.setResults(userVideoResult.get(time), frameHeight, frameWidth);
                break;
            case 2:
                markEachFrame.setResults(originalVideoResult.get(time), frameHeight, frameWidth);

                break;
        }

        Bitmap drawBitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(drawBitmap);
        markEachFrame.draw(canvas);

        return drawBitmap;
    }

//    private void updateImage() {
//        // 現在のインデックスに基づいて画像を設定し、次の画像のインデックスに進める
//        imageView.setImageResource(imageResources[currentIndex]);
//        currentIndex = (currentIndex + 1) % imageResources.length; // 次のインデックス（ループする）
//    }
}
package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
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
    private List<Bitmap> userDrawBitmaps;
    private List<Bitmap> originalDrawBitmaps;

    //時間ごとのスコア配列
    private float[] eachTimeScores;
    //インスタンス
    MarkEachFrame markEachFrame;
    ScoreCalculating scoreCalculating;
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
        scoreCalculating = new ScoreCalculating(this);
        markEachFrame = new MarkEachFrame(this, null);
        resultStocker = resultStocker.getInstance(this);

        Intent preIntent = getIntent();

        userVideo = Uri.parse(preIntent.getStringExtra("USER_VIDEO_KEY"));
        originalVideo = Uri.parse(preIntent.getStringExtra("ORIGINAL_VIDEO_KEY"));

        if(userVideo != null && originalVideo != null){
            //動画保存
            resultStocker.setVideos(userVideo, originalVideo);
            //ポーズ推定
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
                Pair<List<PoseLandmarkerResult>, List<Bitmap>> userResults = detectPoseLandmarker.detection(userVideo);
                userVideoResult = userResults.first;
                userDrawBitmaps = userResults.second;
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
                Pair<List<PoseLandmarkerResult>, List<Bitmap>> originalResults = detectPoseLandmarker.detection(originalVideo);
                originalVideoResult = originalResults.first;
                originalDrawBitmaps = originalResults.second;
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
                    //結果保存
                    resultStocker.setPoseLandmarkerResultList(userVideoResult, originalVideoResult);
                    resultStocker.setDrawBitmaps(userDrawBitmaps, originalDrawBitmaps);
                    //スコア計算
                    scoreCalculating.setPoseLandmarkerResultList(userVideoResult, originalVideoResult);
                    eachTimeScores = scoreCalculating.Scoring();
                    //結果保存
                    if(eachTimeScores != null){
                        resultStocker.setReachTimeScore(eachTimeScores);
                    }

                    //結果1画面に遷移
                    Intent intent = new Intent(this, Result1Activity.class);
                    startActivity(intent);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
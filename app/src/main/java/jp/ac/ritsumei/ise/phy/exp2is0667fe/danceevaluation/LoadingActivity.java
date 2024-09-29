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

    private List<PoseLandmarkerResult> userVideoResult;
    private List<PoseLandmarkerResult> originalVideoResult;

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

        Log.d("PoseLandmarker", "duration:" + duration);

        for(int t = 0; t <= duration; t += interval){
            Log.d("PoseLandmarker", "t:" + t);
            Bitmap frameAtTime = retriever.getFrameAtTime(t);//ms

            if (frameAtTime != null) {
                // ARGB_8888形式に変換
                Bitmap argb8888Frame = frameAtTime.copy(Bitmap.Config.ARGB_8888, false);
                // MPImageに変換
                MPImage mpImage = new BitmapImageBuilder(argb8888Frame).build();

                Log.d("PoseLandmarker", "MPImage:" + (mpImage != null));

                PoseLandmarkerResult result = poseLandmarker.detectForVideo(mpImage, t);
                results.add(result);

                argb8888Frame.recycle();
                frameAtTime.recycle();
                frameAtTime.recycle();
            }
        }
        Log.d("PoseLandmarker", "before release");

        retriever.release();

        return results;
    }
}
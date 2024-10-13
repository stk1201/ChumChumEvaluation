package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetectPoseLandmarker {

    private Context context;
    private Uri video;
    private PoseLandmarker poseLandmarker;

    public DetectPoseLandmarker(Context context) {
        this.context = context.getApplicationContext();
    }

    private void createModel(){
        final String modelName = "pose_landmarker_heavy.task";
        final BaseOptions.Builder baseOptionBuilder = BaseOptions.builder();
        baseOptionBuilder.setModelAssetPath(modelName);
        final BaseOptions baseOptions = baseOptionBuilder.build();
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
        PoseLandmarker poseLandmarker = PoseLandmarker.createFromOptions(context, options);

        this.poseLandmarker = poseLandmarker;
    }

    private void clearModel(){
        this.poseLandmarker.close();
        this.poseLandmarker = null;
    }

    public List<PoseLandmarkerResult> detection(Uri video) throws IOException {
        this.video = video;
        createModel();

        if (this.video == null) {
            throw new IllegalArgumentException("Video is not provided.");
        }

        if (this.poseLandmarker == null) {
            throw new IllegalStateException("PoseLandmarker is not initialized.");
        }


        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, this.video);

        int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));//ms
        int interval = 1000;//ms
        List<PoseLandmarkerResult> results = new ArrayList<>();

        for(int t = 0; t <= duration; t += interval){
            Bitmap frameAtTime = retriever.getFrameAtTime(t*1000);//microSec

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

        clearModel();
        return results;
    }
}

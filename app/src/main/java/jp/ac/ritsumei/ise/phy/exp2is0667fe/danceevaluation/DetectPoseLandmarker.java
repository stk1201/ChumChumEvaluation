package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.AttributeSet;

import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker;

public class DetectPoseLandmarker {

    private Context context;
    private Uri video;

    private PoseLandmarker poseLandmarker;

    public DetectPoseLandmarker(Context context) {
        this.context = context.getApplicationContext();
    }

    public void detection(Uri video){
        this.video = video;

        createModel();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, this.video);

        int duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));//ms
        int interval = 1000;//ms
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

}

package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.mediapipe.tasks.components.containers.Connection;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MarkEachFrame extends View {
    private Context context;
    private ResultStocker resultStocker;
    private Bitmap resultBitmap;
    private PoseLandmarkerResult videoResultPerFrame;
    private Paint pointPaint = new Paint();
    private Paint linePaint = new Paint();
    private float scaleFactor = 1f;
    private int imageWidth = 1;
    private int imageHeight = 1;

    public MarkEachFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        resultStocker = resultStocker.getInstance(this.context);
        initPaints();
    }

    public void clear(){
        videoResultPerFrame = null;
        pointPaint.reset();
        linePaint.reset();
        invalidate();
        initPaints();
    }

    private void initPaints() {
        linePaint = new Paint();
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(6f);
        linePaint.setStyle(Paint.Style.STROKE);

        pointPaint = new Paint();
        pointPaint.setColor(Color.YELLOW);
        pointPaint.setStrokeWidth(12f);
        pointPaint.setStyle(Paint.Style.FILL);
    }

    public Bitmap[] Marker() throws IOException {
        Uri userVideo = resultStocker.getUserVideos();
        Uri originalVideo = resultStocker.getOriginalVideos();

        List<PoseLandmarkerResult> userResult = resultStocker.getUserResult();
        List<PoseLandmarkerResult> originalResult = resultStocker.getOriginalResult();

        float[] eachTimeScores = resultStocker.getEachTimeScore();

        if(userResult != null && originalResult != null && eachTimeScores != null){
            int maxScoreTime = 0;
            int minScoreTime = 0;

            for(int t=1; t < eachTimeScores.length; t++){
                if(eachTimeScores[maxScoreTime] < eachTimeScores[t]){
                    maxScoreTime = t;
                } else if ( eachTimeScores[minScoreTime] > eachTimeScores[t] ) {
                    minScoreTime = t;
                }
            }

            Bitmap userBestShot = DrawVideoFrame(userVideo, maxScoreTime, userResult);
            Bitmap originalBestShot = DrawVideoFrame(originalVideo, maxScoreTime, originalResult);
            Bitmap userWorstShot = DrawVideoFrame(userVideo, minScoreTime, userResult);
            Bitmap originalWorstShot = DrawVideoFrame(originalVideo, minScoreTime, originalResult);

            Bitmap[] shots = {userBestShot, originalBestShot, userWorstShot, originalWorstShot};

           return shots;
        }else{
            return null;
        }
    }

    private Bitmap DrawVideoFrame(Uri video, int time, List<PoseLandmarkerResult> result) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(context, video);

        Bitmap videoFrame = retriever.getFrameAtTime(time * 1000000, MediaMetadataRetriever.OPTION_CLOSEST);
        retriever.release();

        int frameHeight = videoFrame.getHeight();
        int frameWidth = videoFrame.getWidth();

        MarkEachFrame markEachFrame = new MarkEachFrame(context, null);
        markEachFrame.clear();
        markEachFrame.setBitmap(videoFrame);

        markEachFrame.setResults(result.get(time), frameHeight, frameWidth);

        Bitmap drawBitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(drawBitmap);
        markEachFrame.draw(canvas);

        return drawBitmap;
    }

    private void setBitmap(Bitmap bitmap) {
        this.resultBitmap = bitmap;
        invalidate();
    }

    private void setResults(PoseLandmarkerResult poseLandmarkerResultsPerFrame, int imageHeight, int imageWidth) {
        this.videoResultPerFrame = poseLandmarkerResultsPerFrame;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;


        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        this.scaleFactor = Math.min(canvas.getWidth() / (float) imageWidth, canvas.getHeight() / (float) imageHeight);

        if(resultBitmap != null){
            canvas.drawBitmap(resultBitmap, 0, 0, null);
        }

        if (videoResultPerFrame != null) {
            for (List<NormalizedLandmark> landmarks : videoResultPerFrame.landmarks()) {
                // ランドマーク間の線の描画
                for (Connection connection : PoseLandmarker.POSE_LANDMARKS) {
                    canvas.drawLine(
                            videoResultPerFrame.landmarks().get(0).get(connection.start()).x() * imageWidth * scaleFactor,
                            videoResultPerFrame.landmarks().get(0).get(connection.start()).y() * imageHeight * scaleFactor,
                            videoResultPerFrame.landmarks().get(0).get(connection.end()).x() * imageWidth * scaleFactor,
                            videoResultPerFrame.landmarks().get(0).get(connection.end()).y() * imageHeight * scaleFactor,
                            linePaint
                    );
                }

                // ランドマークの点の描画
                for (NormalizedLandmark landmark : landmarks) {
                    canvas.drawPoint(
                        landmark.x() * imageWidth * scaleFactor,
                        landmark.y() * imageHeight * scaleFactor,
                        pointPaint
                    );
                }
            }
        }
    }
}

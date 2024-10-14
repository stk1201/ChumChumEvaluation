package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.util.Pair;
import android.widget.ImageView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult;

import java.util.ArrayList;
import java.util.List;

public class ResultStocker {

    private Context context;
    private static ResultStocker resultStocker;
    private Uri userVideo;
    private Uri originalVideo;
    private List<PoseLandmarkerResult> userResult;
    private List<PoseLandmarkerResult> originalResult;
    private float totalScore;
    private float[] eachTimeScore;
    private String rank;
    private Bitmap userBestShot;
    private Bitmap originalBestShot;
    private Bitmap userWorstShot;
    private Bitmap originalWorstShot;

    public ResultStocker(Context context){
        this.context = context.getApplicationContext();
    }

    public static synchronized ResultStocker getInstance(Context context){
        if(resultStocker == null){
            resultStocker = new ResultStocker(context);
        }
        return resultStocker;
    }

    public void setVideos(Uri userVideo, Uri originalVideo){
        this.userVideo = userVideo;
        this.originalVideo = originalVideo;
    }

    public Uri getUserVideos(){
        return this.userVideo;
    }

    public Uri getOriginalVideos(){
        return this.originalVideo;
    }

    public void setPoseLandmarkerResultList(List<PoseLandmarkerResult> userResult, List<PoseLandmarkerResult> originalResult){
        this.userResult = userResult;
        this.originalResult = originalResult;
    }

    public List<PoseLandmarkerResult> getUserResult(){
        return this.userResult;
    }

    public List<PoseLandmarkerResult> getOriginalResult(){
        return this.originalResult;
    }

    public void setReachTimeScore(float[] eachTimeScore){
        this.eachTimeScore = eachTimeScore;

        float total = 0;
        int maxScore = 2;

        //合計スコア
        for(int t=0; t < eachTimeScore.length; t++){
            total = total + eachTimeScore[t];
        }

        this.totalScore = total/(maxScore * eachTimeScore.length);

        //ランク付け
        if(this.totalScore > 80 && this.totalScore <= 100){
            this.rank = "god";
        } else if (this.totalScore > 60) {
            this.rank = "center";
        } else if (this.totalScore > 40) {
            this.rank = "back";
        } else if (this.totalScore > 20) {
            this.rank = "practice";
        } else {
            this.rank = "normal";
        }
    }

    public float getTotalScore(){
        return this.totalScore;
    }

    public float[] getEachTimeScore(){
        return this.eachTimeScore;
    }

    public void showRank(ImageView imageView){
        switch (this.rank){
            case "god":
                imageView.setImageResource(R.drawable.god);
            case "center":
                imageView.setImageResource(R.drawable.center);
            case "back":
                imageView.setImageResource(R.drawable.back);
            case "practice":
                imageView.setImageResource(R.drawable.practice);
            case "normal":
                imageView.setImageResource(R.drawable.normal);
        }
    }

    public void setBestShot(Bitmap userImage, Bitmap originalImage){
       this.userBestShot = userImage;
       this.originalBestShot = originalImage;
    }

    public Bitmap[] getBestShot(){
        return new Bitmap[]{userBestShot, originalBestShot};
    }

    public Bitmap[] getWorstShot(){
        return new Bitmap[]{userWorstShot, originalWorstShot};
    }

    public void setWorstShot(Bitmap userImage, Bitmap originalImage){
        this.userWorstShot = userImage;
        this.originalWorstShot = originalImage;
    }

    public LineData showGraph(){
        List<Float> x = new ArrayList<>();
        List<Float> y = new ArrayList<>();

        for(float t=0; t < this.eachTimeScore.length; t++){
            x.add(t);
            y.add(eachTimeScore[(int)t]);
        }

        List<Entry> entryList = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            entryList.add(new Entry(x.get(i), y.get(i)));
        }

        List<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataSet = new LineDataSet(entryList, "square");

        lineDataSet.setColor(Color.BLUE);

        lineDataSets.add(lineDataSet);

        LineData lineData = new LineData(lineDataSets);

        return lineData;
    }
}

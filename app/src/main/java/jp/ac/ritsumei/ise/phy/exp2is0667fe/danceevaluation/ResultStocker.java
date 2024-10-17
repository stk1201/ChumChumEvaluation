package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
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
    private List<Bitmap> userBitmaps;
    private List<Bitmap> originalBitmaps;
    private Bitmap userBestShot;
    private Bitmap originalBestShot;
    private Bitmap userWorstShot;
    private Bitmap originalWorstShot;
    private Bitmap graph;
    private String musicName;

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

        //合計スコア
        for(int t=0; t < eachTimeScore.length; t++){
            total = total + eachTimeScore[t];
        }
        Log.d("Posemaker", "totalScore:"+total);

        this.totalScore = total/eachTimeScore.length;

        //ランク付け
        if(this.totalScore > 80){
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

        //ベストショットとワーストショットの設定
        setBestAndWorst();
    }

    public float getTotalScore(){
        return this.totalScore;
    }

    public float[] getEachTimeScore(){
        return this.eachTimeScore;
    }

    public String getRank(){
        return this.rank;
    }
    public void showRank(ImageView imageView){
        switch (this.rank){
            case "god":
                imageView.setImageResource(R.drawable.god);
                break;
            case "center":
                imageView.setImageResource(R.drawable.center);
                break;
            case "back":
                imageView.setImageResource(R.drawable.back);
                break;
            case "practice":
                imageView.setImageResource(R.drawable.practice);
                break;
            case "normal":
                imageView.setImageResource(R.drawable.normal);
                break;
        }
    }

    public void setDrawBitmaps(List<Bitmap> userBitmaps, List<Bitmap> originalBitmaps){
        this.userBitmaps = userBitmaps;
        this.originalBitmaps = originalBitmaps;
    }

    private void setBestAndWorst(){
        int maxScoreTime = 0;
        int minScoreTime = 0;

        for(int t=1; t < this.eachTimeScore.length; t++){
            if(this.eachTimeScore[maxScoreTime] < this.eachTimeScore[t]){
                maxScoreTime = t;
            } else if ( this.eachTimeScore[minScoreTime] > this.eachTimeScore[t] ) {
                minScoreTime = t;
            }
        }

        this.userBestShot = userBitmaps.get(maxScoreTime);
        this.originalBestShot = originalBitmaps.get(maxScoreTime);

        this.userWorstShot = userBitmaps.get(minScoreTime);
        this.originalWorstShot = originalBitmaps.get(minScoreTime);
    }

    public Bitmap[] getBestShot(){
        return new Bitmap[]{userBestShot, originalBestShot};
    }

    public Bitmap[] getWorstShot(){
        return new Bitmap[]{userWorstShot, originalWorstShot};
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

    public void setGraph(Bitmap graph){
        this.graph = graph;
    }

    public Bitmap getGraph(){
        return this.graph;
    }

    public void setMusicName(String musicName){
        this.musicName = musicName;
    }

    public String getMusicName(){return this.musicName;}
}

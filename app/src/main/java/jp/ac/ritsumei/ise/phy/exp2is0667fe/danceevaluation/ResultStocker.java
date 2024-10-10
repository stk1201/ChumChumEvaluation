package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ResultStocker {

    private Context context;
    private static ResultStocker resultStocker;
    private float totalScore;
    private float[] eachTimeScore;
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

    public void setReachTimeScore(float[] eachTimeScore){
        this.eachTimeScore = eachTimeScore;

        float total = 0;
        int maxScore = 2;

        for(int t=0; t < eachTimeScore.length; t++){
            total = total + eachTimeScore[t];
        }

        this.totalScore = total/(maxScore * eachTimeScore.length);
    }

    public float getTotalScore(){
        return this.totalScore;
    }

    public float[] getEachTimeScore(){
        return this.eachTimeScore;
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
}

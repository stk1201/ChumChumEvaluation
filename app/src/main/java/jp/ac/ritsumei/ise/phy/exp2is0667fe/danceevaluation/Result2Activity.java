package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class Result2Activity extends AppCompatActivity {

    ResultStocker resultStocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);

        resultStocker = resultStocker.getInstance(this);

        if(resultStocker != null){
            showImages();
        }
    }

    private void showImages(){
        Bitmap[] bestShots = resultStocker.getBestShot();
        Bitmap[] worstShots = resultStocker.getWorstShot();


        //ベストショット表示
        ImageView userBestShotView = findViewById(R.id.userbest);
        ImageView originalBestShotView = findViewById(R.id.originalbest);

        setImage(userBestShotView, bestShots[0]);
        setImage(originalBestShotView, bestShots[1]);

        //ワーストショット表示
        ImageView userWorstShotView = findViewById(R.id.userworst);
        ImageView originalWorstShotView = findViewById(R.id.originalworst);

        setImage(userWorstShotView, worstShots[0]);
        setImage(originalWorstShotView, worstShots[1]);
    }

    private void setImage(ImageView view, Bitmap image){
        if(image != null){
            view.setImageBitmap(image);
        }
        else{
            Log.e("showImage",view + ": no image");
        }
    }

    //結果3画面に遷移
    public void onNextButtonTapped(View view) {
        Intent intent = new Intent(this, Result3Activity.class);
        startActivity(intent);
    }
}
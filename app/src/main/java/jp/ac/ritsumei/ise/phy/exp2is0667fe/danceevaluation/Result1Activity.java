package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Result1Activity extends AppCompatActivity {

    private ResultStocker resultStocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result1);

        resultStocker = resultStocker.getInstance(this);

        if(resultStocker != null){
            //合計スコア表示
            float totalScore = resultStocker.getTotalScore();
            TextView scoreView = findViewById(R.id.totalScore);
            scoreView.setText(String.valueOf((int)totalScore));

            //ランク表示
            ImageView rankView = findViewById(R.id.rank);
            resultStocker.showRank(rankView);
        }
    }

    //結果2画面に遷移
    public void onNextButtonTapped(View view) {
        Intent intent = new Intent(this, Result2Activity.class);
        startActivity(intent);
    }
}
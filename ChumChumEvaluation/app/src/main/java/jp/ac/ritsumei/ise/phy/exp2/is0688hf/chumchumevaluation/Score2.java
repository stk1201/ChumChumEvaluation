package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Score2 extends AppCompatActivity {

    scoreStorage scoreStorage;
    float upper_score;
    float lower_score;
    float head_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score2);

        scoreStorage = scoreStorage.getInstance(this);
        upper_score = scoreStorage.getScore(1);
        lower_score = scoreStorage.getScore(2);
        head_score = scoreStorage.getScore(3);

        display();//スコア表示する
    }

    private void display(){
        //upper scoreの表示
        TextView upperscore = (TextView)findViewById(R.id.upperScore);
        upperscore.setText(String.valueOf((int) upper_score));

        //lower scoreの表示
        TextView lowerscore = (TextView)findViewById(R.id.lowerScore);
        lowerscore.setText(String.valueOf((int) lower_score));

        //head scoreの表示
        TextView headscore = (TextView)findViewById(R.id.headScore);
        headscore.setText(String.valueOf((int) head_score));
    }

    //ホームボタン
    public void onHomeButtonTapped(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Score2 extends AppCompatActivity {

    scoreStorage scoreStorage;
    float upper_score = scoreStorage.getScore(1);
    float lower_score = scoreStorage.getScore(2);
    float head_score = scoreStorage.getScore(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score2);

        scoreStorage = scoreStorage.getInstance(this);

        display();
    }

    private void display(){

    }
}
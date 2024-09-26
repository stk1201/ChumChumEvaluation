package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Result1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result1);
    }

    //結果2画面に遷移
    public void onNextButtonTapped(View view) {
        Intent intent = new Intent(this, Result2Activity.class);
        startActivity(intent);
    }
}
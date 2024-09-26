package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        Intent intent = new Intent(this, Result1Activity.class);
        startActivity(intent);
    }
}
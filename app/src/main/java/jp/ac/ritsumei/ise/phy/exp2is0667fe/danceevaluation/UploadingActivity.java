package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UploadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading);
    }

    //ローディング画面に遷移
    public void onResultButtonTapped(View view) {
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
    }
}
package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    //アプリが開かれたときに最初に出てくるホーム画面。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //アップロード画面に遷移
    public void onStartButtonTapped(View view) {
        Intent intent = new Intent(this, Uploading.class);
        startActivity(intent);
    }
}
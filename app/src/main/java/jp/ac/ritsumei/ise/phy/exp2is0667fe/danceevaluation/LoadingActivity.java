package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LoadingActivity extends AppCompatActivity {
    //エラーハンドリング必要！！！

    private Uri userVideo;
    private Uri originalVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent preIntent = getIntent();
        userVideo = Uri.parse(preIntent.getStringExtra("USER_VIDEO_KEY"));
        originalVideo = Uri.parse(preIntent.getStringExtra("ORIGINAL_VIDEO_KEY"));

        //何もなかったら結果1画面に遷移
        Intent intent = new Intent(this, Result1Activity.class);
        startActivity(intent);
    }
}
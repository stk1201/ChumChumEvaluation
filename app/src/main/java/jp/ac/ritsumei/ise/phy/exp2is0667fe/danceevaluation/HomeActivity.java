package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private UserStocker userStocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    //アップロード画面に遷移
    public void onEvaluateButtonTapped(View view) {
        Intent intent = new Intent(this, UploadingActivity.class);
        startActivity(intent);
    }

    //履歴画面に遷移
    public void onHistoryButtonTapped(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    //ログイン画面に遷移
    public void onLogoutButtonTapped(View view) {
        userStocker = userStocker.getInstance(this);
        userStocker.setUserInfo(0, null);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}

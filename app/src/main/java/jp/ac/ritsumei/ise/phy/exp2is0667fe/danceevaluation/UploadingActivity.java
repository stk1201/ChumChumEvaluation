package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class UploadingActivity extends AppCompatActivity {
    //エラーハンドリングが必要！！！

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading);
    }

    private static final int REQUEST_VIDEO_CAPTURE = 101;
    private static final int REQUEST_PERMISSION = 102;

    private int flag = 0;//ユーザ動画か本家動画の判定

    private Uri userVideo;
    private Uri originalVideo;

    public void onUploadButtonTapped(View View){//Uploadボタンを押されたとき
        //どのUploadボタンが押されたのかの判定
        if(View.getId() == R.id.userUploading){
            flag = 1;
        }
        else{
            flag = 2;
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_VIDEO},
                    REQUEST_PERMISSION);
        } else {
            dispatchPickVideoIntent();
        }

    }

    //アクセス許可
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchPickVideoIntent();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //ビデオ選択のためのインテント
    private void dispatchPickVideoIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
    }

    //動画ファイルの取得と保存
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri savedVideo = data.getData();//ダンス動画の取得

            if(flag == 1){
                userVideo = savedVideo;
                //インテントで動画を保存したときにファイル名を保存する。
                TextView fileName = (TextView)findViewById(R.id.userFileName);
                fileName.setText(savedVideo.toString());
            }
            if(flag == 2){
                originalVideo = savedVideo;
                //インテントで動画を保存したときにファイル名を保存する。
                TextView fileName = (TextView)findViewById(R.id.originalFileName);
                fileName.setText(savedVideo.toString());
            }

        }
    }

    //ローディング画面に遷移
    public void onResultButtonTapped(View view) {
        Intent intent = new Intent(this, LoadingActivity.class);

        //ローディング画面に引き渡し
        intent.putExtra("USER_VIDEO_KEY", userVideo.toString());
        intent.putExtra("ORIGINAL_VIDEO_KEY", originalVideo.toString());

        startActivity(intent);
    }
}
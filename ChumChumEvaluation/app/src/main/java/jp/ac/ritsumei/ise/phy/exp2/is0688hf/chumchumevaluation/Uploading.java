package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

public class Uploading extends AppCompatActivity {
    //ユーザが自身のフォルダから動画をアップロードする場所。
    videoStorage storage;//動画ストレージ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading);

        storage = storage.getInstance(this);//動画ストレージのインスタンスを作成
    }

    //スマホのフォルダーにアクセス
    private static final int REQUEST_VIDEO_CAPTURE = 101;
    private static final int REQUEST_PERMISSION = 102;

    private int flag = 0;//ユーザ動画か本家動画の判定

    public void onUploadButtonTapped(View View){//Uploadボタンを押されたとき
        //どのUploadボタンが押されたのかの判定
        if(View.getId() == R.id.user1){
            flag = 1;
        }
        else{
            flag = 2;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_VIDEO},
                    REQUEST_PERMISSION);
        } else {
            dispatchPickVideoIntent();
        }

    }

    private void dispatchPickVideoIntent() {//ビデオ選択のためのインテント
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {//アクセス許可
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchPickVideoIntent();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//動画ファイルの取得と保存
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri savedVideo = data.getData();//ダンス動画の取得

            if(flag == 1){
                storage.addUserVideo(savedVideo);//動画ストレージにあるuserVideo_arrayに保存する
                //インテントで動画を保存したときにファイル名を保存する。
                TextView fileName = (TextView)findViewById(R.id.userFileName);
                fileName.setText(savedVideo.toString());
            }
            if(flag == 2){
                storage.addOriginalVideo(savedVideo);//動画ストレージにあるoriginalVideo_arrayに保存する
                //インテントで動画を保存したときにファイル名を保存する。
                TextView fileName = (TextView)findViewById(R.id.originalFileName);
                fileName.setText(savedVideo.toString());
            }

        }
    }

    public void onEvaluateButtonTapped(View view) {
        Intent intent = new Intent(this, Loading.class);
        startActivity(intent);
    }

    //ホームボタン
    public void onHomeButtonTapped(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //動画アップロードされたときにその動画のファイル名を枠内に表示し、アップロードしたことを表す予定。

}
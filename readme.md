xml
///
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
xmlns:tools="http://schemas.android.com/tools"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:orientation="vertical"
android:layout_gravity="center"
android:gravity="center"
tools:context=".MainActivity">

    <ImageView
        android:id="@+id/clickToUploadImg"
        android:src="@drawable/click_to_upload_image"
        android:layout_width="match_parent"
        android:layout_height="300dp"/>
    <Button
        android:id="@+id/btnUpload"
        android:text="Upload Image"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />



</LinearLayout>
///

Main
///
package jp.ac.ritsumei.ise.phy.exp2is0667fe.local2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Base64; // Base64クラスをインポート

import androidx.activity.result.contract.ActivityResultContracts;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
Bitmap bitmap;
Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = findViewById(R.id.clickToUploadImg);
        Button button = findViewById(R.id.btnUpload);

        ActivityResultLauncher<Intent> activityResultLauncher=
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result){
                        if(result.getResultCode()== Activity.RESULT_OK){
                            Intent data=result.getData();
                            Uri uri=data.getData();
                            try {
                                bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                                imageView.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent= new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View View){
                ByteArrayOutputStream byteArrayOutputStream;
                byteArrayOutputStream=new ByteArrayOutputStream();
//                if(bitmap!=null){
//                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
//                    byte[] bytes=byteArrayOutputStream.toByteArray();
//                    final String base64Image=Base64.encodeToString(bytes,Base64.DEFAULT);
if (fileUri != null) {
// ファイルをアップロードする
uploadFileToServer(fileUri);
}else {
Toast.makeText(getApplicationContext(), "Select the image first", Toast.LENGTH_SHORT).show();
}
}
}
);
}
}
///

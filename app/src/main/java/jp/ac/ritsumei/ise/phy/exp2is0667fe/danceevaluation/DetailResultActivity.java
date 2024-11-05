package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.util.Log;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.DataSource;

import com.bumptech.glide.Glide;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.os.AsyncTask;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.util.Date;
import androidx.annotation.Nullable;

import java.net.URL;

public class DetailResultActivity extends AppCompatActivity {
    private final String bucket_name=BuildConfig.S3_BUCKET_NAME;
    private final String identity_pool_id=BuildConfig.S3_IDENTITY_POOL_ID;

    private String[] file_names=new String[5];
    private String[] file_URL=new String[5];
    private int url_index = 0;
    private int imagesLoaded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ditail_result);

        Intent intent=getIntent();
        String Result_id=intent.getStringExtra("Result_id");
        String MusicName = intent.getStringExtra("MusicName");
        String Score=intent.getStringExtra("Score");
        String Rank=intent.getStringExtra("UserRank");
        String Date=intent.getStringExtra("Date");

        //テキスト表示
        setText(MusicName, Score, Date);
        //ランク表示
        setRank(Rank);

        //画像表示
        setImageFileName(Result_id);
        setFileURL(file_names);
    }
    private void displayImages(){
        ImageView[] imageViews = {//id設定
                findViewById(R.id.graph),
                findViewById(R.id.userbestView),
                findViewById(R.id.originalbestView),
                findViewById(R.id.userworstView),
                findViewById(R.id.originalworstView)
        };

        for (int i = 0; i < imageViews.length; i++) {//画面表示
            Glide.with(DetailResultActivity.this)
                    .load(file_URL[i])
                    .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.d("image","Failed to load image: "+ e);
                return false; // デフォルトのエラー処理も実行する
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false; // 通常の成功時の処理
            }
        })
                .into(imageViews[i]);
    }
    }
    private void setText(String MusicName, String Score, String Date){
        Log.d("text",MusicName + Score + Date);
        TextView musicNameView = findViewById(R.id.musicNameView);
        musicNameView.setText(MusicName);

        TextView dateView = findViewById(R.id.dateView);
        dateView.setText(Date);

        TextView scoreView = findViewById(R.id.totalScoreView);
        scoreView.setText(Score);
    }

    private void setRank(String Rank){
        Log.d("rank", Rank);
        ImageView rankView = findViewById(R.id.rank);
        switch (Rank){
            case "god":
                rankView.setImageResource(R.drawable.god);
                break;
            case "center":
                rankView.setImageResource(R.drawable.center);
                break;
            case "backdancer":
                rankView.setImageResource(R.drawable.backdancer);
                break;
            case "practice":
                rankView.setImageResource(R.drawable.practice);
                break;
            case "normal":
                rankView.setImageResource(R.drawable.normal);
                break;
        }
    }

    public void setImageFileName(String Result_id){
        file_names[0] = Result_id+"_graph.png";

        file_names[1]=Result_id+"_user_best_shot.png";
        file_names[2]=Result_id+"_original_best_shot.png";

        file_names[3]=Result_id+"_user_worst_shot.png";
        file_names[4]=Result_id+"_original_worst_shot.png";
    }

    private void setFileURL(String file_names[]){
        for(int i = 0; i < file_names.length; i++){
            new ImageLoading().execute(file_names[i]);
        }
    }

    private class ImageLoading extends AsyncTask<String,Void,String>{
        @Override
        protected  String doInBackground(String... params){
            //Cognito認証
            CognitoCachingCredentialsProvider credentialsProvider=new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    identity_pool_id,
                    Regions.AP_NORTHEAST_1
            );

            AmazonS3 s3Client=new AmazonS3Client(credentialsProvider);

            GeneratePresignedUrlRequest urlRequest=new GeneratePresignedUrlRequest(bucket_name,params[0]);

            urlRequest.setExpiration(new Date(System.currentTimeMillis()+360000));

            URL presignedUrl=s3Client.generatePresignedUrl(urlRequest);

            return presignedUrl.toString();
        }

        @Override
        protected void onPostExecute(String result){
            if(result!=null){
                Log.d("imagesLoaded", Integer.toString(imagesLoaded));

                file_URL[url_index]=result;
                imagesLoaded++;

                if (imagesLoaded == file_URL.length) {
                    displayImages();
                }
            }else {
                Log.d("filename", "Failed to retrieve image URL.");
            }
            url_index++;
        }
    }

    //履歴に戻る
    public void backHistoryButtonTapped(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

}

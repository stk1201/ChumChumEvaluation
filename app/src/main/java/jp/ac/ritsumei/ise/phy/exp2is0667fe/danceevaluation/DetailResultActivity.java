package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

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
    private EditText score,rank;
    private final String bucket_name="chum-chum-s3";
    private final String identity_pool_id="  各自入力 ";

    private String[] file_names=new String[4];
    private String[] file_URL=new String[4];
    private int url_index=0;
    private int imagesLoaded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ditail_result);

        Intent intent=getIntent();
        String Result_id=intent.getStringExtra("Result_id");
        String Score=intent.getStringExtra("Score");
        String Rank=intent.getStringExtra("UserRank");
        String Date=intent.getStringExtra("Date");

        setImageFileName(Result_id);//ファイル名を設定
        System.out.println("Rank"+Rank+" Result_id "+Result_id);
        setFileURL(file_names);//ファイルURLを設定
        setScore_Rate_Date(Score,Rank,Date);

    }
    private void displayImages(){
        ImageView[] imageViews = {//id設定
                findViewById(R.id.userbest),
                findViewById(R.id.originalbest),
                findViewById(R.id.userworst),
                findViewById(R.id.originalworst)
        };
        for (int i = 0; i < imageViews.length; i++) {//画面表示
            Glide.with(DetailResultActivity.this)
                    .load(file_URL[i])
                    .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                System.out.println("Failed to load image: "+ e);
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
    private void setScore_Rate_Date(String Score,String Rank,String Date){
        TextView Score_Rank_Date=findViewById(R.id.textViewDate_Score_Rank);
        Score_Rank_Date.setText("日付："+Date+"\n得点:"+ Score+" \nランク:"+Rank);

    }
    public void setImageFileName(String Result_id){
        file_names[0]=Result_id+"_user_best_shot.png";
        file_names[1]=Result_id+"_original_best_shot.png";

        file_names[2]=Result_id+"_user_worst_shot.png";
        file_names[3]=Result_id+"_original_worst_shot.png";
    }
    public void backHomeButtonTapped(View view) {//ホームに戻る
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
    private void setFileURL(String file_names[]){
            for(int i=0;i<4;i++){//URLを取得
                new ImageLoading().execute(file_names[i]);
            }
    }private class ImageLoading extends AsyncTask<String,Void,String>{
        @Override
        protected  String doInBackground(String... params){
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
        protected void onPostExecute(String result){//バックグラウンド処理後の関数
            if(result!=null){
//                System.out.println("Image URL retrieved successfully: " + result);
                file_URL[url_index]=result;
                imagesLoaded++;
                if (imagesLoaded == 4) {
                    displayImages();
                }
            }else {
                System.out.println("Failed to retrieve image URL.");
            }
            url_index++;
        }
    }

}

package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SaveResult {
    private final Activity activity;

    private UserStocker userStocker;
    private ResultStocker resultStocker;
    private OkHttpClient client = new OkHttpClient();
    private String resultId = null;

    public SaveResult(Activity activity) {
        this.activity = activity;
    }

    public void saving(){
        userStocker = userStocker.getInstance(activity);
        resultStocker = resultStocker.getInstance(activity);

        String url = BuildConfig.SAVE_RESULT_API;

        String resultJson = getJson(userStocker, resultStocker);
        RequestBody body = RequestBody.create(
                resultJson, MediaType.get("application/json; charset=utf-8")
        );

        //HTTP POSTリクエストの作成
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        //リクエスト送信
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(() ->
                        Toast.makeText(activity, "Save failed", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        resultId = jsonResponse.getString("resultId");

                        // result_list作成に成功した時の処理
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, "Save successful", Toast.LENGTH_SHORT).show();

                            //画像をs3へ保存
                            saveImage(0, resultStocker.getBestShot()[0]);
                            saveImage(1, resultStocker.getBestShot()[1]);
                            saveImage(2, resultStocker.getWorstShot()[0]);
                            saveImage(3, resultStocker.getWorstShot()[1]);
                            saveImage(4, resultStocker.getGraph());
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        activity.runOnUiThread(() ->
                                Toast.makeText(activity, "Failed to parse response", Toast.LENGTH_SHORT).show()
                        );
                    }

                } else {
                    activity.runOnUiThread(() ->
                            Toast.makeText(activity, "Save failed: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private String getJson(UserStocker userStocker, ResultStocker resultStocker){
        String jsonData = "{\"user_id\": " + userStocker.getUserId()
                + ", \"music_name\": \"" + resultStocker.getMusicName()
                + ", \"score\": \"" + resultStocker.getTotalScore()
                + ", \"rank\": \"" + resultStocker.getRank()
                + "\"}";
        return jsonData;
    }

    private void saveImage(int flag, Bitmap resultImage){
        final String BUCKET_NAME = BuildConfig.S3_BUCKET_NAME;
        final String IDENTITY_POOL_ID = BuildConfig.S3_IDENTITY_POOL_ID;
        String fileName;

        switch (flag){
            case 0:
                fileName = resultId + "_user_best_shot.png";
                break;
            case 1:
                fileName = resultId + "_original_best_shot.png";
                break;
            case 2:
                fileName = resultId + "_user_worst_shot.png";
                break;
            case 3:
                fileName = resultId + "_original_worst_shot.png";
                break;
            case 4:
                fileName = resultId + "_graph.png";
                break;
            default:
                throw new IllegalArgumentException("無効なフラグ値: " + flag);
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<String> future = executor.submit(() -> {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                // 画像を圧縮してByteArrayInputStreamに変換
                resultImage.compress(Bitmap.CompressFormat.WEBP, 80, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(byteArray);

                // Cognitoで認証情報を取得
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        activity,
                        IDENTITY_POOL_ID,
                        Regions.AP_NORTHEAST_1
                );

                AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);

                // S3にアップロード
                PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, fileName, inputStream, new ObjectMetadata());
                s3Client.putObject(putObjectRequest);

                // アップロードしたファイルのURLを取得
                return s3Client.getUrl(BUCKET_NAME, fileName).toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        executor.execute(() -> {
            try {
                String result = future.get(); // 非同期結果を取得
                if (result != null) {
                    System.out.println("アップロード成功: " + result);
                    // APIにURLを送信する処理をここに追加
                } else {
                    System.out.println("アップロード失敗");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdown(); // Executorを終了
            }
        });
    }
}

package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SaveResult {
    private Context context;
    private UserStocker userStocker;
    private ResultStocker resultStocker;
    private File[] filePaths = new File[5];
    private OkHttpClient client = new OkHttpClient();

    public SaveResult(Context context) {
        this.context = context.getApplicationContext();
    }

    public void saving(){
        userStocker = userStocker.getInstance(context);
        resultStocker = resultStocker.getInstance(context);
        String url = "https://tb78lilb8f.execute-api.ap-northeast-1.amazonaws.com/chum/result/register";

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
                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Save failed", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String resultId = jsonResponse.getString("id");

                        // result_list作成に成功した時の処理
                        ((Activity) context).runOnUiThread(() -> {
                            Toast.makeText(context, "Save successful", Toast.LENGTH_SHORT).show();

                            //画像をS3へ保存
                            saveImages(resultId);

                            //画像の削除
                            deleteImages();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ((Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Failed to parse response", Toast.LENGTH_SHORT).show()
                        );
                    }

                } else {
                    ((Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Save failed: " + response.code(), Toast.LENGTH_SHORT).show()
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
                + ", \"graph\": \"" + filePaths[4].toString()
                + "\"}";
        return jsonData;
    }

    private File bitmapToPng(Bitmap bitmap, String filename){
        File file = new File(this.context.getExternalFilesDir(null) + "/" + filename + ".png");
        Log.d("posemaker", "url:" + file);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveImages(String resultId){
        String url = "https://tb78lilb8f.execute-api.ap-northeast-1.amazonaws.com/chum/upload/image_binary";

        for (int i = 0; i < filePaths.length; i++) {
            String imageJson = getImageJson(i, resultId);
            RequestBody body = RequestBody.create(
                    imageJson, MediaType.get("application/json; charset=utf-8")
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
                    ((Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Save failed", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (! response.isSuccessful()) {
                        ((Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Save failed: " + response.code(), Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });
        }

    }

    private String getImageJson(int i, String resultId){
        String imageType = null;
        Bitmap image = null;
        switch (i){
            case 0:
                imageType = "userBestShot";
                image = resultStocker.getBestShot()[0];
                break;
            case 1:
                imageType = "originalBestShot";
                image = resultStocker.getBestShot()[1];
                break;
            case 2:
                imageType = "userWorstShot";
                image = resultStocker.getWorstShot()[0];
                break;
            case 3:
                imageType = "originalWorstShot";
                image = resultStocker.getWorstShot()[1];
                break;
            case 4:
                imageType = "scoreGraph";
                image = resultStocker.getGraph();
                break;
        }

        File filePath = bitmapToPng(image, imageType + "_" + resultId);
        filePaths[i] = filePath;

        String jsonData = "{\"local_image_path\": " + filePath
                + ", \"s3_upload_name\": \"" + imageType + "_" + resultId
                + "\"}";
        return jsonData;

    }

    private void deleteImages(){
        for (File path : filePaths) {
            if (path.exists()) {
                path.delete(); // 画像を削除
            }
        }

        filePaths = new File[5];
    }
}

package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
        String url = "https://admgumzyeb.execute-api.ap-northeast-1.amazonaws.com/test/result_create";

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
                    // ログイン成功時の処理
                    ((Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Save successful", Toast.LENGTH_SHORT).show();

                        //画像の削除
                        deleteImages();
                    });
                } else {
                    ((Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Save failed: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private String getJson(UserStocker userStocker, ResultStocker resultStocker){
        filePaths[0] = bitmapToPng(resultStocker.getBestShot()[0], "userBestShot");
        filePaths[1] = bitmapToPng(resultStocker.getBestShot()[1], "originalBestShot");
        filePaths[2] = bitmapToPng(resultStocker.getWorstShot()[0], "userWorstShot");
        filePaths[3] = bitmapToPng(resultStocker.getWorstShot()[1], "originalWorstShot");
        filePaths[4] = bitmapToPng(resultStocker.getGraph(), "scoreGraph");

        String jsonData = "{\"user_id\": " + userStocker.getUserId()
                + ", \"music_name\": \"" + resultStocker.getMusicName()
                + ", \"score\": \"" + resultStocker.getTotalScore()
                + ", \"user_best_shot\": \"" + filePaths[0].toString()
                + ", \"original_best_shot\": \"" + filePaths[1].toString()
                + ", \"user_worst_shot\": \"" + filePaths[2].toString()
                + ", \"original_worst_shot\": \"" + filePaths[3].toString()
                + ", \"rank\": \"" + resultStocker.getRank()
                + ", \"graph\": \"" + filePaths[4].toString()
                + "\"}";
        return jsonData;
    }

    private File bitmapToPng(Bitmap bitmap, String filename){
        File file = new File(this.context.getExternalFilesDir(null) + "/" + filename + ".png");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            return file
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

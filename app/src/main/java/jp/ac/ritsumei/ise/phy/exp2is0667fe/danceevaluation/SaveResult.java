package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;

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
    private File[] filePaths = new File[5];
    private OkHttpClient client = new OkHttpClient();

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

//                    try {
//                        JSONObject jsonResponse = new JSONObject(responseData);
//                        String resultId = jsonResponse.getString("id");

                        // result_list作成に成功した時の処理
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, "Save successful", Toast.LENGTH_SHORT).show();

                        });
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        ((Activity) context).runOnUiThread(() ->
//                                Toast.makeText(context, "Failed to parse response", Toast.LENGTH_SHORT).show()
//                        );
//                    }

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
}

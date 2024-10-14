package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;


public class History_Activity extends AppCompatActivity {

    private EditText editUserID, editMusicName;
    private OkHttpClient client = new OkHttpClient();
    private Spinner spinnerSortBy;
    private RecyclerView recyclerView;
    private History_ResponseAdapter adapter;
    private List<History_ApiResponseItem> responseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // ビューの初期化
        editUserID = findViewById(R.id.editUserID);
        editMusicName = findViewById(R.id.editMusicName);
        spinnerSortBy = findViewById(R.id.spinnerSortBy);
        recyclerView = findViewById(R.id.recyclerView);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        // RecyclerViewの設定
        adapter = new History_ResponseAdapter(responseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // ボタンのクリックリスナーを設定
        buttonSubmit.setOnClickListener(v -> sendApiRequest());
    }

    // APIリクエストを送信するメソッド
    private void sendApiRequest() {
        String url ="https://admgumzyeb.execute-api.ap-northeast-1.amazonaws.com/test/results/sort";
        String userID = editUserID.getText().toString().trim();
        String musicName = editMusicName.getText().toString().trim();
        String sortBy = spinnerSortBy.getSelectedItem() != null ? spinnerSortBy.getSelectedItem().toString() : "";

        // デフォルト値の設定
        if (userID.isEmpty()) {
            userID = "7"; // デフォルト値として1を設定
        }
        if (musicName.isEmpty()) {
            musicName = "Symphony No.5"; // 必要に応じてデフォルトの音楽名を設定
        }
        if (sortBy.isEmpty()) {
            sortBy = "Score"; // デフォルトのソート基準を設定
        }

        // リクエストボディを作成
        History_RequestBody requestBody = new History_RequestBody(userID, musicName, sortBy);
        String json = "{"
                + "\"UserID\": \"" + requestBody.getUserID() + "\", "
                + "\"MusicName\": \"" + requestBody.getMusicName() + "\", "
                + "\"SortBy\": \"" + requestBody.getSortBy() + "\""
                + "}";

        RequestBody body = RequestBody.create(
                json, MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(History_Activity.this, "Login failed", Toast.LENGTH_SHORT).show()
                );
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        // レスポンスデータをJSONObjectとしてパース
                        JSONObject parsedData = new JSONObject(responseData);

                        // 'body'フィールドを取得して再パース
                        String bodyString = parsedData.getString("body");
                        JSONArray bodyArray = new JSONArray(bodyString);
//                        Log.d("bodyArray="+bodyArray.length());

                        // 各オブジェクトを{}ごとに区切って表示
                        for (int i = 0; i < bodyArray.length(); i++) {
                            JSONObject item = bodyArray.getJSONObject(i);
                            Log.d("History_Activity", "{" + "\n" + item.toString(4) + "\n" + "}");

                            // 区切り線を表示
                            Log.d("History_Activity", "---");

                        }
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                        // JSONExceptionが発生した場合の処理
                        runOnUiThread(() -> Toast.makeText(History_Activity.this, "JSON parsing error", Toast.LENGTH_SHORT).show());
                    }

                    System.out.println(responseData);
                    // ログイン成功時の処理
                    runOnUiThread(() -> {
                        Toast.makeText(History_Activity.this, " successful", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(History_Activity.this, "failed: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });

    }
}

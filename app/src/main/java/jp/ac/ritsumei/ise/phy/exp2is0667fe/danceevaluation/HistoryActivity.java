package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ListView;
import android.view.View;
import android.view.ViewGroup;

import android.widget.SimpleAdapter;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HistoryActivity extends AppCompatActivity {
    private EditText editUserID, editMusicName;
    private OkHttpClient client = new OkHttpClient();
    private Spinner spinnerSortBy;
    private ListView listView;
    private HistoryResponseAdapter adapter;

    private List<Map<String,String>> resultList=new ArrayList<>();
    private List<HistoryApiResponseItem> responseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // ビューの初期化
        editMusicName = findViewById(R.id.editMusicName);
        spinnerSortBy = findViewById(R.id.spinnerSortBy);
        listView = findViewById(R.id.result_list);
        ImageButton buttonSubmit = findViewById(R.id.buttonSubmit);

        // ボタンのクリックリスナーを設定
        buttonSubmit.setOnClickListener(v -> sendApiRequest());

        Log.d("history",resultList.toString());
    }


    // APIリクエストを送信するメソッド
    private void sendApiRequest() {
        String url =BuildConfig.GET_HISTORYLIST_API;
        String userID = "0";
        String musicName = editMusicName.getText().toString().trim();
        String sortBy = spinnerSortBy.getSelectedItem() != null ? spinnerSortBy.getSelectedItem().toString() : "";

        // デフォルト値の設定
        if (userID.isEmpty()) {
            Toast.makeText(HistoryActivity.this, "ユーザーIDを入力してください", Toast.LENGTH_SHORT).show();
//            userID = "7"; // デフォルト値として1を設定
        }
        if (musicName.isEmpty()) {
//            musicName = "Symphony No.5"; // 必要に応じてデフォルトの音楽名を設定
//            Toast.makeText(HistoryActivity.this, "ユーザーIDを入力してください", Toast.LENGTH_SHORT).show();
        }
        if (sortBy.isEmpty()) {
            sortBy = "Score"; // デフォルトのソート基準を設定
        }

        // リクエストボディを作成
        HistoryRequestBody requestBody = new HistoryRequestBody(userID, musicName, sortBy);
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
                        Toast.makeText(HistoryActivity.this, "Login failed", Toast.LENGTH_SHORT).show()
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

                            // 1つの計算結果から,曲名・得点・日時,result_isを抽出
                            String MusicName = item.getString("music_name");
                            String Score = item.getString("score");
                            String Date = item.getString("date");
                            String Result_id=item.getString("result_id");
                            String UserRank=item.getString("user_rank");

                            // リストに一時保存
                            Map<String,String> data = new HashMap<>();
                            data.put("MusicName",MusicName);
                            data.put("Score",Score);
                            data.put("Date",Date);
                            data.put("Result_id",Result_id);
                            data.put("UserRank",UserRank);

                            //おおもとのリストに1つの計算結果の曲名・得点・日時を記録
                            resultList.add(data);

                        }

                        runOnUiThread(() -> {
                            SimpleAdapter adapter = new SimpleAdapter(
                                    HistoryActivity.this, // ここをgetApplicationContext()からHistoryActivity.thisに変更
                                    resultList,
                                    R.layout.listview_layout_history,
                                    new String[]{"MusicName", "Score", "Date"}, // ここもデータのキー名を適切に設定
                                    new int[]{R.id.song_name, R.id.score, R.id.date}
                            ){
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);

                                    Button detailButton = view.findViewById(R.id.buttonDetail);
                                    if ( detailButton != null) {
                                        detailButton.setOnClickListener(v -> {
                                            // アクションをここに記述
                                            String musicName = resultList.get(position).get("MusicName");
                                            String score=resultList.get(position).get("Score");
                                            String date=resultList.get(position).get("Date");
                                            String result_id=resultList.get(position).get("Result_id");
                                            String UserRank=resultList.get(position).get("UserRank");
                                            Toast.makeText(HistoryActivity.this, "詳細を表示: " + musicName, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(HistoryActivity.this, DetailResultActivity.class);
                                            intent.putExtra("MusicName", musicName);
                                            intent.putExtra("Score", score);
                                            intent.putExtra("Date", date);
                                            intent.putExtra("Result_id", result_id);
                                            intent.putExtra("UserRank", UserRank);
                                            startActivity(intent);
                                        });
                                    } else {
                                        Log.e("HistoryActivity", "actionButton is null");
                                    }
                                    return view;
                                }
                            };
                            listView.setAdapter(adapter);
                        });

                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                        // JSONExceptionが発生した場合の処理
                        runOnUiThread(() -> Toast.makeText(HistoryActivity.this, "JSON parsing error", Toast.LENGTH_SHORT).show());
                    }

                    System.out.println(responseData);
                    // 検索成功時の処理
                    runOnUiThread(() -> {
                        Toast.makeText(HistoryActivity.this, " successful", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(HistoryActivity.this, "failed: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }

        });

    }

}
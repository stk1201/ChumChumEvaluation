//package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//
//public class MainActivity extends AppCompatActivity {
//    //アプリが開かれたときに最初に出てくるホーム画面。
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//
//    //アップロード画面に遷移
//    public void onStartButtonTapped(View view) {
//        Intent intent = new Intent(this, Uploading.class);
//        startActivity(intent);
//    }
//}
package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText userIdInput;
    private EditText passwordInput;

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // EditTextの初期化
        userIdInput = findViewById(R.id.userIdInput);
        passwordInput = findViewById(R.id.passwordInput);
    }

    // アップロード画面に遷移するメソッド
    public void onStartButtonTapped(View view) {
        String userId = userIdInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (userId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "User ID and Password are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // ログインリクエストを送信
        sendLoginRequest(userId, password);
    }

    // ログインリクエストを送信するメソッド
    private void sendLoginRequest(String userId, String password) {
        String url = "https://admgumzyeb.execute-api.ap-northeast-1.amazonaws.com/test/new_login";

        // JSON データを作成
        String json = "{\"user_id\": " + userId + ", \"password\": \"" + password + "\"}";

        RequestBody body = RequestBody.create(
                json, MediaType.get("application/json; charset=utf-8")
        );

        // HTTP POST リクエストを作成
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // 非同期でリクエストを送信
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // ログイン成功時の処理
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        // 次の画面に遷移
                        Intent intent = new Intent(MainActivity.this, Uploading.class);
                        startActivity(intent);
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}

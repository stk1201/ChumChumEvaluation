package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;


public class LoginActivity extends AppCompatActivity {
    private Context context;

    private EditText emailAddressInput;
    private EditText passwordInput;

    private OkHttpClient client = new OkHttpClient();

    private UserStocker userStocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        // EditTextの初期化
        emailAddressInput = findViewById(R.id.emailAddressInput);
        passwordInput = findViewById(R.id.passwordInput);
} // アップロード画面に遷移するメソッド
    public void onStartButtonTapped(View view) {
//        String userId = userIdInput.getText().toString();
        String emailAddress = emailAddressInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (emailAddress.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "メールアドレスとパスワードを入力してください。", Toast.LENGTH_SHORT).show();
            return;
        }

        // ログインリクエストを送信
        sendLoginRequest(emailAddress, password);
    }

    // ログインリクエストを送信するメソッド
    private void sendLoginRequest(String emailAddress, String password) {
        String url = BuildConfig.LOGIN_API;
        System.out.println( url);
        // JSON データを作成
        String innerJson = "{\"email_address\": \"" + emailAddress + "\", \"password\": \"" + password + "\"}";
        String json = "{\"body\": " + JSONObject.quote(innerJson) + "}";
        Log.d("json", json);
        System.out.println("RequestBody: " + json);
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
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show()
                );
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("respinse", responseData);
                    System.out.println("RequestBody: " + json);

                    // ログイン成功時の処理
                    try {
                        JSONObject outerJson = new JSONObject(responseData);
                        String body = outerJson.getString("body");

                        Log.d("respinsebody", body);
                        System.out.println("RequestBody: " + json);

                        JSONObject innerJson = new JSONObject(body);
                        int userIdInt = innerJson.getInt("user_id");

                        // result_list作成に成功した時の処理
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            System.out.println("result_list作成に成功");
                            //UserStockerに保存
                            userStocker = userStocker.getInstance(context);
                            if(userStocker != null){
                                userStocker.setUserInfo(userIdInt, emailAddress);
                            }

                            // 次の画面に遷移
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            System.out.println("ホーム画面に移行");
                            startActivity(intent);

                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this, "Failed to parse response", Toast.LENGTH_SHORT).show()
                        );
                    }

                } else {
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }


    // 新規登録画面に遷移するメソッド
    public void onStartCreateButtonTapped(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}



//package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;
//import android.os.Bundle;
//import android.content.Intent;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import org.json.JSONObject;
//import java.io.IOException;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class DeleteUserInfoActivity extends AppCompatActivity {
//    private EditText editTextEmail;
//    private EditText editTextPassword;
//    private OkHttpClient client;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_delete_input_info); // XMLファイル名に合わせて変更
//        client = new OkHttpClient();
//
//
//        editTextEmail = findViewById(R.id.editTextEmail);
//        editTextPassword = findViewById(R.id.editTextPassword);
//        Button buttonDelete = findViewById(R.id.buttonDelete);
//        Button buttonCancel = findViewById(R.id.buttonCancel);
//
//        buttonDelete.setOnClickListener(v -> deleteUserInfo());
//        buttonCancel.setOnClickListener(v -> {
//            Intent intent = new Intent(DeleteUserInfoActivity.this, HomeActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish(); // Close this activity
//        });
//    }
//
//    private void deleteUserInfo() {
//        String email = editTextEmail.getText().toString();
//        String password = editTextPassword.getText().toString();
//
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "メールアドレスとパスワードを入力してください", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // JSONボディの作成
//        String innerJson = String.format("{\"email_address\": \"%s\", \"password\": \"%s\"}", email, password);
//        JSONObject outerJson = new JSONObject();
//        try {
//            outerJson.put("body", innerJson);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//
//        // リクエストボディの作成
//        RequestBody requestBody = RequestBody.create(
//                outerJson.toString(), MediaType.get("application/json; charset=utf-8"));
//
//        // リクエストの作成
//        String url = "https://tb78lilb8f.execute-api.ap-northeast-1.amazonaws.com/chum/delete";
//        System.out.println("URL: "+ url);
//        System.out.println("Body: "+ outerJson.toString());
//        Request request = new Request.Builder()
//                .url(url)
//                .delete(requestBody)
//                .build();
//
//        // 非同期リクエストを送信
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                System.out.println("リクエスト失敗: "+ e);
//                runOnUiThread(() ->
//                        Toast.makeText(DeleteUserInfoActivity.this, "削除リクエストが失敗しました", Toast.LENGTH_SHORT).show());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String responseData = response.body().string();
//                System.out.println("レスポンスコード: " + response.code());
//                System.out.println("レスポンスデータ: " + responseData);
//                runOnUiThread(() -> {
//                    if (response.isSuccessful()) {
//                        Toast.makeText(DeleteUserInfoActivity.this, "ユーザー情報が削除されました", Toast.LENGTH_SHORT).show();
//                        // 必要に応じて他の処理を追加
//                    } else {
//                        Toast.makeText(DeleteUserInfoActivity.this, "削除リクエスト失敗: " + response.code(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
//}
package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeleteUserInfoActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_input_info); // XMLファイル名に合わせて変更

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonDelete = findViewById(R.id.buttonDelete);
        Button buttonCancel = findViewById(R.id.buttonCancel);

        buttonDelete.setOnClickListener(v -> deleteUserInfo());
        buttonCancel.setOnClickListener(v -> {
            Intent intent = new Intent(DeleteUserInfoActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void deleteUserInfo() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "メールアドレスとパスワードを入力してください", Toast.LENGTH_SHORT).show();
            return;
        }

        // JSONボディの作成
        JSONObject outerJson = new JSONObject();
        try {
            JSONObject innerJson = new JSONObject();
            innerJson.put("email_address", email);
            innerJson.put("password", password);
            outerJson.put("body", innerJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // リクエストボディの作成
        RequestBody requestBody = RequestBody.create(
                outerJson.toString(), MediaType.get("application/json; charset=utf-8")
        );

        // リクエストの作成
        String url = BuildConfig.DELETE_ACCOUNT_API;
        System.out.println( "URL: " + url);
        System.out.println("Request Body: " + outerJson.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 非同期リクエストを送信
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("DeleteUserInfoActivity", "リクエスト失敗: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(DeleteUserInfoActivity.this, "削除リクエストが失敗しました", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                System.out.println( "レスポンスコード: " + response.code());
                System.out.println( "レスポンスデータ: " + responseData);

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(DeleteUserInfoActivity.this, "ユーザー情報が削除されました", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DeleteUserInfoActivity.this, LoginActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(DeleteUserInfoActivity.this, "削除リクエスト失敗: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

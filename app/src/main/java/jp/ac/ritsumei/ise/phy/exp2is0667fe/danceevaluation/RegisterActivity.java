package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class RegisterActivity extends AppCompatActivity {
    private EditText emailInput;
    private EditText passwordInput;
    private EditText userNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // EditText フィールドを取得
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        userNameInput = findViewById(R.id.userNameInput);
    }
    // ボタンが押されたときに呼ばれるメソッド
    public void onRegisterButtonTapped(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String userName = userNameInput.getText().toString();

        // フィールドが空の場合、警告を表示
        if (email.isEmpty() || password.isEmpty() || userName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // APIリクエストを非同期で行う
        new RegisterUserTask(email, password, userName).execute();
    }

    // 非同期タスクを作成してAPIリクエストを送信
    private class RegisterUserTask extends AsyncTask<Void, Void, String> {
        private String email;
        private String password;
        private String userName;

        public RegisterUserTask(String email, String password, String userName) {
            this.email = email;
            this.password = password;
            this.userName = userName;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String apiUrl = "https://admgumzyeb.execute-api.ap-northeast-1.amazonaws.com/test/user_create";
            HttpURLConnection connection = null;
            try {
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setDoOutput(true);

                // JSONオブジェクトを作成
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("email_address", email);
                jsonBody.put("password", password);
                jsonBody.put("user_name", userName);

                // JSONデータを送信
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonBody.toString());
                writer.flush();
                writer.close();
                os.close();

                // レスポンスを受け取る
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();
                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // レスポンスの結果を表示
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Response: " + result, Toast.LENGTH_LONG).show());
        }
    }
}

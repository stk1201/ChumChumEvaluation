package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
    private EditText passwordInput2;
    private EditText userNameInput;

    private UserStocker userStocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // EditText フィールドを取得
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        passwordInput2 = findViewById(R.id.passwordInput2);
        userNameInput = findViewById(R.id.userNameInput);
    }
    public void onLoginBackButtonTapped(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);

    }
    // ボタンが押されたときに呼ばれるメソッド
    public void onRegisterButtonTapped(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String password2 = passwordInput2.getText().toString();
        String userName = userNameInput.getText().toString();

        // フィールドが空の場合、警告を表示
        if (email.isEmpty() || password.isEmpty() || userName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(password2)) {
            Toast.makeText(this, "パスワードが一致していません", Toast.LENGTH_SHORT).show();
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
            String apiUrl = BuildConfig.CREATE_ACCOUNT_API;
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
                    Log.d("loginrespomse",response.toString());

                    int userId;
                    String mail_address;

                    //userIdの取得
                    try {
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        String body = jsonResponse.optString("body");
                        if (!body.isEmpty()) {
                            JSONObject innerJson = new JSONObject(body);
                            userId = innerJson.getInt("user_id");
                        } else {
//                            userId = jsonResponse.getInt("user_id");
////                            mail_address = jsonResponse.getString("mail_address");
//                            Log.d("userid", "UserID: " + userId);
//                            System.out.println("UserID: " + userId);
//                            System.out.println("mail_address: " + mail_address);
                            return "Error: No user_id found in response";
                        }
                        System.out.println( "UserID: " + userId);
                        userStocker = UserStocker.getInstance(RegisterActivity.this);
                        if (userStocker != null) {
                            userStocker.setUserInfo(userId, email);
                        }
                        return response.toString();

                    } catch (JSONException e) {
                        System.out.println( "Failed to parse JSON"+ e);
                        return "Error: JSON parsing failed";
                    }

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
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "登録完了", Toast.LENGTH_SHORT).show());
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // 遷移先のアクティビティをスタックのトップに配置し、不要なアクティビティを削除
            startActivity(intent);
            String userInfo = userStocker.getUserInfo();
            System.out.println(userInfo);

            // RegisterActivity を終了して、戻るボタンでこのアクティビティに戻れないようにする
            finish();

        }
    }
}

package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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

        // 入力されたデータを次のアクティビティに渡すためのIntent
        Intent intent = new Intent(this, NextActivity.class);
        intent.putExtra("email_address", email);
        intent.putExtra("password", password);
        intent.putExtra("user_name", userName);

        // 次の画面に遷移
        startActivity(intent);
    }
}


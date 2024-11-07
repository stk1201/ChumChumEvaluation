package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeleteUserDataDialogFragment extends DialogFragment {
    private UserStocker userStocker;
    private OkHttpClient client = new OkHttpClient(); // OkHttpClientインスタンスを作成

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.dialog_title);
        builder.setMessage(R.string.dialog_message);

        DialogClickListener listener = new DialogClickListener();
        builder.setPositiveButton(R.string.dialog_button_ok, listener);
        builder.setNegativeButton(R.string.dialog_button_cancel, listener);

        // AlertDialog を作成
        AlertDialog dialog = builder.create();

        // アニメーションを無効化
        if (dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(0);
        }

        return dialog;
    }

    private class DialogClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int buttonId) {
            if (buttonId == DialogInterface.BUTTON_POSITIVE) {
                showInputDialog();
            } else if (buttonId == DialogInterface.BUTTON_NEGATIVE) {
                Toast.makeText(getActivity(), "キャンセルされました", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ユーザーに入力を求めるダイアログ
    private void showInputDialog() {
        AlertDialog.Builder inputDialogBuilder = new AlertDialog.Builder(getActivity());
        inputDialogBuilder.setTitle("パスワードの入力");

        // カスタムビューとしてEditTextをセット
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input, null);
        EditText editTextUserId = dialogView.findViewById(R.id.editTextPassword);

        inputDialogBuilder.setView(dialogView);

        // OKボタンを押したときの処理
        inputDialogBuilder.setPositiveButton("送信", (dialog, which) -> {
            String userId = editTextUserId.getText().toString();
            if (!userId.isEmpty()) {
                sendDeleteRequest(userId);  // 入力されたuserIdを使ってDELETEリクエストを送信
            } else {
                if (isAdded()) {
                    Toast.makeText(getActivity(), "パスワードを入力してください", Toast.LENGTH_SHORT).show();
                }
            }
        });

        inputDialogBuilder.setNegativeButton("キャンセル", (dialog, which) -> dialog.dismiss());
        inputDialogBuilder.create().show();
    }

    // DELETEリクエストを送信するメソッド
    private void sendDeleteRequest(String password) {
        String url = "https://tb78lilb8f.execute-api.ap-northeast-1.amazonaws.com/chum/delete";

        userStocker = UserStocker.getInstance(getActivity());
        String emailAddress = userStocker.getEmailAddress();
        // ボディを作成
        JSONObject json = new JSONObject();
        try {
            JSONObject innerBody = new JSONObject();
            innerBody.put("email_address", emailAddress);
            innerBody.put("password", password);
            json.put("body", innerBody.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .build();

        // 非同期でリクエストを送信
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireActivity(), "APIリクエストに失敗しました", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (isAdded() && getActivity() != null) {
                    requireActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getActivity(), "データが削除されました", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            requireActivity().overridePendingTransition(0, 0);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "APIリクエスト失敗: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}

package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class HistoryAdapter extends ArrayAdapter<Map<String, String>> {
    private Context context;
    private List<Map<String, String>> resultList;

    public HistoryAdapter(Context context, List<Map<String, String>> resultList) {
        super(context, 0, resultList);
        this.context = context;
        this.resultList = resultList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // インフレート
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_layout_history, parent, false);
        }

        // 各ビューの取得
        TextView songName = convertView.findViewById(R.id.song_name);
        TextView score = convertView.findViewById(R.id.score);
        TextView date = convertView.findViewById(R.id.date);
        Button detailButton = convertView.findViewById(R.id.buttonDetail);

        // データの設定
        Map<String, String> item = resultList.get(position);
        songName.setText(item.get("MusicName"));
        score.setText(item.get("Score"));
        date.setText(item.get("Date"));

        // ボタンのクリックリスナー設定
        detailButton.setOnClickListener(v -> {
            String musicName = item.get("MusicName");
            Toast.makeText(context, "詳細を表示: " + musicName, Toast.LENGTH_SHORT).show();
            // 詳細表示の処理をここに追加
        });

        return convertView;
    }
}

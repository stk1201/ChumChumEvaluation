package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class History_ResponseAdapter extends RecyclerView.Adapter<History_ResponseAdapter.ResponseViewHolder> {
    private final List<History_ApiResponseItem> responseList;

    // コンストラクタでデータのリストを受け取ります
    public History_ResponseAdapter(List<History_ApiResponseItem> responseList) {
        this.responseList = responseList;
    }

    @NonNull
    @Override
    public ResponseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // アイテムのレイアウトを作成します
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_response, parent, false);
        return new ResponseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponseViewHolder holder, int position) {
        // 各アイテムのデータを設定します
        History_ApiResponseItem item = responseList.get(position);
        holder.textViewDate.setText(item.getDate());
        holder.textViewScore.setText("Score: " + item.getScore());
        holder.textViewMusicName.setText("Music: " + item.getMusicName());
    }

    @Override
    public int getItemCount() {
        // リストのアイテム数を返します
        return responseList.size();
    }

    // ビューホルダーのクラス
    static class ResponseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewScore, textViewMusicName;

        ResponseViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewScore = itemView.findViewById(R.id.textViewScore);
            textViewMusicName = itemView.findViewById(R.id.textViewMusicName);
        }
    }
}

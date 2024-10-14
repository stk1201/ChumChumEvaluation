package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

public class History_ApiResponseItem {
    private String date;
    private int score;
    private String musicName;

    // コンストラクタ
    public History_ApiResponseItem(String date, int score, String musicName) {
        this.date = date;
        this.score = score;
        this.musicName = musicName;
    }

    // ゲッターメソッド
    public String getDate() {
        return date;
    }

    public int getScore() {
        return score;
    }

    public String getMusicName() {
        return musicName;
    }
}

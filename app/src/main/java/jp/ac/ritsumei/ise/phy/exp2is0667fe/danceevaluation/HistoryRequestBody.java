package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.widget.Switch;

public class HistoryRequestBody {
    private String UserID;
    private String MusicName;
    private String SortBy;

    public HistoryRequestBody(String userID, String musicName, String sortBy) {
        this.UserID = userID;
        this.MusicName = musicName;

        switch(sortBy){
            case "日付順":
                this.SortBy = "Date";
                break;
            default:
                this.SortBy = "Score";
                break;
        }
    }

    public String getUserID() {
        return UserID;
    }

    public String getMusicName() {
        return MusicName;
    }

    public String getSortBy() {
        return SortBy;
    }
    @Override
    public String toString() {
        return "{" +
                "UserID='" + UserID + '\'' +
                ", MusicName='" + MusicName + '\'' +
                ", SortBy='" + SortBy + '\'' +
                '}';
    }
}

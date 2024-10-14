package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

public class History_RequestBody {
    private String UserID;
    private String MusicName;
    private String SortBy;

    public History_RequestBody(String userID, String musicName, String sortBy) {
        this.UserID = userID;
        this.MusicName = musicName;
        this.SortBy = sortBy;
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

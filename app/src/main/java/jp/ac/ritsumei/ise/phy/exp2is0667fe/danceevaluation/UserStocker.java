package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Context;

public class UserStocker {
    private Context context;
    private static UserStocker userStocker;
    private int userId;
    private String userName;

    public UserStocker(Context context){
        this.context = context.getApplicationContext();
    }

    public static synchronized UserStocker getInstance(Context context){
        if(userStocker == null){
            userStocker = new UserStocker(context);
        }
        return userStocker;
    }

    public void setUserInfo(int userId, String userName){
        this.userId = userId;
        this.userName = userName;
    }

    public int getUserId(){
        return this.userId;
    }

    public String getUserName(){
        return this.userName;
    }
}

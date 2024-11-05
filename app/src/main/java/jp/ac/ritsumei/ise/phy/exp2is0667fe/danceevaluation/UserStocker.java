package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.content.Context;

public class UserStocker {
    private Context context;
    private static UserStocker userStocker;
    private int userId;
    private String emailAddress;

    public UserStocker(Context context){
        this.context = context.getApplicationContext();
    }

    public static synchronized UserStocker getInstance(Context context){
        if(userStocker == null){
            userStocker = new UserStocker(context);
        }
        return userStocker;
    }

    public void setUserInfo(int userId,String emailAddress){
        this.userId = userId;
        this.emailAddress = emailAddress;
    }

    public int getUserId(){
        return this.userId;
    }
    public String getEmailAddress() {return this.emailAddress;}
    public String getUserInfo() {return "User ID: " + userId + ", Email Address: " + emailAddress;}
}

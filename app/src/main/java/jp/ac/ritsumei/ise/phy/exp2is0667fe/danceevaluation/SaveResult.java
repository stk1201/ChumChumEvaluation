package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;

import android.app.Activity;

import java.io.File;

import okhttp3.OkHttpClient;

public class SaveResult {
    private final Activity activity;

    private UserStocker userStocker;
    private ResultStocker resultStocker;
    private File[] filePaths = new File[5];
    private OkHttpClient client = new OkHttpClient();

    public SaveResult(Activity activity) {
        this.activity = activity;
    }

    public void saving(){
        userStocker = userStocker.getInstance(activity);
        resultStocker = resultStocker.getInstance(activity);
    }
}

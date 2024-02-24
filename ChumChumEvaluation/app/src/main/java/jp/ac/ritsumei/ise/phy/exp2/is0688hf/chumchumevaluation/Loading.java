package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.widget.ImageView;

import jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation.ml.AutoModel4;

public class Loading extends AppCompatActivity {
    //アップロード画面で動画をアップロードしてスタートボタンを押したときにこの画面に遷移する。
    //ここではユーザからはロード画面が見えてるが裏ではTensorFlowを導入する。
    private ImageView imageView;
    private int[] imageResources = {R.drawable.loading1, R.drawable.loading2, R.drawable.loading3, R.drawable.loading4}; // 画像のリソースID配列
    private int currentIndex = 0; // 現在の画像のインデックス
    private Handler handler;
    private Runnable runnable;
    private static final long INTERVAL = 1000; // 1秒ごとに画像を変更する
    private static AutoModel4 model;
    videoStorage storage;
    Coodinate coordinate;
    private Uri userVideo; // フィールドとして宣言
    private Uri originalVideo; // フィールドとして宣言
    private final int numThreads = 1; // 使用するスレッドの数。これにより並行処理が可能になる。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_loading);

        storage = storage.getInstance(this);//videoStorageクラスのインスタンスの取得
        coordinate = coordinate.getInstance(this);//Coodinateクラスの作成

        // storageの初期化後にuserVideoとoriginalVideoを初期化する
        userVideo = storage.getVideo(0);// userVideoの初期化
        originalVideo = storage.getVideo(1);// originalVideoの初期化

        if(userVideo != null && originalVideo != null){
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            try {
                executorService.execute(() -> {
                    try {
                        analysis(userVideo, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                executorService.execute(() -> {
                    try {
                        analysis(originalVideo, 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } finally {
                executorService.shutdown();
            }
        }


        imageView = findViewById(R.id.imageView); // ImageViewの取得

        handler = new Handler();

        // 1秒ごとに画像を更新するRunnableを作成
        runnable = new Runnable() {
            @Override
            public void run() {
                updateImage(); // 画像を更新
                handler.postDelayed(this, INTERVAL); // 1秒後に再び実行
            }
        };

        // 最初の画像を表示
        updateImage();

    }

    double frameRate = 10;//1秒間に何フレームか
    ImageProcessor imageProcessor = new ImageProcessor.Builder()
            .add(new ResizeOp(192, 192, ResizeOp.ResizeMethod.BILINEAR))
            .build();

    private int analysisCount = 0; // analysisメソッドの呼び出し回数をカウントするための変数

    private void analysis(Uri video, int flag) throws IOException {//flagでユーザ動画か本家動画か区別する。0がユーザで1が本家になる。
        //フレーム処理と取得
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(this, video);

        String durationString = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int duration = Integer.parseInt(durationString); // 動画の長さ。単位はミリ秒
        int totalFrame = (int) (duration * frameRate / 1000); // 総フレーム数

        try{
            model = AutoModel4.newInstance(this);//モデルのインスタンス作成
        }catch (IOException e) {
            // TODO Handle the exception
            throw new RuntimeException(e);
        }

        //座標用の配列を用意する。
        //総フレーム数を計算して配列を用意する。listにして後で配列に変換するという方法もある。
        //51の内訳は17(各パーツのx座標)+17(各パーツのy座標)+17(各パーツのスコア)
        //配列には各フレームにパーツごとにx座標、y座標、スコアで並ぶ。
        float data[][] = new float[totalFrame][51];

        //フレームごとに推定を行う。
        for (int i = 0; i < totalFrame; i+= 1000/frameRate) {
            Bitmap frameBitmap = mediaMetadataRetriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);//1フレームのBitmap
            Bitmap argbBitmap = frameBitmap.copy(Bitmap.Config.ARGB_8888, true);

            TensorImage tensorImage = new TensorImage(DataType.UINT8);
            tensorImage.load(argbBitmap);
            tensorImage = imageProcessor.process(tensorImage);//リサイズ
            ByteBuffer buffer = tensorImage.getBuffer();// リサイズ後のTensorImageオブジェクトからバッファを取得する

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 192, 192, 3}, DataType.UINT8);//TensorBufferの作成
            inputFeature0.loadBuffer(buffer);

            AutoModel4.Outputs outputs = model.process(inputFeature0);//モデルの実行し、出力する。
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();//出力結果

            data[i] = outputFeature0.getFloatArray();
        }
        coordinate.addCoordinate(flag, data);
        System.out.println("finish");
        model.close();//MoveNetの終了
        mediaMetadataRetriever.release();

        analysisCount++;// analysisCountをインクリメント
        // analysisメソッドが2回呼び出されたら遷移
        if (analysisCount == 2) {
            Intent intent = new Intent(this, Score1.class);
            startActivity(intent);
        }
    }    @Override
    protected void onResume() {
        super.onResume();
        // Activityがフォアグラウンドになったら、1秒ごとに画像を変更するRunnableを開始
        handler.postDelayed(runnable, INTERVAL);
    }    @Override
    protected void onPause() {
        super.onPause();
        // Activityがバックグラウンドに移ったら、Runnableを停止
        handler.removeCallbacks(runnable);
    }

    private void updateImage() {
        // 現在のインデックスに基づいて画像を設定し、次の画像のインデックスに進める
        imageView.setImageResource(imageResources[currentIndex]);
        currentIndex = (currentIndex + 1) % imageResources.length; // 次のインデックス（ループする）
    }

}
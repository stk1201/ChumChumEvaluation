package jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

import jp.ac.ritsumei.ise.phy.exp2.is0688hf.chumchumevaluation.ml.AutoModel4;

public class Loading extends AppCompatActivity {
    //アップロード画面で動画をアップロードしてスタートボタンを押したときにこの画面に遷移する。
    //ここではユーザからはロード画面が見えてるが裏ではTensorFlowを導入する。

    private static AutoModel4 model;
    private videoStorage storage;
    private Coodinate coordinate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_loading);

        storage = new videoStorage();//videoStorageクラスの作成
        coordinate = new Coodinate();//Coodinateクラスの作成

        try {
            analysis(userVideo, 0);
            analysis(originalVideo, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Uri userVideo = storage.getVideo(0);
    private Uri originalVideo = storage.getVideo(1);
    double frameRate = 30;//1秒間に何フレームか


    private void analysis(Uri video, int flag) throws IOException {
        //フレーム処理と取得
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(this, video);

        String durationString = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long duration = Long.parseLong(durationString) * 1000; // 動画の長さ。単位はマイクロ秒

        //フレームごとに推定を行う。
        for (long i = 0; i < duration; i += 1000000 / frameRate) {
            Bitmap frameBitmap = mediaMetadataRetriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);//1フレームのBitmap

            try {
                model = AutoModel4.newInstance(this);//モデルのインスタンス作成

                // ビットマップのピクセルデータを取得する
                int bytes = frameBitmap.getByteCount();
                ByteBuffer buffer = ByteBuffer.allocate(bytes);
                frameBitmap.copyPixelsToBuffer(buffer);

                buffer.rewind();// バッファのポジションを最初に戻す

                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 192, 192, 3}, DataType.UINT8);//TensorBufferの作成
                inputFeature0.loadBuffer(buffer);

                AutoModel4.Outputs outputs = model.process(inputFeature0);//モデルの実行し、出力する。
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();//出力結果

                System.out.println(outputFeature0);


            } catch (IOException e) {
                // TODO Handle the exception
            }

        }

        mediaMetadataRetriever.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.close();//MoveNetの終了
    }
}
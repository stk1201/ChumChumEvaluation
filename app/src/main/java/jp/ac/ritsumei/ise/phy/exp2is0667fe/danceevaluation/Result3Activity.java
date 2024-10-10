package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;


import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

public class Result3Activity extends AppCompatActivity {

    private ResultStocker resultStocker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result3);

        resultStocker = resultStocker.getInstance(this);

        if(resultStocker != null){
            showingGraph();
        }
    }

    private void showingGraph(){
        LineData lineData = resultStocker.showGraph();

        LineChart lineChart = findViewById(R.id.lineChartExample);
        lineChart.setData(lineData);

        // X軸の設定
        lineChart.getXAxis().setEnabled(true);
        lineChart.getXAxis().setTextColor(Color.BLACK);

        lineChart.invalidate();
    }
}
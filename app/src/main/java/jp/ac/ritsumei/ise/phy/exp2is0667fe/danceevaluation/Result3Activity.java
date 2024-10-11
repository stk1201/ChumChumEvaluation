package jp.ac.ritsumei.ise.phy.exp2is0667fe.danceevaluation;


import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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

        //X軸の設定
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //Y軸の設定
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(100f);

        //凡例の設定
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        //説明ラベルの設定
        lineChart.getDescription().setEnabled(false);

        lineChart.invalidate();
    }
}
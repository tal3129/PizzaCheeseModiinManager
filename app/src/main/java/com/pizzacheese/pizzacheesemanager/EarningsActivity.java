package com.pizzacheese.pizzacheesemanager;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.pizzacheese.pizzacheesemanager.Types.DailyEarning;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EarningsActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnPrev, btnNxt, btnMode;
    TextView tvTotal, tvCash, tvCC;
    GraphView graph;
    private DecimalFormat df;
    int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        df = new DecimalFormat("#.0");
        setContentView(R.layout.activity_earnings);

        btnPrev = findViewById(R.id.btnPrev);
        btnNxt = findViewById(R.id.btnNext);
        btnMode = findViewById(R.id.btnMode);
        graph = findViewById(R.id.graph);
        tvTotal = findViewById(R.id.tvTotal);
        tvCash = findViewById(R.id.tvCash);
        tvCC = findViewById(R.id.tvCC);

        btnMode.setOnClickListener(this);
        btnNxt.setOnClickListener(this);
        btnPrev.setOnClickListener(this);

        currentIndex = MainActivity.earningsList.size() - 1;
        createApp(MainActivity.earningsList.get(currentIndex));
        updateButtons();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                if (currentIndex != MainActivity.earningsList.size() - 1) {
                    currentIndex += 1;
                    createApp(MainActivity.earningsList.get(currentIndex));
                    updateButtons();
                }
                break;
            case R.id.btnPrev:
                if (currentIndex != 0) {
                    currentIndex -= 1;
                    createApp(MainActivity.earningsList.get(currentIndex));
                    updateButtons();
                }
                break;
            case R.id.btnMode:
                break;
        }
    }

    public void updateButtons() {
        if (currentIndex == 0)
            btnPrev.setEnabled(false);
        else
            btnPrev.setEnabled(true);

        if (currentIndex == MainActivity.earningsList.size() - 1)
            btnNxt.setEnabled(false);
        else
            btnNxt.setEnabled(true);
    }


    public void createApp(DailyEarning tde) {

        if (tde == null)
            tde = new DailyEarning(Calendar.getInstance().getTime(), 0, 0);


        tvCC.setText(Html.fromHtml("סך הכנסות אשראי: " + "<b>" + df.format(tde.getCcSum()) + "₪" + "</b>"));
        tvCash.setText(Html.fromHtml("סך הכנסות מזומן: " + "<b>" + df.format(tde.getCashSum()) + "₪" + "</b>"));
        tvTotal.setText(Html.fromHtml("סך כל ההכנסות היומיות: " + "<b>" + df.format(tde.getCcSum() + tde.getCashSum()) + "₪" + "</b>"));


        //crating the bar chart
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(1, tde.getCashSum()),
                new DataPoint(2, tde.getCcSum()),
                new DataPoint(3, tde.getCashSum() + tde.getCcSum())
        });

        series.setSpacing(50);
        DateFormat datef = new SimpleDateFormat("dd/MM/yy");
        graph.setTitle("הכנסות יומיות " + datef.format(tde.getDate()));
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(4);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY((tde.getCashSum() + tde.getCcSum()) * 1.25);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.removeAllSeries();
        graph.addSeries(series);

// styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return data.getX() == 1 ? Color.parseColor("#f9a825") : data.getX() == 2 ? Color.parseColor("#FFAA00FF") : Color.parseColor("#FF64DD17");
            }
        });

        LabelFormatter labelFormatter = new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX && value == 1)
                    return "מזומן";
                else if (isValueX && value == 2)
                    return "אשראי";
                else if (isValueX && value == 3)
                    return "סה\"כ";
                else if (isValueX)
                    return "";
                return (df.format(value));
            }

            @Override
            public void setViewport(Viewport viewport) {
            }
        };

        graph.getGridLabelRenderer().setLabelFormatter(labelFormatter);

        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
    }

}

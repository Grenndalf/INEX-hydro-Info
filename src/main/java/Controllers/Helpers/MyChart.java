package Controllers.Helpers;

import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

public class MyChart extends LineChart<Number,Number> {
    public NumberAxis xAxis;

    public MyChart(Axis axis, Axis axis2) {
        super(axis, axis2);
    }
    public class MyXaxis{
        public NumberAxis xAxis;
    }
}

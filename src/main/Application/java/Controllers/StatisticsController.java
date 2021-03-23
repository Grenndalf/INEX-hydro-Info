package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import DButils.Tables.GaugeMeasurement;
import Others.Utils.ListsUtils;
import Others.Utils.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;

import java.util.*;
import java.util.stream.Collectors;

import static Others.Mediator.ControllerHolder.getInstance;
import static Others.Utils.Utils.divideIntegers;


public class StatisticsController {
    private static final String YAXIS_TEXT = "statistics.chart.yaxis";
    private static final String SSQ = "SSQ";
    private static final String SYSTEM = "System";
    private static final String M_3_S = " [m3]/s";
    private final GaugeDBActions gaugeDBActions = new GaugeDBActions ();
    List<GaugeMeasurement> selectedTownData =
            gaugeDBActions.queryForDataOfSelectedTownAndRiver (getInstance ().getRiverName (),
                                                               getInstance ().getTownName ());
    List<GaugeMeasurement> correctedDataList = correctData (selectedTownData);
    ListsUtils ls = new ListsUtils (gaugeDBActions.getCorrectedDoubleMeasurementsList (getInstance ().getRiverName ()
            , getInstance ().getTownName ()));

    @FXML
    private Label q10;
    @FXML
    private Label q50;
    @FXML
    private Label q90;
    @FXML
    private Label q100;
    @FXML
    private Label factor;
    @FXML
    private Label helperValue;
    @FXML
    private Arc dataArc;
    @FXML
    private Label correctDataInfo;
    @FXML
    private Pane chartContainer;
    @FXML
    private Label totalDataQuantity;
    @FXML
    private Label correctDataQuantity;
    @FXML
    private Label IncorrectDataQuantity;
    @FXML
    private Label averageFlowValue;
    @FXML
    private Pane secLineChartContainer;

    @FXML
    void initialize () {
        initDataArcValues ();
        initCorrectDataInfo ();
        chartContainer.getChildren ().add (getLineChart ());
        setQuantityInfo ();
        getAverageFlowValue ();
        setDeciles ();
        addSecondChart ();
    }

    private void setDeciles () {
        Platform.runLater (() -> {
            q10.setText (ls.getQ10 ().toEngineeringString ());
            q50.setText (ls.getQ50 ().toEngineeringString ());
            q90.setText (ls.getQ90 ().toEngineeringString ());
            q100.setText (ls.getQ100 ().toEngineeringString ());
            factor.setText (ls.getFactor ().toEngineeringString ());
            helperValue.setText (ls.getHelperValue ().toEngineeringString ());
        });
    }

    private void getAverageFlowValue () {
        averageFlowValue.setText (String.format ("%.2f",
                                                 correctedDataList
                                                         .stream ()
                                                         .collect (Collectors.averagingDouble (GaugeMeasurement::getData2))) + M_3_S);
    }

    private void setQuantityInfo () {
        totalDataQuantity.setText (String.valueOf (selectedTownData.size ()));
        correctDataQuantity.setText (String.valueOf (correctedDataList.size ()));
        IncorrectDataQuantity.setText (String.valueOf (selectedTownData.size () - correctedDataList.size ()));
    }

    private LineChart<Number, Number> getLineChart () {
        NumberAxis xAxis = new NumberAxis ();
        final HashMap<Short, Double> maxValuesPerYear =
                gaugeDBActions.getMaxValuesPerYear (getInstance ().getTownName (),
                                                   getInstance ().getRiverName ());
        short maxXAXis = maxValuesPerYear.keySet ().stream ().max (Short::compareTo).orElse ((short) 9);
        short minXAXis = maxValuesPerYear.keySet ().stream ().min (Short::compareTo).orElse (((short) 1));
        XYChart.Series<Number, Number> series = new XYChart.Series<> ();
        setXaxisParameters (xAxis);
        xAxis.setTickLength (20);
        LineChart<Number, Number> lineChart = new LineChart<> (xAxis, new NumberAxis ());
        lineChart.getData ().add (series);
        setSeriesFirstChart (series, maxValuesPerYear);
        lineChart.setPrefWidth (1100);
        lineChart.setPrefHeight (400);
        xAxis.setLowerBound (minXAXis - 1);
        xAxis.setUpperBound (maxXAXis + 1);
        lineChart.setLegendVisible (false);

        lineChart.getYAxis ().setLabel (Utils.getResourceBundle ().getString (YAXIS_TEXT));
        lineChart.setTitle (SSQ);
        return lineChart;
    }

    private void setSeriesSecondChart (Map<Integer, Double> dataMap, XYChart.Series<Number, Number> series) {
        dataMap.forEach ((integer, aDouble) -> series.getData ().add (new XYChart.Data<> (integer, aDouble)));
        series.getData ().forEach (this::addTooltipToChart);
    }

    private void setSeriesFirstChart (XYChart.Series<Number, Number> series, HashMap<Short, Double> maxValuesPerYear) {
        maxValuesPerYear.forEach ((year, data) -> series.getData ().add (new XYChart.Data<> (year, data)));
        series.getData ().forEach (this::addTooltipToChart);
    }

    private void setXaxisParameters (NumberAxis xAxis) {
        xAxis.setTickLabelRotation (120);
        xAxis.setTickLabelFont (Font.font (SYSTEM, 10));
        xAxis.setAutoRanging (false);
        xAxis.setTickUnit (1);
        if (getYearRange ().size () > 40) {
            xAxis.setTickUnit (5);
        }
        xAxis.setMinorTickVisible (false);
        xAxis.setLabel ("Rok");
    }

    private void addTooltipToChart (XYChart.Data<Number, Number> entry) {
        Tooltip t = new Tooltip (String.format ("%.2f", entry.getYValue ().doubleValue ()) + "\n" + entry.getXValue ());
        Tooltip.install (entry.getNode (), t);
    }

    private SortedSet<Short> getYearRange () {
        return correctedDataList.stream ().map (GaugeMeasurement::getMeasurementYear)
                .collect (Collectors.toCollection (TreeSet::new));
    }

    private void initCorrectDataInfo () {
        correctDataInfo.setText (String.format ("%.2f", (divideIntegers (correctedDataList.size (),
                                                                         selectedTownData.size ()) * 100)) + "%");
    }

    private void initDataArcValues () {
        dataArc.setLength (divideIntegers (correctedDataList.size (), selectedTownData.size ()) * 360);
        dataArc.setStartAngle (450 - divideIntegers (correctedDataList.size (), selectedTownData.size ()) * 360);
    }


    private List<GaugeMeasurement> correctData (List<GaugeMeasurement> dataToCorrect) {
        return dataToCorrect
                .stream ()
                .filter (data -> data.getData2 () < 9999.0)
                .collect (Collectors.toList ());
    }


    private Map<Integer, Double> averageValuesFromSortedDataPerDay () {
        Map<Integer, Double> result = new HashMap<> ();
        Map<Short, List<Double>> dataMap = new HashMap<> ();
        List<Object[]> list =
                gaugeDBActions.getSortedValuesPerYearAndPerDay (getInstance ().getRiverName (),
                                                                getInstance ().getTownName ());
        list.forEach (objects -> {
            if (!dataMap.containsKey (objects[0])) {
                List<Double> data = new ArrayList<> ();
                data.add ((double) objects[1]);
                dataMap.put ((short) objects[0], data);
            } else {
                dataMap.get (objects[0]).add ((double) objects[1]);
            }
        });

        for (int i = 0; i < 365; i++) {
            double sumOfMeasurements = 0;
            List<Short> yearList = new ArrayList<> (dataMap.keySet ());
            for (Short aShort : yearList)
                if (i < dataMap.get (aShort).size ())
                    sumOfMeasurements = sumOfMeasurements + dataMap.get (aShort).get (i);
            if (sumOfMeasurements == 0) continue;
            sumOfMeasurements /= yearList.size ();
            int day = i + 1;
            result.put (day, sumOfMeasurements);
        }
        return result;
    }

    private LineChart<Number, Number> createSecondChart (Map<Integer, Double> dataset) {
        NumberAxis xAxis = new NumberAxis ();
        xAxis.setTickLabelRotation (90);
        xAxis.setTickLabelFont (Font.font (SYSTEM, 10));
        xAxis.setAutoRanging (true);
        XYChart.Series<Number, Number> series = new XYChart.Series<> ();
        LineChart<Number, Number> lineChart = new LineChart<> (xAxis, new NumberAxis ());
        lineChart.getStyleClass ().add ("sec-line-chart");
        lineChart.setPrefWidth (1100);
        lineChart.setPrefHeight (400);
        lineChart.getData ().add (series);
        lineChart.setLegendVisible (false);
        setSeriesSecondChart (dataset, series);

        lineChart.getYAxis ().setLabel (Utils.getResourceBundle ().getString (YAXIS_TEXT));
        lineChart.setTitle (SSQ);
        return lineChart;
    }


    public void addSecondChart () {
        Platform.runLater (() -> {
            secLineChartContainer.getChildren ().clear ();
            secLineChartContainer.getChildren ().add (createSecondChart (averageValuesFromSortedDataPerDay ()));
        });
    }


}

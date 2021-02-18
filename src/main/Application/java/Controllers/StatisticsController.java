package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import DButils.Tables.GaugeMeasurement;
import Others.Mediator.ControllerHolder;
import Others.Utils.TableViewData;
import Others.Utils.Utils;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static Others.Utils.Utils.divideIntegers;
import static java.util.Collections.reverse;


public class StatisticsController {
    private static final String YAXIS_TEXT = "statistics.chart.yaxis";
    private static final String SSQ = "SSQ";
    private static final String SYSTEM = "System";
    private static final String M_3_S = " [m3]/s";
    @FXML
    public Arc dataArc;
    @FXML
    public Label correctDataInfo;
    @FXML
    public AnchorPane statisticsContainer;
    @FXML
    public Tab tabGeneralInfo;
    @FXML
    public Pane chartContainer;
    @FXML
    public Label totalDataQuantity;
    @FXML
    public Label correctDataQuantity;
    @FXML
    public Label IncorrectDataQuantity;
    @FXML
    public Label averageFlowValue;
    @FXML
    public Pane secLineChartContainer;
    public TableView<TableViewData> table;
    public TableColumn probability;
    public TableColumn maxFlow;
    public TableColumn num;
    public TableColumn logNumValue;
    private GaugeDBActions gaugeDBActions = new GaugeDBActions ();
    List<GaugeMeasurement> selectedTownData =
            gaugeDBActions.queryForDataOfSelectedTown (ControllerHolder.getInstance ().getTownName ());
    List<GaugeMeasurement> correctedDataList = correctData (selectedTownData);

    @FXML
    void initialize () {
        populateTableView ();
        initDataArcValues ();
        initCorrectDataInfo ();
        chartContainer.getChildren ().add (getLineChart ());
        setQuantityInfo ();
        getAverageFlowValue ();
        secLineChartContainer.getChildren ().add (createSecondChart (averageValuesFromSortedData ()));
        System.out.println (calculateB1(getMaxValuePerYear ()));

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
        setXaxisParameters (xAxis);
        xAxis.setTickLength (20);
        Map<Short, Double> dataMap = new HashMap<> ();
        XYChart.Series<Number, Number> series = new XYChart.Series<> ();
        fillDataMap (dataMap);
        LineChart<Number, Number> lineChart = new LineChart<> (xAxis, new NumberAxis ());
        lineChart.setPrefWidth (1100);
        lineChart.setPrefHeight (400);
        lineChart.getData ().add (series);
        xAxis.setUpperBound (dataMap.keySet ().stream ().max (Short::compareTo).orElse ((short) 10) + 1);
        xAxis.setLowerBound (dataMap.keySet ().stream ().min (Short::compareTo).orElse ((short) 0) - 1);
        lineChart.setLegendVisible (false);
        this.setSeriesFirstChart (dataMap, series);
        lineChart.getYAxis ().setLabel (Utils.getResourceBundle ().getString (YAXIS_TEXT));
        lineChart.setTitle (SSQ);
        return lineChart;
    }

    private void setSeriesFirstChart (Map<Short, Double> dataMap, XYChart.Series<Number, Number> series) {
        dataMap.forEach ((aShort, aDouble) -> series.getData ().add (new XYChart.Data<> (aShort, aDouble)));
        series.getData ().forEach (this::addTooltipToChart);
    }

    private void setSeriesSecondChart (Map<Integer, Double> dataMap, XYChart.Series<Number, Number> series) {
        dataMap.forEach ((integer, aDouble) -> series.getData ().add (new XYChart.Data<> (integer, aDouble)));
        series.getData ().forEach (this::addTooltipToChart);
    }

    private void fillDataMap (Map<Short, Double> dataMap) {
        getYearRange ().forEach (aShort -> {
            double average = correctedDataList.stream ()
                    .filter (gaugeMeasurement -> gaugeMeasurement.getMeasurementYear () == aShort)
                    .collect (Collectors.averagingDouble (GaugeMeasurement::getData2));
            dataMap.put (aShort, average);
        });
    }

    private void setXaxisParameters (NumberAxis xAxis) {
        xAxis.setTickLabelRotation (120);
        xAxis.setTickLabelFont (Font.font (SYSTEM, 15));
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
                .sorted (Comparator.naturalOrder ())
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
                .filter (data -> data.getData2 () < 99999.0)
                .collect (Collectors.toList ());
    }

    private Map<Integer, Double> averageValuesFromSortedData () {
        Map<Integer, Double> result = new HashMap<> ();
        Map<Short, List<Double>> dataMap = new HashMap<> ();

        getYearRange ().forEach (aShort -> {
            List<Double> measurementsFromOneYear = correctedDataList
                    .stream ()
                    .filter (gaugeMeasurement -> gaugeMeasurement.getMeasurementYear () == aShort)
                    .map (GaugeMeasurement::getData2)
                    .sorted (Double::compareTo)
                    .collect (Collectors.toList ());
            reverse (measurementsFromOneYear);
            dataMap.put (aShort, measurementsFromOneYear);
        });

        for (int i = 0; i < 365; i++) {
            double sumOfMeasurements = 0;
            List<Short> yearList = new ArrayList<> (getYearRange ());
            for (Short aShort : yearList)
                if (i < dataMap.get (aShort).size ())
                    sumOfMeasurements = sumOfMeasurements + dataMap.get (aShort).get (i);
            sumOfMeasurements /= yearList.size ();
            if (sumOfMeasurements == 0) continue;
            int day = i + 1;
            result.put (day, sumOfMeasurements);
        }
        return result;
    }

    private List<Double> getMaxValuePerYear () {
        List<Double> maxValuesPerYear = new ArrayList<> ();
        getYearRange ().forEach (aShort -> {
            double measurementsFromOneYear = getMeasurementsFromOneYear (aShort);
            maxValuesPerYear.add (measurementsFromOneYear);
        });
        maxValuesPerYear.sort (Comparator.reverseOrder ());
        return maxValuesPerYear;
    }

    private Double getMeasurementsFromOneYear (Short aShort) {
        double maxValue = 0.0;
        if (correctedDataList
                .stream ()
                .filter (gaugeMeasurement -> gaugeMeasurement.getMeasurementYear () == aShort)
                .map (GaugeMeasurement::getData2).max (Double::compareTo).isPresent ()) {
            maxValue = correctedDataList
                    .stream ()
                    .filter (gaugeMeasurement -> gaugeMeasurement.getMeasurementYear () == aShort)
                    .map (GaugeMeasurement::getData2)
                    .max (Double::compareTo).get ();
        }
        return maxValue;
    }

    private LineChart<Number, Number> createSecondChart (Map<Integer, Double> dataset) {
        NumberAxis xAxis = new NumberAxis ();
        xAxis.setTickLabelRotation (90);
        xAxis.setTickLabelFont (Font.font (SYSTEM, 15));
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


    private void populateTableView () {
        final ObservableList<TableViewData> tableViewDataList = FXCollections.observableArrayList ();
        List<Short> convertedYearRangeList = new ArrayList<> (getYearRange ());
        for (int i = 1; i < convertedYearRangeList.size (); i++) {
            tableViewDataList.add (new TableViewData (new SimpleIntegerProperty (i),
                                                      new SimpleDoubleProperty (getMaxValuePerYear ().get (i)),
                                                      new SimpleDoubleProperty (100 * (divideIntegers (i,
                                                                                                       (getYearRange ().size () + 1)))),
                                                      new SimpleDoubleProperty (Math.log (i))));
        }
        num.setCellValueFactory (new PropertyValueFactory<TableViewData, Integer> ("numValue"));
        maxFlow.setCellValueFactory (new PropertyValueFactory<TableViewData, Double> ("maxFlowValue"));
        probability.setCellValueFactory (new PropertyValueFactory<TableViewData, Integer> ("probability"));
        logNumValue.setCellValueFactory (new PropertyValueFactory<TableViewData, Double> ("logValueOfNumValue"));
        table.setItems (tableViewDataList);
    }

    private void calculateFirstDecile (double b0, double b1, int x) {
        double firstDecile = Math.log (0.1 * x);

    }

    private List<Integer> numList (int maxValueListSize) {
        List<Integer> result = new ArrayList<> ();
        if (maxValueListSize > 0) {
            for (int i = 1; i <= maxValueListSize; i++) {
                result.add (i);
            }
            return result;
        }
        return result;
    }

    private double calculateB0 (List<Double> maxValuesList) {

        if (maxValuesList.size () > 0) {
            return maxValuesList.stream ().collect (Collectors.averagingDouble (value -> value));
        } else {
            return 0;
        }
    }

    private double calculateB1 (List<Double> maxValuesList) {
        if (maxValuesList.size () > 0) {
            double sumOfMultiplyAverageValues = 0;
            double sumOfPowLogValues = 0;
            for (int i = 0; i < maxValuesList.size (); i++) {
                sumOfPowLogValues +=
                        Math.pow ((getLogModifiedMaxValueList (maxValuesList).get (i)),2);
                System.out.println (getLogModifiedMaxValueList (maxValuesList).get (i));
                System.out.println (sumOfPowLogValues);
                sumOfMultiplyAverageValues +=
                        getLogModifiedMaxValueList (maxValuesList).get (i) * getModifiedMaxValueList (maxValuesList).get (i);
            }
            double result = sumOfMultiplyAverageValues/sumOfPowLogValues;
            return result;
        } else {
            return 0;
        }
    }
//ok
    private List<Double> getModifiedMaxValueList (List<Double> maxValuesList) {
        List<Double> maxValuesModifiedList =
                maxValuesList.stream ()
                        .map (aDouble -> aDouble - averageMaxValue (maxValuesList))
                        .collect (Collectors.toList ());
        return maxValuesModifiedList;
    }
//ok
    private List<Double> getLogModifiedMaxValueList (List<Double> maxValuesList) {
        List<Double> logModifiedMaxValuesList =
                IntStream.range (1, (maxValuesList.size () + 1))
                        .mapToObj (i -> Math.log (i) - averageNatLog (maxValuesList.size ()))
                        .collect (Collectors.toList ());
        return logModifiedMaxValuesList;
    }
//ok
    private double averageNatLog (int listSize) {
        double result = 0;
        if (listSize > 0) {
            for (int i = 1; i <= listSize; i++) {
                result = result + Math.log (i);
            }
            return result / listSize;
        } else {
            return result;
        }
    }
//ok
    private double averageMaxValue (List<Double> maxValuesList) {
        if (maxValuesList.size () > 0) {
            return maxValuesList.stream ().collect (Collectors.averagingDouble (value -> value));
        } else {
            return 0;
        }
    }

}

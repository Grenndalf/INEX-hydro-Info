package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import DButils.Tables.GaugeMeasurement;
import Others.Mediator.ControllerHolder;
import Others.Utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private final GaugeDBActions gaugeDBActions = new GaugeDBActions ();
    List<GaugeMeasurement> selectedTownData =
            gaugeDBActions.queryForDataOfSelectedTownAndRiver (ControllerHolder.getInstance ().getRiverName (),
                                                               ControllerHolder.getInstance ().getTownName ());
    List<GaugeMeasurement> correctedDataList = correctData (selectedTownData);
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
        q10.setText (getStringValueOfDecile (0.1));
        q50.setText (getStringValueOfDecile (0.5));
        q90.setText (getStringValueOfDecile (0.9));
        q100.setText (getStringValueOfDecile (1));
        setHelperValue ();
        // factor.setText (getQuantilePointer ().toEngineeringString ());
        initDataArcValues ();
        initCorrectDataInfo ();
        chartContainer.getChildren ().add (getLineChart ());
        setQuantityInfo ();
        getAverageFlowValue ();
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
        xAxis.setLowerBound (dataMap.keySet ().stream ().min (Short::compareTo).orElse ((short) 1) - 1);
        xAxis.setUpperBound (dataMap.keySet ().stream ().max (Short::compareTo).orElse ((short) 9) + 1);
        lineChart.setLegendVisible (false);
        setSeriesFirstChart (dataMap, series);
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
        final List<Object[]> maxValuesPerYear =
                gaugeDBActions.getMaxValuesPerYear (ControllerHolder.getInstance ().getTownName (),
                                                    ControllerHolder.getInstance ().getRiverName ());
        for (Object[] row : maxValuesPerYear) {
            dataMap.put ((short) row[0], (double) row[1]);
        }
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


    private Map<Integer, Double> averageValuesFromSortedDataPerDay () {

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

//        List<Object[]> list =
//                gaugeDBActions.getSortedValuesPerYearAndPerDay (ControllerHolder.getInstance ().getRiverName (),
//                                                                ControllerHolder.getInstance ().getTownName ());
//        list.forEach (objects -> {
//            if (!dataMap.containsKey (objects[0])) {
//                List<Double> data = new ArrayList<> ();
//                data.add ((double) objects[1]);
//                dataMap.put ((short) objects[0], data);
//            } else {
//                dataMap.get (objects[0]).add ((double) objects[1]);
//            }
//        });
//        System.out.println (dataMap);

        for (int i = 0; i < 366; i++) {
            double sumOfMeasurements = 0;
            List<Short> yearList = new ArrayList<> (getYearRange ());
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
        return correctedDataList
                .stream ()
                .filter (gaugeMeasurement -> gaugeMeasurement.getMeasurementYear () == aShort)
                .map (GaugeMeasurement::getData2)
                .max (Double::compareTo).orElse (0.0);
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


    //OK W KLASIE
    private BigDecimal getQuantilePointer () {
        return (getBigDecimalValueDecile (0.1).subtract (getBigDecimalValueDecile (0.9)))
                .divide (getBigDecimalValueDecile (0.5).multiply (BigDecimal.valueOf (2)),
                         RoundingMode.HALF_UP);
    }

    private void setHelperValue () {
        helperValue.setText (calculateHelperValue ());
    }

    //OK W KLASIE
    private String calculateHelperValue () {
        if ((getBigDecimalValueDecile (0.5).subtract (getBigDecimalValueDecile (1))).setScale (2,
                                                                                               RoundingMode.HALF_UP).signum () > 0) {
            return (getQuantilePointer ().multiply (getBigDecimalValueDecile (0.5)))
                    .divide (getBigDecimalValueDecile (0.5).subtract (getBigDecimalValueDecile (1)), 2,
                             RoundingMode.HALF_UP)
                    .toEngineeringString ();
        }
        return "0";
    }

    private String getStringValueOfDecile (double v) {
        return getBigDecimalValueDecile (v).toEngineeringString ();
    }

    //OK W KLASIE
    private BigDecimal getBigDecimalValueDecile (double v) {
        return calculateDeciles (calculateB0 (getMaxValuePerYear ()),
                                 calculateB1 (getMaxValuePerYear ()),
                                 BigDecimal.valueOf (v).multiply (BigDecimal.valueOf (getMaxValuePerYear ().size ())));
    }

    //OK W KLASIE
    private BigDecimal calculateDeciles (BigDecimal b0, BigDecimal b1, BigDecimal x) {
        if (x.compareTo (BigDecimal.valueOf (1)) > 0 && x.compareTo (BigDecimal.valueOf (100)) < 0) {
            return (b0.add ((b1).multiply (BigDecimal.valueOf (Math.log (x.doubleValue ()))))).setScale (2,
                                                                                                         RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    //ok w klasie
    private BigDecimal calculateB0 (List<Double> maxValuesList) {
        if (maxValuesList.size () > 0) {
            final BigDecimal averageLogNumValue = averageNatLog (maxValuesList.size ());
            final BigDecimal averageMaxFlowValue = averageMaxValue (maxValuesList);
            return averageMaxFlowValue.subtract (averageLogNumValue
                                                         .multiply (calculateB1 (maxValuesList)))
                    .setScale (4, RoundingMode.HALF_UP);

        } else {
            return BigDecimal.ZERO;
        }
    }

    //ok w klasie
    private BigDecimal calculateB1 (List<Double> maxValuesList) {
        if (maxValuesList.size () > 0) {
            BigDecimal sumOfMultiplyAverageValues = BigDecimal.ZERO;
            BigDecimal sumOfPowLogValues = BigDecimal.ZERO;
            int i = 0;
            while (i < maxValuesList.size ()) {
                sumOfPowLogValues = sumOfPowLogValues.add (BigDecimal.valueOf (
                        Math.pow ((getLogModifiedMaxValueList (maxValuesList.size ()).get (i)), 2))).setScale (8,
                                                                                                               RoundingMode.HALF_UP);

                sumOfMultiplyAverageValues = sumOfMultiplyAverageValues.add (BigDecimal.valueOf (
                        getLogModifiedMaxValueList (maxValuesList.size ()).get (i)).multiply (getModifiedMaxValueList (maxValuesList).get (i))).setScale (4, RoundingMode.HALF_UP);
                i++;
            }
            if (sumOfPowLogValues.signum () > 0) {
                return sumOfMultiplyAverageValues.divide (sumOfPowLogValues, 4, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    //ok w klasie
    private List<BigDecimal> getModifiedMaxValueList (List<Double> maxValuesList) {
        return maxValuesList.stream ()
                .map (aDouble -> BigDecimal.valueOf (aDouble).subtract (averageMaxValue (maxValuesList)))
                .collect (Collectors.toList ());
    }

    //ok w klasie
    private List<Double> getLogModifiedMaxValueList (int maxValuesList) {
        return IntStream.range (1, (maxValuesList + 1))
                .mapToObj (i -> Math.log (i) - averageNatLog (maxValuesList).doubleValue ())
                .collect (Collectors.toList ());
    }

    //ok w klasie
    private BigDecimal averageNatLog (int listSize) {
        BigDecimal result = BigDecimal.ZERO;
        if (listSize > 0) {
            for (int i = 1; i <= listSize; i++) {
                result = result.add (BigDecimal.valueOf (Math.log (i)));
            }
            return result.divide (BigDecimal.valueOf (listSize), 2, RoundingMode.HALF_UP);
        } else {
            return result;
        }
    }

    //ok w klasie
    private BigDecimal averageMaxValue (List<Double> maxValuesList) {
        if (maxValuesList.size () > 0) {
            BigDecimal result = BigDecimal.ZERO;
            for (Double value : maxValuesList
            ) {
                result = result.add (BigDecimal.valueOf (value));
            }
            return BigDecimal.valueOf (maxValuesList.stream ().collect (Collectors.averagingDouble (value -> value))).setScale (4, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public void addChart () {
        secLineChartContainer.getChildren ().clear ();
        secLineChartContainer.getChildren ().add (createSecondChart (averageValuesFromSortedDataPerDay ()));
    }


}

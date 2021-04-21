package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import DButils.Tables.GaugeMeasurement;
import Others.Utils.Calculations;
import Others.Utils.Utils;
import com.jfoenix.controls.JFXTabPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;

import java.util.*;
import java.util.stream.Collectors;

import static Others.Mediator.ControllerHolder.getInstance;
import static Others.Utils.Utils.divideIntegers;


public class StatisticsController {
    public static final String KRZYWA_SUM = "Krzywa sum czasów trwania wraz z wy¿szymi";
    private static final String YAXIS_TEXT = "statistics.chart.yaxis";
    private static final String SSQ = "SSQ";
    private static final String SYSTEM = "System";
    private static final String M_3_S = " [m3]/s";
    private final GaugeDBActions gaugeDBActions = new GaugeDBActions ();

    @FXML
    public JFXTabPane tabPane;

    List<GaugeMeasurement> selectedTownData =
            gaugeDBActions.queryForDataOfSelectedTownAndRiver (getInstance ().getRiverName (),
                                                               getInstance ().getTownName ());
    List<GaugeMeasurement> correctedDataList = correctData (selectedTownData);
    Calculations ls =
            new Calculations (gaugeDBActions.getCorrectedDoubleMeasurementsList (getInstance ().getRiverName ()
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
    private Label skewCoefficient;
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
    private Pane thirdChartContainer;
    @FXML
    private VBox qpPercentContainer;
    @FXML
    private VBox pPercentContainer;

    @FXML
    void initialize () {
        initDataArcValues ();
        initCorrectDataInfo ();
        setQuantityInfo ();
        getAverageFlowValue ();
        setDeciles ();
        addChart (chartContainer, getFirstLineChart (), tabPane);
        addChart (secLineChartContainer, getSecondLineChart (), tabPane);
        addChart (thirdChartContainer, getThirdLineChart (), tabPane);
        setPercentValues ();
    }

    private void setDeciles () {
        Platform.runLater (() -> {
            q10.setText (ls.getQ10 ().toEngineeringString ());
            q50.setText (ls.getQ50 ().toEngineeringString ());
            q90.setText (ls.getQ90 ().toEngineeringString ());
            q100.setText (ls.getQ100 ().toEngineeringString ());
            factor.setText (ls.getFactor ().toEngineeringString ());
            helperValue.setText (ls.getHelperValue ().toEngineeringString ());
            skewCoefficient.setText (ls.getSkewCoefficient ().toEngineeringString ());
        });
    }

    private void setPercentValues () {
        for (int i = 0; i < Utils.percentValueList ().size (); i++) {
            pPercentContainer.getChildren ().add (new Label (Utils.percentValueList ().get (i).toString ()));
            qpPercentContainer.getChildren ().add (new Label (ls.getFinalResults ().get (Utils.percentValueList ().size () - 1 - i).toString ()));
        }
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

    private LineChart<Number, Number> getFirstLineChart () {
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
        lineChart.prefWidthProperty ().bind (chartContainer.widthProperty ());
        lineChart.prefHeightProperty ().bind (chartContainer.heightProperty ());
        xAxis.setLowerBound (minXAXis - 1);
        xAxis.setUpperBound (maxXAXis + 1);
        lineChart.setLegendVisible (false);
        lineChart.getYAxis ().setLabel (Utils.getResourceBundle ().getString (YAXIS_TEXT));
        lineChart.setTitle (SSQ);
        return lineChart;
    }

    private LineChart<Number, Number> getSecondLineChart () {
        NumberAxis xAxis = new NumberAxis ();
        NumberAxis yAxis = new NumberAxis ();
        yAxis.setLabel ("Przep³yw Q [m3/s]");
        xAxis.setTickLabelRotation (90);
        xAxis.setTickLabelFont (Font.font (SYSTEM, 10));
        xAxis.setAutoRanging (true);
        xAxis.setLabel ("Czas (t) dni");
        XYChart.Series<Number, Number> series = new XYChart.Series<> ();
        LineChart<Number, Number> lineChart = new LineChart<> (xAxis, yAxis);
        lineChart.getStyleClass ().add ("sec-line-chart");
        lineChart.prefWidthProperty ().bind (secLineChartContainer.widthProperty ());
        lineChart.prefHeightProperty ().bind (secLineChartContainer.heightProperty ());
        lineChart.getData ().add (series);
        lineChart.setLegendVisible (false);
        setSeriesSecondChart (averageValuesFromSortedDataPerDay (), series);
        lineChart.getYAxis ().setLabel (Utils.getResourceBundle ().getString (YAXIS_TEXT));
        lineChart.setTitle (KRZYWA_SUM);
        return lineChart;
    }

    private LineChart<Number, Number> getThirdLineChart () {
        NumberAxis xAxis = new NumberAxis ();
        xAxis.setLowerBound (-5);
        xAxis.setUpperBound (105);
        xAxis.setTickUnit (10);
        xAxis.setAutoRanging (false);
        xAxis.setLabel ("prawdopodobieñstwo przewy¿szenia p [%]");
        xAxis.setMinorTickVisible (true);
        final HashMap<Double, Double> finalDataMap = new HashMap<> ();
        for (int i = 0; i < Utils.percentValueList ().size (); i++) {
            finalDataMap.put (Utils.percentValueList ().get (i),
                              ls.getFinalResults ().get (ls.getFinalResults ().size () - 1 - i));
        }
        XYChart.Series<Number, Number> series = new XYChart.Series<> ();
        LineChart<Number, Number> lineChart = new LineChart<> (xAxis, new NumberAxis ());
        lineChart.getData ().add (series);
        lineChart.getYAxis ().setLabel ("Przep³yw [m3/s]");
        setSeriesThirdChart (series, finalDataMap);
        lineChart.prefWidthProperty ().bind (thirdChartContainer.widthProperty ());
        lineChart.prefHeightProperty ().bind (thirdChartContainer.heightProperty ());
        lineChart.setLegendVisible (false);
        lineChart.getYAxis ().setLabel (Utils.getResourceBundle ().getString (YAXIS_TEXT));
        lineChart.setTitle ("Przep³ywy Prawdopodobne");
        return lineChart;
    }

    private void setSeriesSecondChart (HashMap<Integer, Double> dataMap, XYChart.Series<Number, Number> series) {
        dataMap.forEach ((integer, aDouble) -> series.getData ().add (new XYChart.Data<> (integer, aDouble)));
        series.getData ().forEach (this::addTooltipToChart);
    }

    private void setSeriesFirstChart (XYChart.Series<Number, Number> series, HashMap<Short, Double> maxValuesPerYear) {
        maxValuesPerYear.forEach ((year, data) -> series.getData ().add (new XYChart.Data<> (year, data)));
        series.getData ().forEach (this::addTooltipToChart);
    }

    private void setSeriesThirdChart (XYChart.Series<Number, Number> series, HashMap<Double, Double> finalDataMap) {
        finalDataMap.forEach ((year, data) -> series.getData ().add (new XYChart.Data<> (year, data)));
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
                .filter (data -> data.getData2 () < 9_999.0)
                .collect (Collectors.toList ());
    }

    private HashMap<Integer, Double> averageValuesFromSortedDataPerDay () {
        HashMap<Integer, Double> result = new HashMap<> ();
        HashMap<Short, List<Double>> dataMap = new HashMap<> ();
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

    public void addChart (Pane container, LineChart lineChart, Node windowOwner) {
        Platform.runLater (() -> {
            container.getChildren ().clear ();
            container.getChildren ().add (lineChart);
        });
        setContextMenuCopy (container, windowOwner);
    }

    private void setContextMenuCopy (Node imageArea, Node windowOwner) {
        ContextMenu contextMenu = new ContextMenu ();
        MenuItem menuItem1 = new MenuItem ("Kopiuj");
        contextMenu.getItems ().add (menuItem1);
        menuItem1.setOnAction (event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard ();
            ClipboardContent content = new ClipboardContent ();
            SnapshotParameters snapshotParams = new SnapshotParameters ();
            WritableImage image = imageArea.snapshot (snapshotParams, null);
            content.putImage (image);
            clipboard.setContent (content);
        });
        imageArea.setOnContextMenuRequested (event -> contextMenu.show (windowOwner.getScene ().getWindow (),
                                                                        event.getScreenX (),
                                                                        event.getScreenY ()));
    }
}

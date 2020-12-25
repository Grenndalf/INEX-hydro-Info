package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import DButils.Tables.GaugeMeasurement;
import RegularClasses.Mediator.ControllerHolder;
import RegularClasses.Utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;

import java.util.*;
import java.util.stream.Collectors;


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
    public Label averageFlowValue;

    GaugeDBActions gaugeDBActions = new GaugeDBActions();
    List<GaugeMeasurement> selectedTownData = gaugeDBActions.queryForDataOfSelectedTown(ControllerHolder.getInstance().getTownName());
    List<GaugeMeasurement> correctedDataList = correctData(selectedTownData);

    @FXML
    void initialize() {
        initDataArcValues();
        initCorrectDataInfo();
        chartContainer.getChildren().add(getLineChart());
        setQuantityInfo();
        getAverageFlowValue();
    }

    private void getAverageFlowValue() {
        averageFlowValue.setText(String.format("%.2f",
                correctedDataList
                        .stream()
                        .collect(Collectors.averagingDouble(GaugeMeasurement::getData2))) + M_3_S);
    }

    private void setQuantityInfo() {
        totalDataQuantity.setText(String.valueOf(selectedTownData.size()));
        correctDataQuantity.setText(String.valueOf(correctedDataList.size()));
        IncorrectDataQuantity.setText(String.valueOf(selectedTownData.size() - correctedDataList.size()));
    }

    private LineChart<Number, Number> getLineChart() {
        NumberAxis xAxis = new NumberAxis();
        setXaxisParameters(xAxis);
        Map<Short, Double> dataMap = new HashMap<>();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        fillDataMap(dataMap);
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, new NumberAxis());
        lineChart.setPrefWidth(1100);
        lineChart.setPrefHeight(400);
        lineChart.getData().add(series);
        xAxis.setUpperBound(dataMap.keySet().stream().max(Short::compareTo).orElse((short) 10) + 1);
        xAxis.setLowerBound(dataMap.keySet().stream().min(Short::compareTo).orElse((short) 0) - 1);
        lineChart.setLegendVisible(false);
        setSeries(dataMap, series);
        lineChart.getYAxis().setLabel(Utils.getResourceBundle().getString(YAXIS_TEXT));
        lineChart.setTitle(SSQ);
        return lineChart;
    }

    private void setSeries(Map<Short, Double> dataMap, XYChart.Series<Number, Number> series) {
        dataMap.forEach((aShort, aDouble) -> series.getData().add(new XYChart.Data<>(aShort, aDouble)));
        series.getData().forEach(this::addTooltipToChart);
    }

    private void fillDataMap(Map<Short, Double> dataMap) {
        getYearRange().forEach(aShort -> {
            double average = correctedDataList.stream()
                    .filter(gaugeMeasurement -> gaugeMeasurement.getMeasurementYear() == aShort)
                    .collect(Collectors.averagingDouble(GaugeMeasurement::getData2));
            dataMap.put(aShort, average);
        });
    }

    private void setXaxisParameters(NumberAxis xAxis) {
        xAxis.setTickLabelRotation(90);
        xAxis.setTickLabelFont(Font.font(SYSTEM, 15));
        xAxis.setAutoRanging(false);
        xAxis.setTickUnit(1);
        if (getYearRange().size() > 40) {
            xAxis.setTickUnit(5);
        }
        xAxis.setMinorTickVisible(false);
        xAxis.setLabel("Rok");
    }

    private void addTooltipToChart(XYChart.Data<Number, Number> entry) {
        Tooltip t = new Tooltip(String.format("%.2f", entry.getYValue().doubleValue()) + "\n" + entry.getXValue());
        Tooltip.install(entry.getNode(), t);
    }

    private SortedSet<Short> getYearRange() {
        return correctedDataList.stream().map(GaugeMeasurement::getMeasurementYear)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private void initCorrectDataInfo() {
        correctDataInfo.setText(String.format("%.2f", (Utils.divideIntegers(correctedDataList.size(), selectedTownData.size()) * 100)) + "%");
    }

    private void initDataArcValues() {
        dataArc.setLength(Utils.divideIntegers(correctedDataList.size(), selectedTownData.size()) * 360);
        dataArc.setStartAngle(450 - Utils.divideIntegers(correctedDataList.size(), selectedTownData.size()) * 360);
    }

    private List<GaugeMeasurement> correctData(List<GaugeMeasurement> dataToCorrect) {
        return dataToCorrect
                .stream()
                .filter(data -> data.getData2() < 99999.0)
                .collect(Collectors.toList());
    }

}

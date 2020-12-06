package Controllers;

import DButils.DbActions;
import RegularClasses.Mediator.ControllerHolder;
import RegularClasses.Tables.GaugeMeasurement;
import RegularClasses.Utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Arc;

import java.util.*;
import java.util.stream.Collectors;


public class StatisticsController {
    @FXML
    public Arc dataArc;
    @FXML
    public Label correctDataInfo;
    @FXML
    public AnchorPane statisticsContainer;


    DbActions dbActions = new DbActions();
    List<GaugeMeasurement> selectedTownData = dbActions.queryForDataOfSelectedTown(ControllerHolder.getInstance().getTownName());
    List<GaugeMeasurement> correctedDataList = correctData(selectedTownData);

    @FXML
    void initialize() {
        initDataArcValues();
        initCorrectDataInfo();
        statisticsContainer.getChildren().add(getLineChart());
    }

    private LineChart<Number, Number> getLineChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();


        xAxis.setAutoRanging(false);
        xAxis.setTickUnit(1);
        if (getYearRange().size() > 60) {
            System.out.println(getYearRange().size());
            xAxis.setTickUnit(5);
        }
        xAxis.setMinorTickVisible(false);
        xAxis.setLowerBound(returnMaxOrMinYear(false) - 1);
        xAxis.setUpperBound(returnMaxOrMinYear(true) + 2);

        Map<Short, Double> dataMap = new HashMap<>();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        getYearRange().forEach(aShort -> {
                    double average = correctedDataList.stream()
                            .filter(gaugeMeasurement -> gaugeMeasurement.getMeasurementYear() == aShort)
                            .collect(Collectors.averagingDouble(value -> value.getData2().doubleValue()));
                    if (average < 9999)
                        dataMap.put(aShort, average);
                });


        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setPrefWidth(1100);
        lineChart.setPrefHeight(400);
        lineChart.getData().add(series);
        lineChart.setLegendVisible(false);
        dataMap.forEach((aShort, aDouble) -> series.getData().add(new XYChart.Data<>(aShort,aDouble)));
        for (XYChart.Data<Number, Number> entry : series.getData()) {
            Tooltip t = new Tooltip(String.format("%.2f",entry.getYValue().doubleValue()) + "\n" + entry.getXValue());
            Tooltip.install(entry.getNode(), t);
        }
        return lineChart;
    }

    private SortedSet<Short> getYearRange() {
        return correctedDataList.stream().map(GaugeMeasurement::getMeasurementYear)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private Short returnMaxOrMinYear(Boolean returnMaxYear) {
        if (getYearRange().size() < 1) return 0;
        else {
            // dodac try/catch zawierajacy obsluge nosuchelementexeption
            if (returnMaxYear) {
                return selectedTownData.stream().filter(gaugeMeasurement -> gaugeMeasurement.getMeasurementYear() != 2019).map(GaugeMeasurement::getMeasurementYear).max(Short::compareTo).get();
            }
            return selectedTownData.stream().map(GaugeMeasurement::getMeasurementYear).min(Short::compareTo).get();
        }
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
                .filter(data -> data.getData2().doubleValue() < 9999)
                .collect(Collectors.toList());
    }

}

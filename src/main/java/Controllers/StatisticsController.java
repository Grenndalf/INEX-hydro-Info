package Controllers;

import DButils.DbActions;
import RegularClasses.Mediator.ControllerHolder;
import RegularClasses.Tables.GaugeMeasurement;
import RegularClasses.Utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.shape.Arc;

import java.util.List;
import java.util.stream.Collectors;


public class StatisticsController {
    @FXML
    public Arc dataArc;
    @FXML
    public Label correctDataInfo;


    DbActions dbActions = new DbActions();
    List<GaugeMeasurement> selectedTownData = dbActions.queryForDataOfSelectedTown(ControllerHolder.getInstance().getTownName());
    List<Double> correctedDataList = correctData(selectedTownData);

    @FXML
    void initialize() {
        initDataArcValues();
        initCorrectDataInfo();
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setMinorTickVisible(false);
        if (returnMaxOrMinYear(true) - returnMaxOrMinYear(false) > 50) {
            xAxis.setAutoRanging(false);
            xAxis.setTickUnit(5);
        }
        xAxis.setLowerBound(returnMaxOrMinYear(false)-1);
        xAxis.setUpperBound(returnMaxOrMinYear(true)+1);
        XYChart.Series series = new XYChart.Series();
        series.getData().add(new XYChart.Data<>());
    }

    private Short returnMaxOrMinYear(Boolean returnMaxYear) {
        if (selectedTownData.size() < 1) return 0;
        else {
            return returnMaxYear ?
                    selectedTownData.stream().map(GaugeMeasurement::getMeasurementYear).max(Short::compareTo).get()
                    : selectedTownData.stream().map(GaugeMeasurement::getMeasurementYear).min(Short::compareTo).get();
        }
    }

    private void initCorrectDataInfo() {
        correctDataInfo.setText(String.format("%.2f", (Utils.divideIntegers(correctedDataList.size(), selectedTownData.size()) * 100)) + "%");
    }

    private void initDataArcValues() {
        dataArc.setLength(Utils.divideIntegers(correctedDataList.size(), selectedTownData.size()) * 360);
        dataArc.setStartAngle(450 - Utils.divideIntegers(correctedDataList.size(), selectedTownData.size()) * 360);
    }

    private List<Double> correctData(List<GaugeMeasurement> dataToCorrect) {
        return dataToCorrect
                .stream()
                .map(gaugeMeasurement -> gaugeMeasurement.getData2().doubleValue())
                .filter(data -> data < 9999)
                .collect(Collectors.toList());
    }

}

package Controllers;

import Others.Mediator.ControllerHolder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class LeftMenuButtonsController {
    @FXML
    public AnchorPane leftMenuContainer;
    @FXML
    public ToggleGroup leftMenuToggleGroup;
    @FXML
    public Label townName;

    public Label getRiverName () {
        return riverName;
    }

    public void setRiverName (Label riverName) {
        this.riverName = riverName;
    }

    @FXML
    public Label riverName;

    @FXML
    void initialize() {
        ControllerHolder.getInstance().registerButtonLeftMenuButtonController(this);
        addToggleGroupListener();
        townName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null){
                townName.setText(oldValue);
            }
        });
        riverName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null){
                riverName.setText(oldValue);
            }
        });
    }

    private void addToggleGroupListener() {
        leftMenuToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                oldValue.setSelected(true);
            }
        });
    }

    @FXML
    void selectFileForImport() {
        ControllerHolder.getInstance().setMainContent("/FXML/Import.fxml");
    }

    @FXML
    void showGaugeList() {
        ControllerHolder.getInstance().setMainContent("/FXML/GaugeList.fxml");
    }

    @FXML
    void showStatistics() {
        ControllerHolder.getInstance().setMainContent("/FXML/Statistics.fxml");
    }

    @FXML
    void showTableToExport() {
        ControllerHolder.getInstance().setMainContent("/FXML/Export.fxml");
    }
}
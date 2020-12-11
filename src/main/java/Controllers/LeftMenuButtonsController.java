package Controllers;

import RegularClasses.Mediator.ControllerHolder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class LeftMenuButtonsController {
    @FXML
    public AnchorPane leftMenuContainer;
    @FXML
    public ToggleGroup leftMenuToggleGroup;
    @FXML
    public Label townName;

    @FXML
    void initialize() {
        ControllerHolder.getInstance().registerButtonLeftMenuButtonController(this);
        addToggleGroupListener();
        townName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null){
                townName.setText(oldValue);
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
    void selectFileForImport() throws IOException {
        ControllerHolder.getInstance().setMainContent("/FXML/Import.fxml");
    }

    @FXML
    void showGaugeList() {
        ControllerHolder.getInstance().setMainContent("/FXML/GaugeList.fxml");
    }

    @FXML
    void showStatistics() throws IOException {
        ControllerHolder.getInstance().setMainContent("/FXML/Statistics.fxml");
    }


    @FXML
    void showTableToExport() {
    }
}
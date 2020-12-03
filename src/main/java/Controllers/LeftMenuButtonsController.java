package Controllers;

import RegularClasses.Mediator.ControllerHolder;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class LeftMenuButtonsController {
    @FXML
    public AnchorPane leftMenuContainer;
    @FXML
    public ToggleGroup leftMenuToggleGroup;

    public MainController mainController;

    @FXML
    void initialize() {
        ControllerHolder.getInstance().registerButtonLeftMenuButtonController(this);
        addToggleGroupListener();
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
        ControllerHolder.getInstance().setMainContent("/fxml/Import.fxml");
    }

    @FXML
    void showGaugeList() {
        ControllerHolder.getInstance().setMainContent("/fxml/GaugeList.fxml");
    }

    @FXML
    void showStatistics() throws IOException {
        ControllerHolder.getInstance().setMainContent("/fxml/testPane.fxml");
    }


    @FXML
    void showTableToExport() {
    }
}
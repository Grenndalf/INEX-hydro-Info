package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;

public class LeftMenuButtonsController {
    @FXML
    private ToggleGroup leftMenuToggleGroup;

    private MainController mainController;

    @FXML
    void initialize() {
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
        mainController.setInMainWindow("/fxml/testPane.fxml");
    }

    @FXML
    void showGaugeList() {
        System.out.println("wodowskazy");
    }

    @FXML
    void showStatistics() throws IOException {
        mainController.setInMainWindow("/fxml/GaugeList.fxml");
        System.out.println("Staty");
    }


    @FXML
    void showTableToExport() {
        System.out.println("Excel export");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
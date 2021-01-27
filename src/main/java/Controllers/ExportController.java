package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import RegularClasses.Mediator.ControllerHolder;
import RegularClasses.Utils.FileExporter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class ExportController {

    @FXML
    public Button exportButton;

    GaugeDBActions ge = new GaugeDBActions();
    FileExporter fe = new FileExporter();
    DirectoryChooser directoryChooser = new DirectoryChooser();


    public void selectDestinationAndSaveFile() {
        directoryChooser.setTitle("Wybierz miejsce do zapisu pliku");
        File filePath = directoryChooser.showDialog(exportButton.getScene().getWindow());
        if (filePath != null) {
            fe.createWorkBook(ge.queryForDataOfSelectedTown(ControllerHolder.getInstance().getTownName()), filePath.getAbsolutePath());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Zakoñczono");
            alert.setContentText("Wykesportowano do pliku: " + ControllerHolder.getInstance().getTownName() + ".xlsx");
            alert.setHeaderText("zadanie wykonano");
            alert.showAndWait();
        }
    }
}

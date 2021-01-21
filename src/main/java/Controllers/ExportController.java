package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import RegularClasses.Mediator.ControllerHolder;
import RegularClasses.Utils.FileExporter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ExportController {

    @FXML
    public Button dupa;


    public void pritner(ActionEvent actionEvent) {
        FileExporter fe = new FileExporter();
        GaugeDBActions ge = new GaugeDBActions();

        fe.createWorkBook(ge.queryForDataOfSelectedTown(ControllerHolder.getInstance().getTownName()));

    }
}

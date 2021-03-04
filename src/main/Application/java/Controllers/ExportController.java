package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import Others.Mediator.ControllerHolder;
import Others.Utils.FileExporter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class ExportController {

    @FXML
    public Button exportButton;

    GaugeDBActions ge = new GaugeDBActions ();
    FileExporter fe = new FileExporter ();
    DirectoryChooser directoryChooser = new DirectoryChooser ();


    public void selectDestinationAndSaveFile () {
        directoryChooser.setTitle ("Wybierz miejsce do zapisu pliku");
        File filePath = directoryChooser.showDialog (exportButton.getScene ().getWindow ());
        if (filePath != null) {
            fe.createWorkBook (ge.queryForDataOfSelectedTownAndRiver (ControllerHolder.getInstance ().getRiverName (), ControllerHolder.getInstance ().getTownName ()),
                               filePath.getAbsolutePath ());
        }
    }
}

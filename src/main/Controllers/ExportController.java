package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import Others.Multihreading.FileExporter;
import Others.Multihreading.FileExporterService;
import Others.Utils.Calculations;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;

import java.io.File;

import static Others.Mediator.ControllerHolder.getInstance;

public class ExportController {

    @FXML
    private Button exportButton;

    GaugeDBActions ge = new GaugeDBActions ();

    DirectoryChooser directoryChooser = new DirectoryChooser ();

    @FXML
    private void selectDestinationAndSaveFile () {
        Calculations calculations =
                new Calculations (new GaugeDBActions ().getCorrectedDoubleMeasurementsList (getInstance ().getRiverName (),
                                                                                            getInstance ().getTownName ()));
        directoryChooser.setTitle ("Wybierz miejsce do zapisu pliku");

        File filePath = directoryChooser.showDialog (exportButton.getScene ().getWindow ());
        if (filePath != null) {
            FileExporter fe =
                    new FileExporter (ge.queryForDataOfSelectedTownAndRiver (getInstance ().getRiverName (),
                                                                             getInstance ().getTownName ()),
                                      calculations, filePath.getAbsolutePath ());
            FileExporterService service = new FileExporterService (fe);
            service.restart ();
        }
    }


}

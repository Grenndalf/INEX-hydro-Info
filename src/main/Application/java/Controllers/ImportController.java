package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import DButils.TableDBActions.RiverDBActions;
import RegularClasses.Multihreading.FIleDownloader;
import RegularClasses.Multihreading.FileDownloaderService;
import RegularClasses.Multihreading.FileImporter;
import RegularClasses.Multihreading.FileImporterService;
import RegularClasses.Utils.Utils;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

public class ImportController {


    public static final String IMPORT_ON_GOING = "import.on.going";
    public static final String WAIT = "wait";
    public static final String IMPORTING_ERROR = "Importing.error";
    public static final String ERROR_LIST = "error.list";
    public static final String NOT_IMPORTED_LIST = "not.imported.list";
    public static final String CSV_FILES = "csv.files";
    public static final String CSV = "*.csv";
    public static final String CANCEL = "Cancel";
    public static final String SET_DESTINATION = "Wybierz miejsce zapisu dla pobieranych plików";
    @FXML
    private JFXButton downloadButton;
    @FXML
    private JFXButton removeDataButton;
    @FXML
    private JFXButton importButton;
    @FXML
    private HBox barContainer;
    @FXML
    private HBox downloadInfoContainer;

    GaugeDBActions gaugeDBActions = new GaugeDBActions();
    RiverDBActions riverDBActions = new RiverDBActions();

    @FXML
    void initialize() {

        if (FileImporter.myTask != null) {
            showProgressBar();
        }
        if (FIleDownloader.myTask != null) {
            setLabelProperty();
        }
    }

    private void showProgressBar() {
        barContainer.getChildren().clear();

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(180);
        progressBar.setPrefHeight(40);
        progressBar.progressProperty().bind(FileImporter.myTask.progressProperty());

        Button button = new Button(CANCEL);
        button.setOnAction(event -> {
            FileImporter.myTask.cancel();
            barContainer.getChildren().removeAll(progressBar, button);
            FileImporter.myTask = null;
        });

        FileImporter.myTask.runningProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                barContainer.getChildren().clear();
                if (!FileImporter.importErrors.isEmpty()) {
                    Button showErrors = new Button(Utils.getResourceBundle().getString(ERROR_LIST));
                    showErrors.setOnAction(event -> showNotImportedData());
                    Region filler = new Region();
                    HBox.setHgrow(filler, Priority.ALWAYS);
                    barContainer.getChildren().addAll(showErrors, filler);
                }
                Label taskInfo = new Label("zadanie skoñczone");
                taskInfo.setPrefHeight(30);
                taskInfo.setPrefWidth(150);
                taskInfo.setFont(Font.font("System", FontWeight.BOLD, 16));
                taskInfo.setTextFill(Paint.valueOf("#317FFF"));
                barContainer.getChildren().add(taskInfo);

            }
        });

        barContainer.getChildren().addAll(progressBar, button);
    }

    //pomyœleæ jak by tu importowac du¿¹ iloœæ danych - moze do pliku tekstowego?
    private void showNotImportedData() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(Utils.getResourceBundle().getString(NOT_IMPORTED_LIST));
        TextArea textArea = new TextArea();
        alert.getDialogPane().setContent(textArea);
        for (String line : FileImporter.importErrors)
            textArea.setText(textArea.getText() + line);
        alert.showAndWait();
    }

    private void alertTemplate(Alert.AlertType alertType, String s, String s2, String s3) {
        Alert alert = new Alert(alertType);
        alert.setTitle(s);
        alert.setContentText(s2);
        alert.setHeaderText(s3);
        alert.showAndWait();
    }

    public void chooseFiles() {
        importButton.disableProperty().set(true);
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter(Utils.getResourceBundle().getString(CSV_FILES), CSV);
        fileChooser.getExtensionFilters().add(extFilter);
        if (FileImporter.myTask == null) {
            List<File> fileList = fileChooser.showOpenMultipleDialog(new Stage());
            if (fileList != null) {
                FileImporterService fileImporterService = new FileImporterService(fileList);
                fileImporterService.start();
                Platform.runLater(this::showProgressBar);
            }
        } else {
            alertTemplate(Alert.AlertType.ERROR,
                    Utils.getResourceBundle().getString(IMPORT_ON_GOING),
                    Utils.getResourceBundle().getString(WAIT),
                    Utils.getResourceBundle().getString(IMPORTING_ERROR));
        }
        importButton.disableProperty().set(false);
    }

    public void download() {
        DirectoryChooser destination = new DirectoryChooser();
        destination.setTitle(SET_DESTINATION);
        File selectedDirectory = destination.showDialog(downloadInfoContainer.getScene().getWindow());
        if (selectedDirectory != null) {
            FIleDownloader.PATHNAME = selectedDirectory.getAbsolutePath();
        } else {
            return;
        }

        if (FIleDownloader.myTask == null) {
            FileDownloaderService fileDownloaderService = new FileDownloaderService();
            fileDownloaderService.start();
            Platform.runLater(this::setLabelProperty);
        } else {
            alertTemplate(Alert.AlertType.CONFIRMATION,
                    "trwa pobieranie",
                    "proszê czekaæ",
                    "pobieranie w toku.");
        }
    }

    public void removeDataFromDataBase() {
        removeDataButton.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        StringBuilder info = new StringBuilder();
        info.append("Usuniêto ");
        info.append(gaugeDBActions.removeAllMeasurements());
        info.append(" wierszy z tabeli wszystkie dane oraz ");
        info.append(riverDBActions.removeAllRivers());
        info.append(" wierszy z tabeli Rzeki");
        alert.setContentText(info.toString());
        alert.showAndWait();
        removeDataButton.setDisable(false);
    }

    public void setLabelProperty() {

        downloadInfoContainer.getChildren().clear();
        Label label = new Label();
        label.textProperty().bind(FIleDownloader.myTask.messageProperty());
        downloadInfoContainer.getChildren().add(label);

        FIleDownloader.myTask.runningProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                downloadInfoContainer.getChildren().clear();
                Label taskInfo = new Label("Pobrano i Rozpakowano Pliki");
                taskInfo.setPrefHeight(30);
                taskInfo.setPrefWidth(250);
                taskInfo.setFont(Font.font("System", FontWeight.BOLD, 16));
                taskInfo.setTextFill(Paint.valueOf("#317FFF"));
                downloadInfoContainer.getChildren().add(taskInfo);
            }
        });
    }

}


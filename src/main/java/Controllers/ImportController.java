package Controllers;


import RegularClasses.Multihreading.FileImporter;
import RegularClasses.Multihreading.FileImporterService;
import RegularClasses.Utils.Utils;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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

    @FXML
    public HBox barContainer;
    @FXML
    public Button selectFilesButton;
    @FXML
    AnchorPane pane;

    @FXML
    void initialize() {
        if (FileImporter.myTask != null) {
            showProgressBar();
        }
    }

    private void showProgressBar() {
        JFXProgressBar progressBar = new JFXProgressBar();
        progressBar.setPrefWidth(180);
        progressBar.setPrefHeight(40);
        progressBar.progressProperty().bind(FileImporter.myTask.progressProperty());
        Button button = new Button("Cancel");
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
                    showErrors.setOnAction(event -> {
                        showNotImportedData();
                    });
                    Region filler = new Region();
                    HBox.setHgrow(filler, Priority.ALWAYS);
                    barContainer.getChildren().addAll(showErrors, filler);
                }
                barContainer.getChildren().add(new Label("zadanie skoñczone"));

            }
        });
        barContainer.getChildren().addAll(progressBar, button);
    }

    private void showNotImportedData() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(Utils.getResourceBundle().getString(NOT_IMPORTED_LIST));
        TextArea textArea = new TextArea();
        FileImporter.importErrors.forEach(line -> textArea.setText(textArea.getText() + line));
        alert.getDialogPane().setContent(textArea);
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
        selectFilesButton.disableProperty().set(true);
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter(Utils.getResourceBundle().getString(CSV_FILES), CSV);
        fileChooser.getExtensionFilters().add(extFilter);
        List<File> fileList = fileChooser.showOpenMultipleDialog(new Stage());
        if (fileList != null) {
            if (FileImporter.myTask == null) {
                FileImporterService fileImporterService = new FileImporterService(fileList);
                fileImporterService.start();
                Platform.runLater(this::showProgressBar);
            } else {
                alertTemplate(Alert.AlertType.ERROR,
                        Utils.getResourceBundle().getString(IMPORT_ON_GOING),
                        Utils.getResourceBundle().getString(WAIT),
                        Utils.getResourceBundle().getString(IMPORTING_ERROR));
            }
        }
        selectFilesButton.disableProperty().set(false);
    }

    public void selectFiles(ActionEvent event) {
        chooseFiles();
    }
}

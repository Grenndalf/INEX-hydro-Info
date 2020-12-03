package Controllers;


import RegularClasses.FileImporter;
import RegularClasses.FileImporterService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

public class ImportController {

    public static final String IMPORT_ON_GOING = "import.on.going";
    public static final String WAIT = "wait";
    public static final String IMPORTING_ERROR = "Importing.error";
    public static final String BUNDLES = "Bundles.labels";
    public static final String TASK_EXECUTED = "task.executed";
    public static final String ERROR_LIST = "error.list";
    public static final String NOT_IMPORTED_LIST = "not.imported.list";
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
        ProgressBar progressBar = new ProgressBar();
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
                    Button showErrors = new Button(getResourceBundle().getString(ERROR_LIST));
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
        alert.setHeaderText(getResourceBundle().getString(NOT_IMPORTED_LIST));
        TextArea textArea = new TextArea();
        FileImporter.importErrors.forEach(line -> textArea.setText(textArea.getText() + line));
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle(BUNDLES);
    }

    private void alertTemplate(Alert.AlertType alertType, String s, String s2, String s3) {
        Alert alert = new Alert(alertType);
        alert.setTitle(s);
        alert.setContentText(s2);
        alert.setHeaderText(s3);
        alert.showAndWait();
    }

    private void showInfoDialogWindow() {
        alertTemplate(Alert.AlertType.INFORMATION,
                getResourceBundle().getString(TASK_EXECUTED),
                "wykonano zadanie.", "trolololo");
    }

    public void chooseFiles() {
        selectFilesButton.disableProperty().set(true);
        FileChooser fileChooser = new FileChooser();
        List<File> fileList = fileChooser.showOpenMultipleDialog(new Stage());
        if (fileList != null) {
            if (FileImporter.myTask == null) {
                FileImporterService fileImporterService = new FileImporterService(fileList);
                fileImporterService.start();
                Platform.runLater(this::showProgressBar);
            } else {
                alertTemplate(Alert.AlertType.ERROR,
                        getResourceBundle().getString(IMPORT_ON_GOING),
                        getResourceBundle().getString(WAIT),
                        getResourceBundle().getString(IMPORTING_ERROR));
            }
        }
        selectFilesButton.disableProperty().set(false);
    }

    public void setPathTest(ActionEvent event) {
        chooseFiles();
    }
}

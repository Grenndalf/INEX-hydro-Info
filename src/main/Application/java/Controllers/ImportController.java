package Controllers;

import App.App;
import DButils.TableDBActions.GaugeDBActions;
import DButils.TableDBActions.RiverDBActions;
import Others.Multihreading.FIleDownloader;
import Others.Multihreading.FileDownloaderService;
import Others.Multihreading.FileImporter;
import Others.Multihreading.FileImporterService;
import Others.Utils.Utils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
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
    public Pane testPane;
    GaugeDBActions gaugeDBActions = new GaugeDBActions ();
    RiverDBActions riverDBActions = new RiverDBActions ();
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
    @FXML
    private JFXTextArea downloadTextArea;
    @FXML
    private JFXTextArea importTextArea;
    @FXML
    private JFXTextArea removeDataTextArea;

    @FXML
    void initialize () {

        if (FileImporter.myTask != null) {
            showProgressBar ();
        }
        if (FIleDownloader.myTask != null) {
            setLabelProperty ();
        }
        showHelpOnHover (downloadButton, downloadTextArea);
        showHelpOnHover (importButton, importTextArea);
        showHelpOnHover (removeDataButton, removeDataTextArea);
        testing ();
    }

    private void showHelpOnHover (Button button, JFXTextArea textArea) {
        button.setOnMouseEntered (event -> textArea.setVisible (true));
        button.setOnMouseExited (event -> textArea.setVisible (false));
    }

    private void showProgressBar () {
        barContainer.getChildren ().clear ();

        ProgressBar progressBar = new ProgressBar ();
        progressBar.setPrefWidth (180);
        progressBar.setPrefHeight (40);
        progressBar.progressProperty ().bind (FileImporter.myTask.progressProperty ());

        Button button = new Button (CANCEL);
        button.setOnAction (event -> {
            FileImporter.myTask.cancel ();
            barContainer.getChildren ().removeAll (progressBar, button);
            FileImporter.myTask = null;
        });

        FileImporter.myTask.runningProperty ().addListener ((observable, oldValue, newValue) -> {
            if (!newValue) {
                barContainer.getChildren ().clear ();
                if (!FileImporter.importErrors.isEmpty ()) {
                    Button showErrors = new Button (Utils.getResourceBundle ().getString (ERROR_LIST));
                    showErrors.setOnAction (event -> showNotImportedData ());
                    Region filler = new Region ();
                    HBox.setHgrow (filler, Priority.ALWAYS);
                    barContainer.getChildren ().addAll (showErrors, filler);
                }
                Label taskInfo = new Label ("zadanie skoñczone");
                taskInfo.setPrefHeight (30);
                taskInfo.setPrefWidth (150);
                taskInfo.setFont (Font.font ("System", FontWeight.BOLD, 16));
                taskInfo.setTextFill (Paint.valueOf ("#317FFF"));
                barContainer.getChildren ().add (taskInfo);

            }
        });

        barContainer.getChildren ().addAll (progressBar, button);
    }

    //pomyœleæ jak by tu importowac du¿¹ iloœæ danych - moze do pliku tekstowego?
    private void showNotImportedData () {
        Alert alert = new Alert (Alert.AlertType.ERROR);
        alert.setHeaderText (Utils.getResourceBundle ().getString (NOT_IMPORTED_LIST));
        TextArea textArea = new TextArea ();
        alert.getDialogPane ().setContent (textArea);
        for (String line : FileImporter.importErrors)
            textArea.setText (textArea.getText () + line);
        alert.showAndWait ();
    }

    private void alertTemplate (Alert.AlertType alertType, String s, String s2, String s3) {
        Alert alert = new Alert (alertType);
        alert.setTitle (s);
        alert.setContentText (s2);
        alert.setHeaderText (s3);
        alert.showAndWait ();
    }

    public void chooseFiles () {
        importButton.disableProperty ().set (true);
        FileChooser fileChooser = new FileChooser ();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter (Utils.getResourceBundle ().getString (CSV_FILES), CSV);
        fileChooser.getExtensionFilters ().add (extFilter);
        if (FileImporter.myTask == null) {
            List<File> fileList = fileChooser.showOpenMultipleDialog (new Stage ());
            if (fileList != null) {
                FileImporterService fileImporterService = new FileImporterService (fileList);
                fileImporterService.start ();
                Platform.runLater (this::showProgressBar);
            }
        } else {
            alertTemplate (Alert.AlertType.ERROR,
                           Utils.getResourceBundle ().getString (IMPORT_ON_GOING),
                           Utils.getResourceBundle ().getString (WAIT),
                           Utils.getResourceBundle ().getString (IMPORTING_ERROR));
        }
        importButton.disableProperty ().set (false);
    }

    public void download () {
        DirectoryChooser destination = new DirectoryChooser ();
        destination.setTitle (SET_DESTINATION);
        File selectedDirectory = destination.showDialog (downloadInfoContainer.getScene ().getWindow ());
        if (selectedDirectory != null) {
            FIleDownloader.PATHNAME = selectedDirectory.getAbsolutePath ();
        } else {
            return;
        }

        if (FIleDownloader.myTask == null) {
            FileDownloaderService fileDownloaderService = new FileDownloaderService ();
            fileDownloaderService.start ();
            Platform.runLater (this::setLabelProperty);
        } else {
            alertTemplate (Alert.AlertType.CONFIRMATION,
                           "trwa pobieranie",
                           "proszê czekaæ",
                           "pobieranie w toku.");
        }
    }

    public void removeDataFromDataBase () {
        removeDataButton.setDisable (true);
        Alert alert = new Alert (Alert.AlertType.INFORMATION);
        StringBuilder info = new StringBuilder ();
        info.append ("Usuniêto ");
        info.append (gaugeDBActions.removeAllMeasurements ());
        info.append (" wierszy z tabeli wszystkie dane oraz ");
        info.append (riverDBActions.removeAllRivers ());
        info.append (" wierszy z tabeli Rzeki");
        alert.setContentText (info.toString ());
        alert.showAndWait ();
        removeDataButton.setDisable (false);
    }

    public void setLabelProperty () {
        downloadInfoContainer.getChildren ().clear ();
        Label label = new Label ();
        label.textProperty ().bind (FIleDownloader.myTask.messageProperty ());
        downloadInfoContainer.getChildren ().add (label);

        FIleDownloader.myTask.runningProperty ().addListener ((observable, oldValue, newValue) -> {
            if (!newValue) {
                downloadInfoContainer.getChildren ().clear ();
                Label taskInfo = new Label ("Pobrano i Rozpakowano Pliki");
                taskInfo.setPrefHeight (30);
                taskInfo.setPrefWidth (250);
                taskInfo.setFont (Font.font ("System", FontWeight.BOLD, 16));
                taskInfo.setTextFill (Paint.valueOf ("#317FFF"));
                downloadInfoContainer.getChildren ().add (taskInfo);
            }
        });
    }

    public void popup () {
        final Stage dialog = new Stage ();
        Button answerYes = new Button ("Tak");
        Button answerCancel = new Button ("Anuluj");

        Label displayLabel = new Label ("Czy na pewno chcesz usun¹æ wszystkie dane ?");
        displayLabel.setFont (Font.font (null, FontWeight.BOLD, 14));

        dialog.setTitle ("Wymagane Potwierdzenie");
        dialog.getIcons ().add (new Image (App.INEX_LOGO_PNG));
        dialog.initModality (Modality.APPLICATION_MODAL);
        dialog.initOwner (removeDataButton.getScene ().getWindow ());

        HBox dialogHbox = new HBox (20);
        dialogHbox.setAlignment (Pos.CENTER);

        VBox dialogVbox1 = new VBox (20);
        dialogVbox1.setAlignment (Pos.CENTER_LEFT);

        VBox dialogVbox2 = new VBox (20);
        dialogVbox2.setAlignment (Pos.CENTER_RIGHT);

        dialogHbox.getChildren ().add (displayLabel);
        dialogVbox1.getChildren ().add (answerYes);
        dialogVbox2.getChildren ().add (answerCancel);

        answerYes.setOnMouseClicked (e -> {
            removeDataFromDataBase ();
            dialog.close ();
        });
        answerCancel.setOnMouseClicked (e -> dialog.close ());

        dialogHbox.getChildren ().addAll (dialogVbox1, dialogVbox2);
        Scene dialogScene = new Scene (dialogHbox, 500, 40);
        dialog.setScene (dialogScene);
        dialog.show ();
        Runnable runnable = () -> {
            try {
                Thread.sleep (3000);
                Platform.runLater (dialog::close);
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
        };
        Platform.runLater (runnable);
    }

    public void testing () {
        ImageView gif = new ImageView (new Image("/Images/loadingspinner.gif",200,200,true,true));
        gif.resize (200 ,200);
        testPane.getChildren ().add (gif);

                Task task = new Task () {
                    @Override
                    protected Object call () throws Exception {
                        Thread.sleep (5000);
                        return null;
                    }
                };
                task.setOnSucceeded (event -> Platform.runLater (() -> testPane.getChildren ().clear ()));
                new Thread (task).start ();
            }
}


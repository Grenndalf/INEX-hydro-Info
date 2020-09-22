package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class MyTestScrollPane {
    @FXML
    private Button setPathTestButton;
    @FXML
    private VBox myVbox;


    FileChooser fileChooser = new FileChooser();

    @FXML
    void initialize() {
        setSetPathTestButtonAction();
    }


    @FXML
    void setPathTest() {
    }

    private void setSetPathTestButtonAction() {
        setPathTestButton.setOnAction(e -> {
//            directoryChooser.setTitle("wybierz los folderos");
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("pliki z danymi(.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setTitle("dupa");
            try {
                gimmeFile();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    private void gimmeFile() throws Exception {
        List<File> fileList = fileChooser.showOpenMultipleDialog(new Stage());
        if (fileList != null) {
            for (int i = 0; i < myVbox.getChildren().toArray().length; i++) {
                if (myVbox.getChildren().get(i) instanceof Label) {
                    myVbox.getChildren().remove(myVbox.getChildren().get(i));
                }
              for (File file:fileList) {
                  Scanner scanner = null;
                  BufferedReader br = null;
                  try {
                      int numberOfLines = 0;
                      br = new BufferedReader(new FileReader(file));
                      scanner = new Scanner(br);
                      while (scanner.hasNextLine()) {
                          String st = scanner.nextLine();
                          System.out.println(numberOfLines + st);
                          numberOfLines++;
                      }
                      System.out.println(numberOfLines);
                  } catch (Exception e) {
                      System.out.println(e);
                  } finally {
                      br.close();
                      scanner.close();
                  }
              }
            }
            fileList.forEach(n -> {
                Label label = new Label();
                label.setText(n.getName());
                label.setStyle("-fx-text-fill: white;");
                myVbox.getChildren().add(label);
            });
        } else {
            System.out.println("anulowano");
        }

    }


}

package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainController {

    @FXML
    private Parent mainParent;

    @FXML
    private LeftMenuButtonsController leftMenuButtonsController;
    @FXML
    private Pane test;

    @FXML
    void initialize() {
        //trzeba przekazac referencje tej klasy do klasy left... bo leci NullPoiter.
        leftMenuButtonsController.setMainController(this);
    }

    public Parent getMainParent() {
        return mainParent;
    }

    public void setMainParent(Parent mainParent) {
        this.mainParent = mainParent;
    }

    public Pane getTest() {
        return test;
    }

    public void setTest(Pane test) {
        this.test = test;
    }
    public void setInMainWindow(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(path));
        AnchorPane testPane = loader.load();
        getTest().getChildren().clear();
        testPane.prefWidthProperty().bind(getTest().prefWidthProperty());
        testPane.prefHeightProperty().bind(getTest().prefHeightProperty());
        getTest().getChildren().add(testPane);
    }
}

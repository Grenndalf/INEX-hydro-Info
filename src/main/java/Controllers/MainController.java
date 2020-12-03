package Controllers;

import RegularClasses.Mediator.ControllerHolder;
import RegularClasses.Utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.io.IOException;

public class MainController {

    @FXML
    private AnchorPane leftMenuContainer;

    @FXML
    private AnchorPane contentContainer;

    @FXML
    private AnchorPane mainParent;

    @FXML
    void initialize() throws IOException {
        ControllerHolder.getInstance().registerMainController(this);
        setLeftMenu();
    }

    private void setLeftMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/LeftMenuButtons.fxml"), Utils.getResourceBundle());
        leftMenuContainer.getChildren().clear();
        Region loadedContent = loader.load();
        leftMenuContainer.getChildren().add(loadedContent);
    }

    public void setInMainWindow(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(path),Utils.getResourceBundle());
        contentContainer.getChildren().clear();
        // musi zostaæ region jako element do ktorego jest przypisany loader,
        // w jego przypadku mozna bindowac wlasciwosc prefWidthprop z prefWithprop rodzica itd.
        Region loadedContent = loader.load();
        contentContainer.getChildren().add(loadedContent);
        loadedContent.prefWidthProperty().bind(contentContainer.widthProperty());
        loadedContent.prefHeightProperty().bind(contentContainer.heightProperty());
    }
}

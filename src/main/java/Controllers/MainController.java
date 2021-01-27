package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import DButils.TableDBActions.HibernateFactory;
import DButils.Tables.River;
import RegularClasses.Mediator.ControllerHolder;
import RegularClasses.Utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    public Button testy;
    @FXML
    private AnchorPane leftMenuContainer;

    @FXML
    private AnchorPane contentContainer;

    @FXML
    private AnchorPane mainParent;

    GaugeDBActions gaugeDBActions = new GaugeDBActions();

    @FXML
    void initialize() throws IOException {
        ControllerHolder.getInstance().registerMainController(this);
        setLeftMenu();
    }

    private void setLeftMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/FXML/LeftMenu.fxml"), Utils.getResourceBundle());
        leftMenuContainer.getChildren().clear();
        Parent loadedContent = loader.load();
        leftMenuContainer.getChildren().add(loadedContent);
    }

    public void setInMainWindow(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(path), Utils.getResourceBundle());
        contentContainer.getChildren().clear();
        // musi zostaæ region jako element do ktorego jest przypisany loader,
        // w jego przypadku mozna bindowac wlasciwosc prefWidthprop z prefWithprop rodzica itd.
        Region loadedContent = loader.load();
        contentContainer.getChildren().add(loadedContent);
        loadedContent.prefWidthProperty().bind(contentContainer.widthProperty());
        loadedContent.prefHeightProperty().bind(contentContainer.heightProperty());
    }

    public void runtest(ActionEvent event) {
        char a = 'a';
        System.out.println((char) a+1);
    }
}

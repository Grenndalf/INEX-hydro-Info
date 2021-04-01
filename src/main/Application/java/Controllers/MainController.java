package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import Others.Mediator.ControllerHolder;
import Others.Utils.Calculations;
import Others.Utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class MainController {

    @FXML
    private AnchorPane leftMenuContainer;

    @FXML
    private AnchorPane contentContainer;

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

}

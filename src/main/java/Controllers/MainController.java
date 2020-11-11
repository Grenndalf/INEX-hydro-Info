package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import java.io.IOException;

public class MainController {

    @FXML
    private Parent mainParent;

    @FXML
    private LeftMenuButtonsController leftMenuButtonsController;


    public AnchorPane getFxmlContainer() {
        return fxmlContainer;
    }

    @FXML
    private AnchorPane fxmlContainer;

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

    public void setInMainWindow(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(path));
        System.out.println(getFxmlContainer().getChildren().size() + "dzieci cilda");
        System.out.println(getMainParent().getChildrenUnmodifiable().size() + "dzieci parenta");
        getFxmlContainer().getChildren().clear();
        // musi zostaæ region jako element do ktorego jest przypisany loader,
        // w jego przypadku mozna bindowac wlasciwosc prefWidthprop z prefWithprop rodzica itd.
        Region loadedContent = loader.load();
        getFxmlContainer().getChildren().add(loadedContent);
        loadedContent.prefWidthProperty().bind(getFxmlContainer().widthProperty());
        loadedContent.prefHeightProperty().bind(getFxmlContainer().heightProperty());
    }
}

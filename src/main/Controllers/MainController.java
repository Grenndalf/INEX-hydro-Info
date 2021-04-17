package Controllers;

import Others.Mediator.ControllerHolder;
import Others.Utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.io.IOException;

public class MainController {

    @FXML
    private AnchorPane leftMenuContainer;

    @FXML
    private AnchorPane contentContainer;

    @FXML
    void initialize () throws IOException {
        ControllerHolder.getInstance ().registerMainController (this);
        setLeftMenu ();
    }

    private void setLeftMenu () throws IOException {
        FXMLLoader loader = new FXMLLoader (this.getClass ().getResource ("/FXML/LeftMenu.fxml"),
                                            Utils.getResourceBundle ());
        leftMenuContainer.getChildren ().clear ();
        Region loadedContent = loader.load ();
        leftMenuContainer.getChildren ().add (loadedContent);
        // musi zostaæ region jako element do ktorego jest przypisany loader,
        // w jego przypadku mozna bindowac wlasciwosc prefWidthprop z prefWithprop rodzica itd.
        loadedContent.prefWidthProperty ().bind (leftMenuContainer.widthProperty ());
        loadedContent.prefHeightProperty ().bind (leftMenuContainer.heightProperty ());
    }

    public void setInMainWindow (String path) throws IOException {
        FXMLLoader loader = new FXMLLoader (this.getClass ().getResource (path), Utils.getResourceBundle ());
        contentContainer.getChildren ().clear ();
        // musi zostaæ region jako element do ktorego jest przypisany loader,
        // w jego przypadku mozna bindowac wlasciwosc prefWidthprop z prefWithprop rodzica itd.
        Region loadedContent = loader.load ();
        contentContainer.getChildren ().add (loadedContent);
        loadedContent.prefWidthProperty ().bind (contentContainer.widthProperty ());
        loadedContent.prefHeightProperty ().bind (contentContainer.heightProperty ());
    }

    public void testIT () {

    }
}

package App;

import DButils.DbActions;
import RegularClasses.GaugeMeasurement;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    private final String BUNDLES = "Bundles.labels";
    private final String MAINFRAME = "/fxml/MainContainer.fxml";

    public static void main(String[] args) throws SQLException, IOException {
        launch(args);
    }

    private static void testy(GaugeMeasurement gaugeMeasurement) {
        DbActions dbActions = new DbActions();
        dbActions.creatOrUpdate(gaugeMeasurement);
    }

    public void start(Stage primaryStage) throws Exception {

        Locale.setDefault(Locale.forLanguageTag("pl"));
        Scene scene = new Scene(initFxml().load());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public FXMLLoader initFxml() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(MAINFRAME), getResourceBundle());
        return loader;
    }

    private ResourceBundle getResourceBundle() {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLES);
        return bundle;
    }
}

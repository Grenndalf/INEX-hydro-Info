package App;

import DButils.DbManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    private final String BUNDLES = "Bundles.labels";
    private final String MAINFRAMEPATH = "/fxml/MainContainer.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        DbManager.initDatabase();
        Locale.setDefault(Locale.forLanguageTag("pl"));
        Scene scene = new Scene(initFxml().load());
//        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public FXMLLoader initFxml() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(MAINFRAMEPATH), getResourceBundle());
        return loader;
    }

    private ResourceBundle getResourceBundle() {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLES);
        return bundle;
    }
}

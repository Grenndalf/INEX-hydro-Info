package App;

import DButils.DbManager;
import RegularClasses.Utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Locale;

import static com.sun.org.apache.bcel.internal.util.SecuritySupport.getResourceAsStream;

public class App extends Application {


    private final String MAINFRAMEPATH = "/FXML/MainContainer.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        DbManager.initDatabase();
        Locale.setDefault(Locale.forLanguageTag("pl"));
        primaryStage.getIcons().add(new Image("Images/inex_logo.png"));
        Scene scene = new Scene(initFxml().load());
//        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public FXMLLoader initFxml() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(MAINFRAMEPATH), Utils.getResourceBundle());
        return loader;
    }


}

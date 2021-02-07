package App;

import Others.Utils.Utils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Locale;

import static com.sun.org.apache.bcel.internal.util.SecuritySupport.getResourceAsStream;

public class App extends Application {


    public static final String INEX_LOGO_PNG = "Images/inex_logo.png";
    private final String MAINFRAMEPATH = "/FXML/MainContainer.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(Locale.forLanguageTag("pl"));
        primaryStage.getIcons().add(new Image(INEX_LOGO_PNG));
        Scene scene = new Scene(initFxml().load());
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public FXMLLoader initFxml() {
        return new FXMLLoader(this.getClass().getResource(MAINFRAMEPATH), Utils.getResourceBundle());
    }


}

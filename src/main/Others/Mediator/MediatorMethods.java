package Others.Mediator;

import Controllers.LeftMenuButtonsController;
import Controllers.MainController;

public interface MediatorMethods {

    void registerMainController(MainController controller);
    void registerLeftMenuButtonController (LeftMenuButtonsController controller);
    void setMainContent(String pathToFxml);
    void setTownName(String townName);

    String getRiverName ();

    void setRiverName (String riverName);

    String getTownName();

}

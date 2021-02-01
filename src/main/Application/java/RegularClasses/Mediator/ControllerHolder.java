package RegularClasses.Mediator;

import Controllers.LeftMenuButtonsController;
import Controllers.MainController;

import java.io.IOException;

public class ControllerHolder implements MediatorMethods {

    private MainController mainController;
    private LeftMenuButtonsController menuButtonsController;

    private ControllerHolder() {
    }

    @Override
    public void registerMainController(MainController controller) {
        mainController = controller;
    }

    @Override
    public void registerButtonLeftMenuButtonController(LeftMenuButtonsController controller) {
        menuButtonsController = controller;
    }

    @Override
    public void setMainContent(String pathToFxml) {
        try {
            mainController.setInMainWindow(pathToFxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTownName(String townName) {
        menuButtonsController.townName.setText(townName);
    }

    @Override
    public String getTownName() {
        return menuButtonsController.townName.getText();
    }

    public static ControllerHolder getInstance() {
        return ControllerMediatorHolder.INSTANCE;
    }

    private static class ControllerMediatorHolder {
        private static final ControllerHolder INSTANCE = new ControllerHolder();
    }
}

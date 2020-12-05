package Controllers;

import DButils.DbActions;
import RegularClasses.Alphabet;
import RegularClasses.TownList;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class GaugeListController {
    @FXML
    public FlowPane buttonContainer;
    @FXML
    public JFXListView townListView;
    @FXML
    public Label gaugeQuantity;
    ToggleGroup toggleGroup = new ToggleGroup();
    DbActions dbActions = new DbActions();

    @FXML
    void initialize() {
        createButtons();
    }

    private void createButtons() {
        Arrays.stream(Alphabet.values()).forEach(letter -> {
            ToggleButton button = new ToggleButton(letter.name());
            button.getStyleClass().add("toggleButtons");
            setListViewValues(letter, button);
            addToggleButtonListener();
            toggleGroup.getToggles().add(button);
            buttonContainer.getChildren().add(button);
        });
    }

    private void setListViewValues(Alphabet letter, ToggleButton button) {
        button.setOnMouseClicked(event -> {
            ObservableList<String> items = FXCollections.observableArrayList(dbActions.queryForOneTownStartedWithLetter(letter.name())
                    .stream()
                    .map(TownList::getTownName).sorted(Comparator.naturalOrder()).collect(Collectors.toList()));
            townListView.setItems(items);
            gaugeQuantity.setText(String.valueOf(items.size()));
        });
    }

    private void addToggleButtonListener() {
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                toggleGroup.selectToggle(oldValue);
            }
        });
    }


}

package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import DButils.TableDBActions.RiverDBActions;
import DButils.Tables.River;
import RegularClasses.Mediator.ControllerHolder;
import RegularClasses.Utils.Alphabet;
import com.jfoenix.controls.JFXListView;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class GaugeListController {
    @FXML
    public FlowPane buttonContainer;
    @FXML
    public JFXListView<String> townListView;
    @FXML
    public Label gaugeQuantity;
    public JFXListView<String> riverListView;
    ToggleGroup toggleGroup = new ToggleGroup();
    RiverDBActions riverDBActions = new RiverDBActions();
    GaugeDBActions gaugeDBActions = new GaugeDBActions();

    @FXML
    void initialize() {
        createButtons();
        setTownListView();
    }

    private void createButtons() {
        Arrays.stream(Alphabet.values()).forEach(letter -> {
            ToggleButton button = new ToggleButton(letter.name());
            button.getStyleClass().add("toggleButtons");
            addToggleButtonListener();
            setListViewValuesOnClickedButton(button);
            toggleGroup.getToggles().add(button);
            buttonContainer.getChildren().add(button);
        });
    }

    private void setListViewValuesOnClickedButton(ToggleButton button) {
        button.setOnMouseClicked(event -> {
            ObservableList<String> items =
                    FXCollections.observableArrayList
                            (riverDBActions.queryForOneRiverStartedWithLetter
                                    (button.getText())
                                    .stream()
                                    .map(River::getRiverName)
                                    .collect(Collectors.toList()));
            riverListView.setItems(items);
            gaugeQuantity.setText(String.valueOf(items.size()));
        });
    }

    private void setTownListView() {
        riverListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ObservableList<String> items;
                items = FXCollections.observableArrayList(
                        new HashSet<>(gaugeDBActions.getTownListOfSelectedRiver(newValue)));
                townListView.setItems(items);
            } else {
                ObservableList<String> emptyList = new SimpleListProperty<>();
                townListView.setItems(emptyList);
            }
        });
        townListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ControllerHolder.getInstance().setTownName(newValue);
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

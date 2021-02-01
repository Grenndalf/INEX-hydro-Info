package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import DButils.TableDBActions.RiverDBActions;
import DButils.Tables.River;
import RegularClasses.Mediator.ControllerHolder;
import com.jfoenix.controls.JFXListView;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;

import java.util.HashSet;

public class GaugeListController {
    private static final String TOGGLE_BUTTONS = "toggleButtons";
    @FXML
    private FlowPane buttonContainer;
    @FXML
    private JFXListView<String> townListView;
    @FXML
    private Label gaugeQuantity;
    @FXML
    private JFXListView<River> riverListView;
    ToggleGroup toggleGroup = new ToggleGroup();
    RiverDBActions riverDBActions = new RiverDBActions();
    GaugeDBActions gaugeDBActions = new GaugeDBActions();

    @FXML
    void initialize() {
        createButtons();
        addRiverListViewListener();
        addTownListViewListener();
    }

    private void createButtons() {
        final char[] input = "A¥BCDEÊFGHIJKL£MNOÓPRSŒTUWYZ¯".toCharArray();
        for (char c : input) {
            ToggleButton button = new ToggleButton(String.valueOf(c));
            button.getStyleClass().add(TOGGLE_BUTTONS);
            addToggleButtonListener();
            setListViewValuesOnClickedButton(button);
            toggleGroup.getToggles().add(button);
            buttonContainer.getChildren().add(button);
        }
    }

    private void setListViewValuesOnClickedButton(ToggleButton button) {
        button.setOnMouseClicked(event -> {
            ObservableList<River> items = FXCollections.observableArrayList
                    (riverDBActions.queryForOneRiverStartedWithLetter(button.getText()));
            riverListView.setItems(items);
            gaugeQuantity.setText(String.valueOf(items.size()));
        });
    }

    private void addTownListViewListener() {
        townListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                ControllerHolder.getInstance().setTownName(newValue));
    }

    private void addRiverListViewListener() {
        riverListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ObservableList<String> items;
                items = FXCollections.observableArrayList(
                        new HashSet<>(gaugeDBActions.getTownListOfSelectedRiver(newValue.getRiverName())));
                townListView.setItems(items);
            } else {
                ObservableList<String> emptyList = new SimpleListProperty<>();
                townListView.setItems(emptyList);
            }
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

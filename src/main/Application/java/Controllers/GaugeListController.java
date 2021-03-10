package Controllers;

import DButils.TableDBActions.GaugeDBActions;
import DButils.TableDBActions.RiverDBActions;
import DButils.Tables.River;
import com.jfoenix.controls.JFXListView;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;

import java.util.Comparator;
import java.util.HashSet;

import static Others.Mediator.ControllerHolder.getInstance;

public class GaugeListController {
    public static final String ALPHABET = "A•BCDE FGHIJKL£MNO”PRSåTUWYZèØ";
    private static final String TOGGLE_BUTTONS = "toggleButtons";
    ToggleGroup toggleGroup = new ToggleGroup ();
    RiverDBActions riverDBActions = new RiverDBActions ();
    GaugeDBActions gaugeDBActions = new GaugeDBActions ();
    @FXML
    private FlowPane buttonContainer;
    @FXML
    private JFXListView<String> townListView;
    @FXML
    private Label gaugeQuantity;
    @FXML
    private JFXListView<River> riverListView;

    @FXML
    void initialize () {
        createButtons ();
        addRiverListViewListener ();
        addTownListViewListener ();
    }

    private void createButtons () {
        final char[] input = ALPHABET.toCharArray ();
        for (char c : input) {
            ToggleButton button = new ToggleButton (String.valueOf (c));
            button.getStyleClass ().add (TOGGLE_BUTTONS);
            addToggleButtonListener ();
            setListViewValuesOnClickedButton (button);
            toggleGroup.getToggles ().add (button);
            buttonContainer.getChildren ().add (button);
        }
    }

    private void setListViewValuesOnClickedButton (ToggleButton button) {
        button.setOnMouseClicked (event -> {
            ObservableList<River> items = FXCollections.observableArrayList
                    (riverDBActions.queryForOneRiverStartedWithLetter (button.getText ()));
            riverListView.setItems (items);
            gaugeQuantity.setText (String.valueOf (items.size ()));
        });
    }

    private void addTownListViewListener () {
        townListView.getSelectionModel ().selectedItemProperty ().addListener ((observable, oldValue, newValue) ->
                                                                                       getInstance ().setTownName (newValue));
    }

    private void addRiverListViewListener () {
        riverListView.getSelectionModel ().selectedItemProperty ().addListener ((observable, oldValue, newValue) -> {
            getInstance ().setTownName ("BRAK");
            if (newValue != null) {
                ObservableList<String> items =
                        FXCollections.observableArrayList (new HashSet<> (gaugeDBActions.getTownListOfSelectedRiver (newValue.getRiverName ())));
                items.sort (Comparator.naturalOrder ());
                townListView.setItems (items);
                getInstance ().setRiverName (String.valueOf (newValue));
            } else {
                ObservableList<String> emptyList = new SimpleListProperty<> ();
                townListView.setItems (emptyList);
            }
        });
    }

    private void addToggleButtonListener () {
        toggleGroup.selectedToggleProperty ().addListener ((observable, oldValue, newValue) -> {
            if (newValue == null) {
                toggleGroup.selectToggle (oldValue);
            }
        });
    }
}

package Others.Multihreading;

import DButils.TableDBActions.GaugeDBActions;
import Others.Mediator.ControllerHolder;
import Others.Utils.ListsUtils;
import javafx.concurrent.Task;

import java.util.List;

public class DataFiller extends Task<Void> {
    public static DataFiller myTask;
    private GaugeDBActions gaugeDBActions = new GaugeDBActions ();
    public static List<String> results;

    @Override
    public Void call () {
        myTask = this;
        ListsUtils listsUtils =
                new ListsUtils (gaugeDBActions.getCorrectedDoubleMeasurementsList (ControllerHolder.getInstance ().getTownName ()));

        return null;
    }
}

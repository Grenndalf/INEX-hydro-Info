package Others.Utils;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class TableViewData {

    private final SimpleIntegerProperty numValue;
    private final SimpleDoubleProperty maxFlowValue;
    private final SimpleDoubleProperty probability;
    private final SimpleDoubleProperty logValueOfNumValue;


    public int getNumValue () {
        return numValue.get ();
    }

    public SimpleIntegerProperty numValueProperty () {
        return numValue;
    }

    public double getMaxFlowValue () {
        return maxFlowValue.get ();
    }

    public SimpleDoubleProperty maxFlowValueProperty () {
        return maxFlowValue;
    }

    public double getProbability () {
        return probability.get ();
    }

    public void setProbability (double probability) {
        this.probability.set (probability);
    }

    public SimpleDoubleProperty probabilityProperty () {
        return probability;
    }

    public double getLogValueOfNumValue () {
        return logValueOfNumValue.get ();
    }

    public SimpleDoubleProperty logValueOfNumValueProperty () {
        return logValueOfNumValue;
    }
}

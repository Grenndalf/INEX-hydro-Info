package DButils.Intefaces;

import DButils.Tables.GaugeMeasurement;

import java.util.HashMap;
import java.util.List;

public interface GaugeQueries {

    void createOrUpdateMeasurementTable(List<GaugeMeasurement> gaugeMeasurement);

    HashMap<Short, Double> getMaxValuesPerYear (String riverName, String townName);

    int removeAllMeasurements();

    List<GaugeMeasurement> queryForDataOfSelectedTownAndRiver (String selectedRiver, String selectedTown);

    List<String> getTownListOfSelectedRiver(String riverName);

    List<Double> getCorrectedDoubleMeasurementsList (String riverName, String townName);

    List<Object[]> getSortedValuesPerYearAndPerDay (String riverName, String townName);
}


package DButils.Intefaces;

import DButils.Tables.GaugeMeasurement;

import java.util.List;

public interface GaugeQueries {
    void createOrUpdateMeasurementTable(GaugeMeasurement gaugeMeasurement);

    void createOrUpdateMeasurementTable(List<GaugeMeasurement> gaugeMeasurement);

    List<GaugeMeasurement> queryForAllGaugeMeasurements();

    int removeAllMeasurements();

    List<GaugeMeasurement> queryForDataOfSelectedTownAndRiver (String selectedRiver, String selectedTown);

    List<String> getTownListOfSelectedRiver(String riverName);

//    List<Short> getYearRange(String riverName);
}

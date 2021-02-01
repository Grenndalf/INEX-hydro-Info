package DButils.Intefaces;

import DButils.Tables.GaugeMeasurement;

import java.util.List;

public interface GaugeQueries {
    void createOrUpdateMeasurementTable(GaugeMeasurement gaugeMeasurement);

    void createOrUpdateMeasurementTable(List<GaugeMeasurement> gaugeMeasurement);

    List<GaugeMeasurement> queryForDataOfSelectedTown(String selectedTown);

    List<GaugeMeasurement> queryForAllGaugeMeasurements();

}

package DButils.Intefaces;

import RegularClasses.Tables.GaugeMeasurement;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;

public interface GaugeQueries {
    void createOrUpdateMeasurementTable(GaugeMeasurement gaugeMeasurement);

    void createOrUpdateMeasurementTable(List<GaugeMeasurement> gaugeMeasurement);

    public List<GaugeMeasurement> queryForDataOfSelectedTown(String selectedTown);

    List<GaugeMeasurement> queryForAllGaugeMeasurements();

    Dao<GaugeMeasurement, Integer> getDaoGaugeMeasurement();

    QueryBuilder<GaugeMeasurement, Integer> getQueryBuilderGaugeMeasurement();
}

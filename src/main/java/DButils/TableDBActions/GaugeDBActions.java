package DButils.TableDBActions;

import DButils.Intefaces.GaugeQueries;
import DButils.Tables.GaugeMeasurement;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GaugeDBActions extends DbActions implements GaugeQueries {
    private final Dao<GaugeMeasurement, Integer> dao = getDaoGaugeMeasurement();

    @Override
    public void createOrUpdateMeasurementTable(GaugeMeasurement gaugeMeasurement) {
        try {
            dao.createOrUpdate(gaugeMeasurement);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        }
    }

    @Override
    public void createOrUpdateMeasurementTable(List<GaugeMeasurement> gaugeMeasurement) {
        try {
            dao.create(gaugeMeasurement);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
    }

    @Override
    public List<GaugeMeasurement> queryForDataOfSelectedTown(String selectedTown) {
        try {
           return dao.queryForEq("Nazwa_wodowskazu",selectedTown);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return new ArrayList<>();
    }

    @Override
    public List<GaugeMeasurement> queryForAllGaugeMeasurements() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return new ArrayList<>();
    }

    @Override
    public Dao<GaugeMeasurement, Integer> getDaoGaugeMeasurement() {
        try {
            return DaoManager.createDao(connectionSource, GaugeMeasurement.class);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return null;
    }

    @Override
    public QueryBuilder<GaugeMeasurement, Integer> getQueryBuilderGaugeMeasurement() {
        Dao<GaugeMeasurement, Integer> dao = getDaoGaugeMeasurement();
        return dao.queryBuilder();
    }

    @Override
    public List<GaugeMeasurement> getGaugeNameListForRiver(String river) {
        try {
            dao.queryForEq("Nazwa_rzeki", river);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return new ArrayList<>();
    }
}

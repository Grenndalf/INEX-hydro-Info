package DButils;

import RegularClasses.GaugeMeasurement;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbActions {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbActions.class);
    protected final ConnectionSource connectionSource;

    public DbActions() {
        this.connectionSource = DbManager.getConnectionSource();
    }

    public void createOrUpdate(GaugeMeasurement gaugeMeasurement) {
        Dao<GaugeMeasurement, Integer> dao = getDao(GaugeMeasurement.class);
        try {
            dao.createOrUpdate(gaugeMeasurement);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
    }

    public synchronized void createOrUpdate(List<GaugeMeasurement> gaugeMeasurement) {
        Dao<GaugeMeasurement, Integer> dao = getDao(GaugeMeasurement.class);
        try {
            dao.create(gaugeMeasurement);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
    }

    public void refresh(GaugeMeasurement gaugeMeasurement) {
        try {
            Dao<GaugeMeasurement, Integer> dao = getDao(GaugeMeasurement.class);
            dao.refresh(gaugeMeasurement);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
    }

    public void delete(GaugeMeasurement gaugeMeasurement) {
        try {
            Dao<GaugeMeasurement, Integer> dao = getDao(GaugeMeasurement.class);
            dao.delete(gaugeMeasurement);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
    }

    public List<GaugeMeasurement> queryForAll(Class<GaugeMeasurement> cls) {
        try {
            Dao<GaugeMeasurement, Integer> dao = getDao(GaugeMeasurement.class);
            return dao.queryForAll();
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return new ArrayList<>();
    }


    public Dao<GaugeMeasurement, Integer> getDao(Class<GaugeMeasurement> cls) {
        Dao<GaugeMeasurement, Integer> gaugeMeasurementDao = null;
        try {
            return DaoManager.createDao(connectionSource, cls);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return null;
    }

    public QueryBuilder<GaugeMeasurement, Integer> getQueryBuilder(Class<GaugeMeasurement> cls) {
        Dao<GaugeMeasurement, Integer> dao = getDao(GaugeMeasurement.class);
        return dao.queryBuilder();
    }

    private void closeDbConnection() {
        try {
            this.connectionSource.close();
        } catch (IOException e) {
            LOGGER.warn(e.getCause().getMessage());
        }
    }


}

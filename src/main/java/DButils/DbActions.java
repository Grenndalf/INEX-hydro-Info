package DButils;

import RegularClasses.Tables.GaugeMeasurement;
import RegularClasses.Tables.TownList;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DbActions {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbActions.class);
    protected final ConnectionSource connectionSource;

    public DbActions() {
        this.connectionSource = DbManager.getConnectionSource();
    }

    public void createOrUpdateMeasurementTable(GaugeMeasurement gaugeMeasurement) {
        Dao<GaugeMeasurement, Integer> dao = getDaoGaugeMeasurement(GaugeMeasurement.class);
        try {
            dao.createOrUpdate(gaugeMeasurement);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
    }

    public synchronized void createOrUpdateMeasurementTable(List<GaugeMeasurement> gaugeMeasurement) {
        Dao<GaugeMeasurement, Integer> dao = getDaoGaugeMeasurement(GaugeMeasurement.class);
        try {
            dao.create(gaugeMeasurement);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
    }

    public synchronized void createOrUpdateTownListTable(Set<String> townList) {
        Dao<TownList, String> dao = getDaoTownList(TownList.class);
        try {
            dao.create(townList.stream().map(s -> {
                TownList tl = new TownList();
                tl.setTownName(s);
                return tl;
            }).collect(Collectors.toSet()));
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
    }

    public List<GaugeMeasurement> queryForAll(Class<GaugeMeasurement> cls) {
        try {
            Dao<GaugeMeasurement, Integer> dao = getDaoGaugeMeasurement(GaugeMeasurement.class);
            return dao.queryForAll();
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return new ArrayList<>();
    }
    public List<GaugeMeasurement> queryForDataOfSelectedTown(String selectedTown) {
        try {
            return getQueryBuilderGaugeMeasurement().where().eq("Nazwa_wodowskazu",selectedTown).query();
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return new ArrayList<>();
    }

    public List<TownList> queryForOneTownStartedWithLetter(String townNameFirstLetter) {
        try {
            String letter = townNameFirstLetter + "%";
            return getQueryBuilderTownList().where().like("Wodowskaz", letter).query();
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return new ArrayList<>();
    }

    public Set<String> queryForAllTowns() {
        try {
            return getDaoTownList(TownList.class).queryForAll().stream().map(townList -> townList.getTownName()).collect(Collectors.toSet());
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return new HashSet<>();
    }


    public Dao<GaugeMeasurement, Integer> getDaoGaugeMeasurement(Class<GaugeMeasurement> cls) {
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

    public Dao<TownList, String> getDaoTownList(Class<TownList> cls) {
        Dao<TownList, String> townListDao = null;
        try {
            return DaoManager.createDao(connectionSource, cls);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return null;
    }

    public QueryBuilder<GaugeMeasurement, Integer> getQueryBuilderGaugeMeasurement() {
        Dao<GaugeMeasurement, Integer> dao = getDaoGaugeMeasurement(GaugeMeasurement.class);
        return dao.queryBuilder();
    }

    public QueryBuilder<TownList, String> getQueryBuilderTownList() {
        Dao<TownList, String> dao = getDaoTownList(TownList.class);
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

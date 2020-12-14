package DButils.TableDBActions;

import DButils.Intefaces.RiverQueries;
import RegularClasses.Tables.River;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RiverDBActions extends DbActions implements RiverQueries {
    private final Dao <River,Integer> dao = getDaoRiverList();

    @Override
    public Dao<River, Integer> getDaoRiverList() {
        try {
            return DaoManager.createDao(connectionSource, River.class);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return null;
    }

    @Override
    public Set<String> queryForAllTownNames() {
        try {
            return dao.queryForAll().stream().map(River::getRiverName).collect(Collectors.toSet());
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return new HashSet<>();
    }

    @Override
    public List<River> queryForOneRiverStartedWithLetter(String riverNameFirstLetter) {
        try {
            String letter = riverNameFirstLetter + "%";
            return getQueryBuilderRiver().where().like("Nazwa_rzeki", letter).query();
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return new ArrayList<>();
    }

    @Override
    public QueryBuilder<River, Integer> getQueryBuilderRiver() {
        Dao<River, Integer> dao = getDaoRiverList();
        return dao.queryBuilder();
    }

    @Override
    public void createOrUpdateRiverTable(Set<String> riverList) {
        try {
            dao.create(riverList.stream().map(s -> {
                River river = new River();
                river.setRiverName(s);
                return river;
            }).collect(Collectors.toList()));
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
    }

    @Override
    public List<River> getAllRivers() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return new ArrayList<>();
    }
}

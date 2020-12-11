package DButils.TableDBActions;

import DButils.Intefaces.TownQueries;
import RegularClasses.Tables.Town;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TownDBActions extends DbActions implements TownQueries {
    private Dao<Town, Integer> dao = getDaoTownList();

    @Override
    public void createOrUpdateTownListTable(Set<String> townList) {
        try {
            dao.create(townList.stream().map(s -> {
                Town tl = new Town();
                tl.setTownName(s);
                return tl;
            }).collect(Collectors.toList()));
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
    }

    @Override
    public List<Town> queryForOneTownStartedWithLetter(String townNameFirstLetter) {
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

    @Override
    public Set<String> queryForAllTowns() {
        try {
            return getDaoTownList().queryForAll().stream().map(Town::getTownName).collect(Collectors.toSet());
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return new HashSet<>();
    }

    @Override
    public Dao<Town, Integer> getDaoTownList() {
        try {
            return DaoManager.createDao(connectionSource, Town.class);
        } catch (SQLException e) {
            LOGGER.warn(e.getCause().getMessage());
        } finally {
            this.closeDbConnection();
        }
        return null;
    }

    @Override
    public QueryBuilder<Town, Integer> getQueryBuilderTownList() {
        Dao<Town, Integer> dao = getDaoTownList();
        return dao.queryBuilder();
    }
}

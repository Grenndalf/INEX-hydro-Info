package DButils.Intefaces;


import DButils.Tables.Town;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;
import java.util.Set;

public interface TownQueries {

    void createOrUpdateTownListTable(Set<String> townList);

    List<Town> queryForTownsStartedWithLetter(String townNameFirstLetter);

    Set<String> queryForAllTownNames();

    Dao<Town, Integer> getDaoTownList();

    QueryBuilder<Town, Integer> getQueryBuilderTownList();

    Town getSingleTown(String townName);
    List<Town> queryForAllTowns();
}

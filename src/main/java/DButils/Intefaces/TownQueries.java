package DButils.Intefaces;


import RegularClasses.Tables.Town;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;
import java.util.Set;

public interface TownQueries {
    void createOrUpdateTownListTable(Set<String> townList);

    List<Town> queryForOneTownStartedWithLetter(String townNameFirstLetter);

    Set<String> queryForAllTowns();

    Dao<Town, Integer> getDaoTownList();

    public QueryBuilder<Town, Integer> getQueryBuilderTownList();
}

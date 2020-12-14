package DButils.Intefaces;

import RegularClasses.Tables.River;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;
import java.util.Set;

public interface RiverQueries {
    Dao<River, Integer> getDaoRiverList();

    Set<String> queryForAllTownNames();
    List<River> queryForOneRiverStartedWithLetter(String riverNameFirstLetter);

    QueryBuilder<River, Integer> getQueryBuilderRiver();
    void createOrUpdateRiverTable(Set<String> riverList);
    List<River> getAllRivers();
}

package DButils.Intefaces;

import DButils.Tables.River;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;
import java.util.Set;

public interface RiverQueries {


    Set<String> queryForAllRiverNames();

    List<River> queryForOneRiverStartedWithLetter(String riverNameFirstLetter);

    void createOrUpdateRiverTable(Set<String> riverList);

    List<River> getAllRivers();
}

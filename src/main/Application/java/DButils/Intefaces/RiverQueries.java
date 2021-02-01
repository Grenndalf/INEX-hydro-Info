package DButils.Intefaces;

import DButils.Tables.River;

import java.util.List;
import java.util.Set;

public interface RiverQueries {


    List<String> queryForAllRiverNames();

    List<River> queryForOneRiverStartedWithLetter(String riverNameFirstLetter);

    void createOrUpdateRiverTable(Set<String> riverList);

    List<River> getAllRivers();
}

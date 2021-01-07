package DButils.Intefaces;


import DButils.Tables.Town;

import java.util.List;
import java.util.Set;

public interface TownQueries {

    void createOrUpdateTownListTable(Set<String> townList);

    List<Town> queryForTownsStartedWithLetter(String townNameFirstLetter);

    List<Town> queryForAllTownNames();

    Town getSingleTown(String townName);

    List<Town> getAllTowns();
}

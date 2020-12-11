package DButils.Intefaces;

import RegularClasses.Tables.River;
import com.j256.ormlite.dao.Dao;

public interface RiverQueries {
    Dao<River, Integer> getDaoRiverList();
}

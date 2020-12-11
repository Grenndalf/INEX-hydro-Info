package DButils.TableDBActions;

import DButils.Intefaces.RiverQueries;
import RegularClasses.Tables.River;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;

public class RiverDBActions extends DbActions implements RiverQueries {
    Dao <River,Integer> dao = getDaoRiverList();

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
}

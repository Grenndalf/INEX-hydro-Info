package DButils.TableDBActions;

import DButils.DbManager;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;

public class DbActions {

    static final Logger LOGGER = LoggerFactory.getLogger(DbActions.class);
    protected final ConnectionSource connectionSource;

    public DbActions() {
        this.connectionSource = DbManager.getConnectionSource();
    }

    void closeDbConnection() {
        try {
            this.connectionSource.close();
        } catch (IOException e) {
            LOGGER.warn(e.getCause().getMessage());
        }
    }


}

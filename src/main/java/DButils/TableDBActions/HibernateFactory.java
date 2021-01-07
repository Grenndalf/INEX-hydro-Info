package DButils.TableDBActions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HibernateFactory {

    private static final Logger logger = LogManager.getLogger();
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("gaugePersistenceUnit");


    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}

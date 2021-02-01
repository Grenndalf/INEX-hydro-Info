package DButils.TableDBActions;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HibernateFactory {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("gaugePersistenceUnit");
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}

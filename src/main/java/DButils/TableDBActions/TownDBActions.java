package DButils.TableDBActions;

import DButils.Intefaces.TownQueries;
import DButils.Tables.Town;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TownDBActions implements TownQueries {

    private final EntityManagerFactory emf = HibernateFactory.getEntityManagerFactory();
    private final BasicCrudOperator<Town> crudOperator = new BasicCrudOperator<>();

    @Override
    public void createOrUpdateTownListTable(Set<String> townList) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (String s : townList) {
                Town town = new Town();
                town.setTownName(s);
                em.persist(town);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            if (em.getTransaction().isActive()) em.close();
        }
    }

    @Override
    public List<Town> queryForTownsStartedWithLetter(String townNameFirstLetter) {
        return null;
    }

    @Override
    public List<String> queryForAllTownNames() {
        return crudOperator.getAll(Town.class).stream().map(Town::getTownName).collect(Collectors.toList());
    }

    @Override
    public Town getSingleTown(String townName) {
        return null;
    }

    @Override
    public List<Town> getAllTowns() {
        return crudOperator.getAll(Town.class);
    }
}
package DButils.TableDBActions;

import DButils.Intefaces.TownQueries;
import DButils.Tables.Town;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TownDBActions implements TownQueries {

    private final EntityManager em = HibernateFactory.getEntityManagerFactory().createEntityManager();
    private BasicCrudOperator<Town> crudOperator = new BasicCrudOperator<>();

    @Override
    public void createOrUpdateTownListTable(Set<String> townList) {
        try {
            em.getTransaction().begin();
            List<String> savedTowns = em.createQuery("SELECT st FROM Town st", Town.class)
                    .getResultList().stream().map(Town::getTownName).collect(Collectors.toList());
            System.out.println("dupa" + savedTowns.size());
            townList.removeAll(savedTowns);
            List<String> townListToSave = new ArrayList<>(townList);
            for (int i = 0; i < townListToSave.size(); i++) {
                Town town = new Town();
                town.setTownName(townListToSave.get(i));
                em.persist(town);
                if (i % 50 == 0) {
                    em.flush();
                    em.clear();
                }
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
    public List<Town> queryForAllTownNames() {
        return crudOperator.getAll(Town.class);
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
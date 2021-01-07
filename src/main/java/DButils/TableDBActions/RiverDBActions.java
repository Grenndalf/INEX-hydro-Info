package DButils.TableDBActions;

import DButils.Intefaces.RiverQueries;
import DButils.Tables.River;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RiverDBActions implements RiverQueries {

    private final EntityManager em = HibernateFactory.getEntityManagerFactory().createEntityManager();

    @Override
    public Set<String> queryForAllRiverNames() {
        try {
            em.getTransaction();
            Set<String> listOfRiverNames = em.createQuery("SELECT r FROM River r", River.class).getResultList()
                    .stream().map(River::getRiverName).collect(Collectors.toSet());
            em.getTransaction().commit();
            return listOfRiverNames;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (em.getTransaction().isActive()) em.close();
        }
        return new HashSet<>();
    }

    @Override
    public List<River> queryForOneRiverStartedWithLetter(String riverNameFirstLetter) {
        String letter = "'" + riverNameFirstLetter + "%'";
        try {
            em.getTransaction().begin();
            List<River> result = em.createQuery("Select r from River r where r.riverName Like " + letter).getResultList();
            System.out.println(result.size());
            em.getTransaction().commit();
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (em.getTransaction().isActive()) em.close();
        }
        return new ArrayList<>();
    }

    @Override
    public void createOrUpdateRiverTable(Set<String> riverList) {
        try {
            em.getTransaction().begin();
            List<String> savedRivers = em.createQuery("SELECT sr FROM River sr", River.class)
                    .getResultList().stream().map(River::getRiverName).collect(Collectors.toList());
            System.out.println("dupa" + savedRivers.size());
            riverList.removeAll(savedRivers);
            List<String> riverToSave = new ArrayList<>(riverList);
            for (int i = 0; i < riverToSave.size(); i++) {
                River river = new River();
                river.setRiverName(riverToSave.get(i));
                em.persist(river);
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
    public List<River> getAllRivers() {
        return null;
    }
}

package DButils.TableDBActions;

import DButils.Intefaces.RiverQueries;
import DButils.Tables.River;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RiverDBActions implements RiverQueries {

    private final EntityManagerFactory emf = HibernateFactory.getEntityManagerFactory();
    private final BasicCrudOperator<River> crudOperator = new BasicCrudOperator<>();

    @Override
    public List<String> queryForAllRiverNames() {
        return crudOperator.getAll(River.class).stream().map(River::getRiverName).collect(Collectors.toList());
    }

    @Override
    public List<River> queryForOneRiverStartedWithLetter(String riverNameFirstLetter) {
        String letter = riverNameFirstLetter + "%";
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<River> criteriaQuery = criteriaBuilder.createQuery(River.class);
            Root<River> riverRootTable = criteriaQuery.from(River.class);
            criteriaQuery.select(riverRootTable).where(criteriaBuilder.like(riverRootTable.get("riverName"), letter));
            TypedQuery<River> query = em.createQuery(criteriaQuery);
            List<River> result = query.getResultList();
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
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (String s : riverList) {
                River river = new River();
                river.setRiverName(s);
                em.persist(river);
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

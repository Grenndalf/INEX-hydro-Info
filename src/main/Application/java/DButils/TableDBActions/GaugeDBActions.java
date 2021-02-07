package DButils.TableDBActions;

import DButils.Intefaces.GaugeQueries;
import DButils.Tables.GaugeMeasurement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class GaugeDBActions implements GaugeQueries {

    private final BasicCrudOperator<GaugeMeasurement> crudOperator = new BasicCrudOperator<> ();
    private final Logger logger = LogManager.getLogger (GaugeDBActions.class);
    private final EntityManagerFactory emf = HibernateFactory.getEntityManagerFactory ();

    @Override
    public void createOrUpdateMeasurementTable (GaugeMeasurement gaugeMeasurement) {
        crudOperator.save (gaugeMeasurement);
    }

    @Override
    public void createOrUpdateMeasurementTable (List<GaugeMeasurement> gaugeMeasurements) {
        crudOperator.saveMultiple (gaugeMeasurements);
    }

    @Override
    public List<GaugeMeasurement> queryForDataOfSelectedTown (String selectedTown) {
        EntityManager em = emf.createEntityManager ();
        try {
            em.getTransaction ().begin ();
            List<GaugeMeasurement> result =
                    em.createQuery ("SELECT A FROM GaugeMeasurement A WHERE A.gaugeName = '" + selectedTown + "'",
                                    GaugeMeasurement.class).getResultList ();
            em.getTransaction ().commit ();
            return result;
        } catch (Exception ex) {
            em.getTransaction ().rollback ();
            logger.info (ex.getMessage ());
        } finally {
            if (em.getTransaction ().isActive ()) em.close ();
        }
        return new ArrayList<> ();
    }

    @Override
    public List<String> getTownListOfSelectedRiver (String riverName) {
        EntityManager em = emf.createEntityManager ();
        try {
            em.getTransaction ().begin ();
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder ();
            CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery (String.class);
            Root<GaugeMeasurement> gaugeMeasurementRoot = criteriaQuery.from (GaugeMeasurement.class);
            criteriaQuery.select (gaugeMeasurementRoot.get ("gaugeName"))
                    .where (criteriaBuilder.equal (gaugeMeasurementRoot.get ("riverName"), riverName));
            TypedQuery<String> query = em.createQuery (criteriaQuery);
            List<String> result = query.getResultList ();
            em.getTransaction ().commit ();
            return result;
        } catch (Exception ex) {
            em.getTransaction ().rollback ();
            logger.info (ex.getMessage ());
        } finally {
            if (em.getTransaction ().isActive ()) {
                em.flush ();
                em.clear ();
                em.close ();
            }
        }
        return new ArrayList<> ();
    }


    @Override
    public int removeAllMeasurements () {
        EntityManager em = emf.createEntityManager ();
        try {
            em.getTransaction ().begin ();
            String stringQuery = "DELETE FROM GaugeMeasurement";
            Query query = em.createQuery (stringQuery);
            final int i = query.executeUpdate ();
            em.getTransaction ().commit ();
            return i;
        } catch (Exception ex) {
            em.getTransaction ().rollback ();
            logger.info (ex.getMessage ());
        } finally {
            if (em.getTransaction ().isActive ()) {
                em.flush ();
                em.clear ();
                em.close ();
            }
        }
        return 0;
    }


    @Override
    public List<GaugeMeasurement> queryForAllGaugeMeasurements () {
        return crudOperator.getAll (GaugeMeasurement.class);
    }
}

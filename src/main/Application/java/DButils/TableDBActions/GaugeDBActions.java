package DButils.TableDBActions;

import DButils.Intefaces.GaugeQueries;
import DButils.Tables.GaugeMeasurement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GaugeDBActions implements GaugeQueries {

    private Logger logger = LogManager.getLogger(GaugeDBActions.class);

    private EntityManagerFactory emf = HibernateFactory.getEntityManagerFactory();

    private final BasicCrudOperator<GaugeMeasurement> crudOperator = new BasicCrudOperator<>();

    @Override
    public void createOrUpdateMeasurementTable(GaugeMeasurement gaugeMeasurement) {
        crudOperator.save(gaugeMeasurement);
    }

    @Override
    public void createOrUpdateMeasurementTable(List<GaugeMeasurement> gaugeMeasurements) {
        crudOperator.saveMultiple(gaugeMeasurements);
    }

    @Override
    public List<GaugeMeasurement> queryForDataOfSelectedTown(String selectedTown) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            List<GaugeMeasurement> result = em.createQuery("SELECT A FROM GaugeMeasurement A WHERE A.gaugeName = '" + selectedTown + "'", GaugeMeasurement.class).getResultList();
            em.getTransaction().commit();
            return result;
        } catch (Exception ex) {
            em.getTransaction().rollback();
            logger.info(ex.getMessage());
        } finally {
            if (em.getTransaction().isActive()) em.close();
        }
        return new ArrayList<>();
    }

    public Set<String> getTownListOfSelectedRiver(String riverName) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Set<String> result = new HashSet<>(em.createQuery("SELECT A.gaugeName FROM GaugeMeasurement A WHERE A.riverName = '" + riverName + "'", String.class).getResultList());
            em.getTransaction().commit();
            return result;
        } catch (Exception ex) {
            em.getTransaction().rollback();
            logger.info(ex.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.flush();
                em.clear();
                em.close();
            }
        }
        return new HashSet<>();
    }

    public Set<Short> getYearListOfSelectedTown(String townName) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Set<Short> result = new HashSet<>(em.createQuery("SELECT A.measurementYear FROM GaugeMeasurement A  Where A.townName = '" + townName + "' ORDER BY A.measurementYear ASC", Short.class).getResultList());
            em.getTransaction().commit();
            return result;
        } catch (Exception ex) {
            em.getTransaction().rollback();
            logger.info(ex.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.flush();
                em.clear();
                em.close();
            }
        }
        return new HashSet<>();
    }

    public List<Double> getDataOfOneYearOfSelectedTown(String townName, Short year) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            List<Double> result = em.createQuery("SELECT g.data2 FROM GaugeMeasurement g Where g.townName ='" + townName + "' AND g.measurementYear = '" + year + "' ORDER BY g.measurementYear, g.measurementMonth, g.measurementDay ASC ", Double.class).getResultList();
            em.getTransaction().commit();
            return result;
        } catch (Exception ex) {
            em.getTransaction().rollback();
            logger.info(ex.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.flush();
                em.clear();
                em.close();
            }
        }
        return new ArrayList<>();
    }

    public int removeAllMeasurements() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            String stringQuery = "DELETE FROM GaugeMeasurement";
            Query query = em.createQuery(stringQuery);
            final int i = query.executeUpdate();
            em.getTransaction().commit();
            return i;
        } catch (Exception ex) {
            em.getTransaction().rollback();
            logger.info(ex.getMessage());
        } finally {
            if (em.getTransaction().isActive()) {
                em.flush();
                em.clear();
                em.close();
            }
        }
        return 0;
    }


    @Override
    public List<GaugeMeasurement> queryForAllGaugeMeasurements() {
        return crudOperator.getAll(GaugeMeasurement.class);
    }
}

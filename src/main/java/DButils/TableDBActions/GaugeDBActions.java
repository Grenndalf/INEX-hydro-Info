package DButils.TableDBActions;

import DButils.Intefaces.GaugeQueries;
import DButils.Tables.GaugeMeasurement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GaugeDBActions implements GaugeQueries {

    private Logger logger = LogManager.getLogger(GaugeDBActions.class);

    private EntityManager em = HibernateFactory.getEntityManagerFactory().createEntityManager();

    private BasicCrudOperator<GaugeMeasurement> crudOperator = new BasicCrudOperator<>();

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
        try {
            em.getTransaction().begin();
            Set<String> result = new HashSet<>(em.createQuery("SELECT A.gaugeName FROM GaugeMeasurement A WHERE A.riverName = '" + riverName + "'", String.class).getResultList());
            em.getTransaction().commit();
            return result;
        } catch (Exception ex) {
            em.getTransaction().rollback();
            logger.info(ex.getMessage());
        } finally {
            if (em.getTransaction().isActive()) em.close();
        }
        return new HashSet<>();
    }


    @Override
    public List<GaugeMeasurement> queryForAllGaugeMeasurements() {
        return crudOperator.getAll(GaugeMeasurement.class);
    }
}

package DButils.TableDBActions;

import DButils.Intefaces.GaugeQueries;
import DButils.Tables.GaugeMeasurement;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GaugeDBActions implements GaugeQueries {

    private final BasicCrudOperator<GaugeMeasurement> crudOperator = new BasicCrudOperator<> ();
    private final EntityManagerFactory emf = HibernateFactory.getEntityManagerFactory ();

    @Override
    public void createOrUpdateMeasurementTable (List<GaugeMeasurement> gaugeMeasurements) {
        crudOperator.saveMultiple (gaugeMeasurements);
    }

    @Override
    public List<GaugeMeasurement> queryForDataOfSelectedTownAndRiver (String selectedRiver, String selectedTown) {
        EntityManager em = emf.createEntityManager ();
        try {
            em.getTransaction ().begin ();
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder ();
            CriteriaQuery<GaugeMeasurement> criteriaQuery = criteriaBuilder.createQuery (GaugeMeasurement.class);
            Root<GaugeMeasurement> gaugeMeasurementRoot = criteriaQuery.from (GaugeMeasurement.class);
            criteriaQuery.select (gaugeMeasurementRoot)
                    .where (criteriaBuilder.equal (gaugeMeasurementRoot.get ("riverName"), selectedRiver),
                            (criteriaBuilder.equal (gaugeMeasurementRoot.get ("gaugeName"), selectedTown)));
            TypedQuery<GaugeMeasurement> query = em.createQuery (criteriaQuery);
            List<GaugeMeasurement> result = query.getResultList ();
            em.getTransaction ().commit ();
            return result;
        } catch (Exception ex) {
            em.getTransaction ().rollback ();
            ex.printStackTrace ();
            showDialog (ex);
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
                    .where (criteriaBuilder.equal (gaugeMeasurementRoot.get ("riverName"), riverName))
                    .orderBy (criteriaBuilder.asc (gaugeMeasurementRoot.get ("gaugeName")));
            TypedQuery<String> query = em.createQuery (criteriaQuery);
            List<String> result = query.getResultList ();
            em.getTransaction ().commit ();
            return result;
        } catch (Exception ex) {
            em.getTransaction ().rollback ();
            ex.printStackTrace ();
            showDialog (ex);
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
    public List<Double> getCorrectedDoubleMeasurementsList (String riverName, String townName) {
        EntityManager em = emf.createEntityManager ();
        try {
            em.getTransaction ().begin ();
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder ();
            CriteriaQuery<Double> criteriaQuery = criteriaBuilder.createQuery (Double.class);
            Root<GaugeMeasurement> gaugeMeasurementRoot = criteriaQuery.from (GaugeMeasurement.class);
            criteriaQuery.select (criteriaBuilder.max (gaugeMeasurementRoot.get ("data2")))
                    .where (criteriaBuilder.equal (gaugeMeasurementRoot.get ("riverName"), riverName),
                            criteriaBuilder.equal (gaugeMeasurementRoot.get ("gaugeName"), townName),
                            criteriaBuilder.lt (gaugeMeasurementRoot.get ("data2"), 9999))
                    .groupBy (gaugeMeasurementRoot.get ("measurementYear"))
                    .orderBy (criteriaBuilder.desc (gaugeMeasurementRoot.get ("data2")));
            TypedQuery<Double> query = em.createQuery (criteriaQuery);
            List<Double> result = query.getResultList ();
            em.getTransaction ().commit ();
            return result;
        } catch (Exception ex) {
            em.getTransaction ().rollback ();
            ex.printStackTrace ();
            showDialog (ex);
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
    public HashMap<Short, Double> getMaxValuesPerYear (String riverName, String townName) {
        EntityManager em = emf.createEntityManager ();
        HashMap<Short, Double> resultMap = new HashMap<> ();
        try {
            em.getTransaction ().begin ();
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder ();
            CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery (Object[].class);
            Root<GaugeMeasurement> gaugeMeasurementRoot = criteriaQuery.from (GaugeMeasurement.class);
            criteriaQuery.multiselect (gaugeMeasurementRoot.get ("measurementYear"),
                                       criteriaBuilder.max (gaugeMeasurementRoot.get ("data2")))
                    .where (criteriaBuilder.equal (gaugeMeasurementRoot.get ("gaugeName"), riverName),
                            criteriaBuilder.equal (gaugeMeasurementRoot.get ("riverName"), townName),
                            criteriaBuilder.lt (gaugeMeasurementRoot.get ("data2"), 9999))
                    .groupBy (gaugeMeasurementRoot.get ("measurementYear"))
                    .orderBy (criteriaBuilder.asc (gaugeMeasurementRoot.get ("measurementYear")));
            TypedQuery<Object[]> query = em.createQuery (criteriaQuery);
            List<Object[]> result = query.getResultList ();
            em.getTransaction ().commit ();
            for (Object[] row : result) {
                resultMap.put ((short) row[0], (double) row[1]);
            }
            return resultMap;
        } catch (Exception ex) {
            em.getTransaction ().rollback ();
            ex.printStackTrace ();
            showDialog (ex);
        } finally {
            if (em.getTransaction ().isActive ()) {
                em.flush ();
                em.clear ();
                em.close ();
            }
        }
        return new HashMap<> ();
    }

    @Override
    public List<Object[]> getSortedValuesPerYearAndPerDay (String riverName, String townName) {
        EntityManager em = emf.createEntityManager ();
        try {
            em.getTransaction ().begin ();
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder ();
            CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery (Object[].class);
            Root<GaugeMeasurement> gaugeMeasurementRoot = criteriaQuery.from (GaugeMeasurement.class);
            criteriaQuery.multiselect (gaugeMeasurementRoot.get ("measurementYear"),
                                       gaugeMeasurementRoot.get ("data2"))
                    .where (criteriaBuilder.equal (gaugeMeasurementRoot.get ("gaugeName"), townName),
                            criteriaBuilder.equal (gaugeMeasurementRoot.get ("riverName"), riverName),
                            criteriaBuilder.lt (gaugeMeasurementRoot.get ("data2"), 9999))
                    .orderBy (criteriaBuilder.asc (gaugeMeasurementRoot.get ("measurementYear")),
                              criteriaBuilder.desc (gaugeMeasurementRoot.get ("data2")));
            TypedQuery<Object[]> query = em.createQuery (criteriaQuery);
            List<Object[]> result = query.getResultList ();
            em.getTransaction ().commit ();

            return result;
        } catch (Exception ex) {
            em.getTransaction ().rollback ();
            ex.printStackTrace ();
            showDialog (ex);
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
            ex.printStackTrace ();
            showDialog (ex);
        } finally {
            if (em.getTransaction ().isActive ()) {
                em.flush ();
                em.clear ();
                em.close ();
            }
        }
        return 0;
    }
    private static void showDialog(Exception exception){
        Dialog dialog = new Dialog ();
        dialog.setTitle ("Wyst¹pi³ b³¹d");
        dialog.setContentText (Arrays.toString (exception.getStackTrace ()));
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
        dialog.show ();
    }

}

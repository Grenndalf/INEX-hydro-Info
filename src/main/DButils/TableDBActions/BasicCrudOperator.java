package DButils.TableDBActions;



import javafx.scene.control.Dialog;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasicCrudOperator<T> {

    private final EntityManagerFactory emf = HibernateFactory.getEntityManagerFactory();

    public void save(Object T) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(T);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            showDialog (ex);
        } finally {
            if (em.getTransaction().isActive()) em.close();
        }
    }

    // do u¿ycia w przypadku tabel nie zawieraj¹cych unikalnych pól
    public void saveMultiple(List<T> list) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (int i = 0; i < list.size(); i++) {
                em.persist(list.get(i));
                if (i % 100 == 0) {
                    em.flush();
                    em.clear();
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            showDialog (ex);
        } finally {
            if (em.getTransaction().isActive()) em.close();
        }
    }

    public T getOne(Object T) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.find(T.getClass(), T);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            showDialog (ex);
        } finally {
            if (em.getTransaction().isActive()) em.close();
        }
        return null;
    }

    public List<T> getAll(Class<T> type) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
            criteriaQuery.from(type);
            List<T> resultList = em.createQuery(criteriaQuery).getResultList();
            em.getTransaction().commit();
            return resultList;
        } catch (Exception ex) {
            em.getTransaction().rollback();
            showDialog (ex);
        } finally {
            if (em.getTransaction().isActive()) em.close();
        }
        return new ArrayList<>();
    }
    private static void showDialog(Exception exception){
        Dialog dialog = new Dialog ();
        dialog.setTitle ("Wyst¹pi³ b³¹d");
        dialog.setContentText (Arrays.toString (exception.getStackTrace ()));
        dialog.show ();
    }
}

package de.db.derPate.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.eclipse.jdt.annotation.NonNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import de.db.derPate.manager.LoggingManager;
import de.db.derPate.model.DatabaseEntity;
import de.db.derPate.util.HibernateSessionFactoryUtil;

/**
 * This abstract class is used a a base class for all Data Access Objects. It
 * stores the default {@link SessionFactory} (@see
 * {@link HibernateSessionFactoryUtil#getSessionFactory()}
 *
 * @author MichelBlank
 *
 */
abstract class Dao {
	/**
	 * The default {@link SessionFactory}
	 */
	protected static final SessionFactory sessionFactory;
	/**
	 * The {@link Class} that the wanted objects are of
	 *
	 * @see de.db.derPate.model
	 */
	@NonNull
	protected final Class<? extends DatabaseEntity> cls;

	static {
		sessionFactory = HibernateSessionFactoryUtil.getSessionFactory();
	}

	/**
	 * Default constructor for dao objects
	 *
	 * @param cls {@link Class} that the future objects should be of and that the
	 *            data is stored in (in the database)
	 */
	public Dao(@NonNull Class<? extends DatabaseEntity> cls) {
		this.cls = cls;
	}

	/**
	 * Returns a {@link List} of {@link #cls}-objects, stored in the Table used for
	 * {@link #cls}-objects
	 *
	 * @param <T> type
	 * @return {@link List} of objects
	 */
	@SuppressWarnings("unchecked")
	@NonNull
	public <T extends DatabaseEntity> List<T> list() {

		Session session = sessionFactory.openSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();

		// Build query
		CriteriaQuery<T> query = (CriteriaQuery<T>) builder.createQuery(this.cls);
		Root<T> root = (Root<T>) query.from(this.cls);
		query.select(root);

		// Execute query and get ResultList
		Query<T> q = session.createQuery(query);
		List<T> result = q.getResultList();

		session.close(); // first close session, before returning result

		if (result == null) {
			result = new ArrayList<>();
		}
		return result;
	}

	/**
	 * Updates a {@link DatabaseEntity}
	 *
	 * @param entity the {@link DatabaseEntity} to update
	 * @return <code>true</code>, if update was successful; <code>false</code>, if
	 *         an error occurred
	 */
	public <T extends DatabaseEntity> boolean update(@NonNull T entity) {
		boolean success = false;
		try {
			Session session = sessionFactory.openSession();
			Transaction transaction = session.beginTransaction();
			session.update(entity);
			transaction.commit();
			session.close();
			success = transaction.getStatus() == TransactionStatus.COMMITTED;
		} catch (PersistenceException e) {
			LoggingManager.log(Level.INFO, "Error updating DatabaseEntity. Rolling back: " + e.getMessage()); //$NON-NLS-1$
		} catch (IllegalStateException e) {
			LoggingManager.log(Level.WARNING, "Error updating DatabaseEntity: " + e.getMessage()); //$NON-NLS-1$
		}
		return success;
	}
}

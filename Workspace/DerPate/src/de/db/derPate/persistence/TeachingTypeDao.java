package de.db.derPate.persistence;

import de.db.derPate.model.Job;
import de.db.derPate.model.TeachingType;

/**
 * Database object providing methods to get {@link TeachingType}s out of the
 * Database
 *
 * @author MichelBlank
 *
 */
public class TeachingTypeDao extends IdDao {
	private static TeachingTypeDao instance;

	private TeachingTypeDao() {
		super(Job.class);
	}

	/**
	 * Returns current instance
	 *
	 * @return instance
	 */
	public static TeachingTypeDao getInstance() {
		if (instance == null) {
			instance = new TeachingTypeDao();
		}
		return instance;
	}
}

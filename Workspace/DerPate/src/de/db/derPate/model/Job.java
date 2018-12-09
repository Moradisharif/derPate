package de.db.derPate.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.jdt.annotation.NonNull;

/**
 * This dataclass contains informations regarding the different job types (for
 * example "Auszubildender")
 *
 * @author MichelBlank
 *
 */
@Entity
@Table(name = "Job")
@AttributeOverride(name = "id", column = @Column(name = "Id_Job"))
public class Job extends Id {
	@NonNull
	@Column(name = "Job")
	private String job = "";

	/**
	 * Constructor used for database connection. Id will be set to null!
	 */
	Job() {
		super(null);
	}

	/**
	 * Constructor
	 *
	 * @param id  Id
	 * @param job Job name
	 */
	public Job(int id, @NonNull String job) {
		super(id);
		this.setJob(job);
	}

	/**
	 * Returns job name
	 *
	 * @return job name
	 */
	@NonNull
	public String getJob() {
		return this.job;
	}

	/**
	 * Sets job name
	 *
	 * @param job job name
	 */
	public void setJob(@NonNull String job) {
		this.job = job;
	}
}
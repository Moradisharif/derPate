package de.db.derPate.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.jdt.annotation.NonNull;

import com.google.gson.annotations.Expose;

/**
 * This dataclass contains field to hold information about the teaching types
 *
 * @author MichelBlank
 *
 */
@Entity
@Table(name = "Teaching_Type")
@AttributeOverride(name = "id", column = @Column(name = "Id_Teaching_Type"))
public class TeachingType extends Id {
	@NonNull
	@Column(name = "Teaching_Type", nullable = false)
	@Expose
	private String teachingType;

	/**
	 * Default constructor used for hibernate
	 */
	TeachingType() {
		super();
		this.teachingType = ""; //$NON-NLS-1$
	}

	/**
	 * Constructor
	 *
	 * @param id           id
	 * @param teachingType name of teaching type
	 */
	public TeachingType(int id, @NonNull String teachingType) {
		super(id);
		this.teachingType = teachingType;
	}

	/**
	 * Returns the {@link TeachingType}
	 *
	 * @return name of teaching type
	 */
	@NonNull
	public String getTeachingType() {
		return this.teachingType;
	}
}

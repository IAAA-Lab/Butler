/**
 * Autor: Iñigo Alonso Ruiz Quality supervised by: F.J. Lopez Pellicer
 */

package models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


/**
 * Model of the Docker image
 * @author shathe
 *
 */
@Entity
public class ImageDocker {
	@Id
    @GeneratedValue
    private long id;

	private String name, dslId,date;
	

    public ImageDocker (long id, String name, String dslId) {
        this.id = id;
        this.name = name;
        this.dslId = dslId;        
    }

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDslId() {
		return dslId;
	}

	public void setDslId(String dslId) {
		this.dslId = dslId;
	}
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}


	@Override
    public String toString() {
        return String.format(
                "User [id=%d, name='%s', dslId='%s']",
                id, name, dslId);
    }


}

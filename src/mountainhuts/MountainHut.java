package mountainhuts;

import java.util.Optional;

/**
 * Represents a mountain hut
 * 
 * It includes a name, optional altitude, category,
 * number of beds and location municipality.
 *  
 *
 */
public class MountainHut {
	private final String name,category; private final Integer altitude,bedsNumber; private final Municipality municipality;
	MountainHut(String name,Integer altitude,String category,Integer bedsNumber,Municipality municipality){
		this.name=name; this.altitude=altitude; this.category=category; this.bedsNumber=bedsNumber; this.municipality=municipality;
	}

	/**
	 * Retrieves the name of the hut
	 * @return name of the hut
	 */
	public String getName() { return name; }

	/**
	 * Retrieves altituted if available
	 * 
	 * @return optional hut altitude
	 */
	public Optional<Integer> getAltitude() { return Optional.ofNullable(altitude); }

	/**
	 * Retrieves the category of the hut
	 * @return hut category
	 */
	public String getCategory() { return category; }

	/**
	 * Retrieves the number of beds available in the hut
	 * @return number of beds
	 */
	public Integer getBedsNumber() { return bedsNumber; }

	/**
	 * Retrieves the municipality of the hut
	 * @return hut municipality
	 */
	public Municipality getMunicipality() { return municipality; }
}
package mountainhuts;

/**
 * Class representing a municipality that hosts a mountain hut.
 * It is a data class with getters for name, province, and altitude
 * 
 */
public class Municipality {
	private final String name,province; private final Integer altitude;

	public Municipality(String name, String province, Integer altitude) {
		this.name=name; this.province=province; this.altitude=altitude;
	}

	public String getName() { return name; }

	public String getProvince() { return province; }

	public Integer getAltitude() { return altitude; }
}
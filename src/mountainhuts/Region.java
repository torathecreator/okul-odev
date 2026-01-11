package mountainhuts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Class {@code Region} represents the main facade
 * class for the mountains hut system.
 * 
 * It allows defining and retrieving information about
 * municipalities and mountain huts.
 *
 */
public class Region {
	private final String name;
	private final Map<String,Municipality> mun=new TreeMap<>();
	private final Map<String,MountainHut> huts=new TreeMap<>();
	private final List<R> ranges=new ArrayList<>();
	private static class R{final int min,max;final String s;R(int a,int b,String s){min=a;max=b;this.s=s;}boolean ok(int a){return a>min&&a<=max;}}

	/**
	 * Create a region with the given name.
	 * 
	 * @param name
	 *            the name of the region
	 */
	public Region(String name) { this.name=name; }

	/**
	 * Return the name of the region.
	 * 
	 * @return the name of the region
	 */
	public String getName() { return name; }

	/**
	 * Create the ranges given their textual representation in the format
	 * "[minValue]-[maxValue]".
	 * 
	 * @param ranges
	 *            an array of textual ranges
	 */
	public void setAltitudeRanges(String... ranges) {
		this.ranges.clear();
		if(ranges!=null) for(String s:ranges){
			String[] p=s.trim().split("-");
			this.ranges.add(new R(Integer.parseInt(p[0].trim()),Integer.parseInt(p[1].trim()),p[0].trim()+"-"+p[1].trim()));
		}
	}

	/**
	 * Return the textual representation in the format "[minValue]-[maxValue]" of
	 * the range including the given altitude or return the default range "0-INF".
	 * 
	 * @param altitude
	 *            the geographical altitude
	 * @return a string representing the range
	 */
	public String getAltitudeRange(Integer altitude) {
		if(altitude==null) return "0-INF";
		for(R r:ranges) if(r.ok(altitude)) return r.s;
		return "0-INF";
	}

	/**
	 * Return all the municipalities available.
	 * 
	 * The returned collection is unmodifiable
	 * 
	 * @return a collection of municipalities
	 */
	public Collection<Municipality> getMunicipalities() { return Collections.unmodifiableCollection(mun.values()); }

	/**
	 * Return all the mountain huts available.
	 * 
	 * The returned collection is unmodifiable
	 * 
	 * @return a collection of mountain huts
	 */
	public Collection<MountainHut> getMountainHuts() { return Collections.unmodifiableCollection(huts.values()); }

	/**
	 * Create a new municipality if it is not already available or find it.
	 * Duplicates must be detected by comparing the municipality names.
	 * 
	 * @param name
	 *            the municipality name
	 * @param province
	 *            the municipality province
	 * @param altitude
	 *            the municipality altitude
	 * @return the municipality
	 */
	public Municipality createOrGetMunicipality(String name, String province, Integer altitude) {
		return mun.computeIfAbsent(name,n->new Municipality(n,province,altitude));
	}

	/**
	 * Create a new mountain hut if it is not already available or find it.
	 * Duplicates must be detected by comparing the mountain hut names.
	 *
	 * @param name
	 *            the mountain hut name
	 * @param category
	 *            the mountain hut category
	 * @param bedsNumber
	 *            the number of beds in the mountain hut
	 * @param municipality
	 *            the municipality in which the mountain hut is located
	 * @return the mountain hut
	 */
	public MountainHut createOrGetMountainHut(String name, String category, 
											  Integer bedsNumber, Municipality municipality) {
		return createOrGetMountainHut(name,null,category,bedsNumber,municipality);
	}

	/**
	 * Create a new mountain hut if it is not already available or find it.
	 * Duplicates must be detected by comparing the mountain hut names.
	 * 
	 * @param name
	 *            the mountain hut name
	 * @param altitude
	 *            the mountain hut altitude
	 * @param category
	 *            the mountain hut category
	 * @param bedsNumber
	 *            the number of beds in the mountain hut
	 * @param municipality
	 *            the municipality in which the mountain hut is located
	 * @return a mountain hut
	 */
	public MountainHut createOrGetMountainHut(String name, Integer altitude, String category, 
											  Integer bedsNumber, Municipality municipality) {
		return huts.computeIfAbsent(name,n->new MountainHut(n,altitude,category,bedsNumber,municipality));
	}

	/**
	 * Creates a new region and loads its data from a file.
	 * 
	 * The file must be a CSV file and it must contain the following fields:
	 * <ul>
	 * <li>{@code "Province"},
	 * <li>{@code "Municipality"},
	 * <li>{@code "MunicipalityAltitude"},
	 * <li>{@code "Name"},
	 * <li>{@code "Altitude"},
	 * <li>{@code "Category"},
	 * <li>{@code "BedsNumber"}
	 * </ul>
	 * 
	 * The fields are separated by a semicolon (';'). The field {@code "Altitude"}
	 * may be empty.
	 * 
	 * @param name
	 *            the name of the region
	 * @param file
	 *            the path of the file
	 */
	public static Region fromFile(String name, String file) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(file);
		Region r=new Region(name);
		List<String> rows=readData(file);
		for(int i=1;i<rows.size();i++){
			String[] c=rows.get(i).split(";",-1);
			String prov=c[0],mname=c[1],hname=c[3],cat=c[5];
			Integer malt=Integer.valueOf(c[2]),beds=Integer.valueOf(c[6]);
			Municipality m=r.createOrGetMunicipality(mname,prov,malt);
			if(c[4]==null||c[4].isEmpty()) r.createOrGetMountainHut(hname,cat,beds,m);
			else r.createOrGetMountainHut(hname,Integer.valueOf(c[4]),cat,beds,m);
		}
		return r;
	}

	/**
	 * Reads the lines of a text file.
	 *
	 * @param file path of the file
	 * @return a list with one element per line
	 */
	public static List<String> readData(String file) {
		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			return in.lines().toList();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return new ArrayList<>();
		}
	}

	/**
	 * Count the number of municipalities with at least a mountain hut per each
	 * province.
	 * 
	 * @return a map with the province as key and the number of municipalities as
	 *         value
	 */
	public Map<String, Long> countMunicipalitiesPerProvince() {
		return huts.values().stream().map(MountainHut::getMunicipality).distinct()
				.collect(java.util.stream.Collectors.groupingBy(Municipality::getProvince,TreeMap::new,java.util.stream.Collectors.counting()));
	}

	/**
	 * Count the number of mountain huts per each municipality within each province.
	 * 
	 * @return a map with the province as key and, as value, a map with the
	 *         municipality as key and the number of mountain huts as value
	 */
	public Map<String, Map<String, Long>> countMountainHutsPerMunicipalityPerProvince() {
		return huts.values().stream().collect(java.util.stream.Collectors.groupingBy(h->h.getMunicipality().getProvince(),TreeMap::new,
				java.util.stream.Collectors.groupingBy(h->h.getMunicipality().getName(),TreeMap::new,java.util.stream.Collectors.counting())));
	}

	/**
	 * Count the number of mountain huts per altitude range. If the altitude of the
	 * mountain hut is not available, use the altitude of its municipality.
	 * 
	 * @return a map with the altitude range as key and the number of mountain huts
	 *         as value
	 */
	public Map<String, Long> countMountainHutsPerAltitudeRange() {
		return huts.values().stream().collect(java.util.stream.Collectors.groupingBy(h->getAltitudeRange(h.getAltitude().orElse(h.getMunicipality().getAltitude())),
				TreeMap::new,java.util.stream.Collectors.counting()));
	}

	/**
	 * Compute the total number of beds available in the mountain huts per each
	 * province.
	 * 
	 * @return a map with the province as key and the total number of beds as value
	 */
	public Map<String, Integer> totalBedsNumberPerProvince() {
		return huts.values().stream().collect(java.util.stream.Collectors.groupingBy(h->h.getMunicipality().getProvince(),TreeMap::new,
				java.util.stream.Collectors.summingInt(MountainHut::getBedsNumber)));
	}

	/**
	 * Compute the maximum number of beds available in a single mountain hut per
	 * altitude range. If the altitude of the mountain hut is not available, use the
	 * altitude of its municipality.
	 * 
	 * @return a map with the altitude range as key and the maximum number of beds
	 *         as value
	 */
	public Map<String, Optional<Integer>> maximumBedsNumberPerAltitudeRange() {
		return huts.values().stream().collect(java.util.stream.Collectors.groupingBy(h->getAltitudeRange(h.getAltitude().orElse(h.getMunicipality().getAltitude())),
				TreeMap::new,java.util.stream.Collectors.mapping(MountainHut::getBedsNumber,java.util.stream.Collectors.maxBy(Integer::compare))));
	}

	/**
	 * Compute the municipality names per number of mountain huts in a municipality.
	 * The lists of municipality names must be in alphabetical order.
	 * 
	 * @return a map with the number of mountain huts in a municipality as key and a
	 *         list of municipality names as value
	 */
	public Map<Long, List<String>> municipalityNamesPerCountOfMountainHuts() {
		return huts.values().stream().collect(java.util.stream.Collectors.groupingBy(h->h.getMunicipality().getName(),java.util.stream.Collectors.counting()))
				.entrySet().stream().collect(java.util.stream.Collectors.groupingBy(Map.Entry::getValue,TreeMap::new,
						java.util.stream.Collectors.collectingAndThen(java.util.stream.Collectors.mapping(Map.Entry::getKey,java.util.stream.Collectors.toList()),
								l->{l.sort(String::compareTo); return l;})));
	}

}
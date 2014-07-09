package state;
/**
 * Interfaace to enable the visualisation of the map
 */
public interface MapVisualisable {
	
	/**
	 * Function to get the filename of the Map. Your visualiser needs to
	 * handle the loading of the map file
	 * @return The filename of the map
	 */
	public String getMapFilename();

}

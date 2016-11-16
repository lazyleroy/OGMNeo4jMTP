package LocationProcessorServer.gpxParser;

public class LocationPoint {

	double latitude;
	double longitude;
	long time;
	double elevation;
	String name;
	String description;

	public void setLocation(double longitude, double latitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long l) {
		this.time = l;
	}

	public void setElevation(double parseDouble) {
		this.elevation = parseDouble;
	}

	public void setName(String string) {
		this.name = string;
	}

	public void setDescription(String string) {
		this.description = string;
	}
}

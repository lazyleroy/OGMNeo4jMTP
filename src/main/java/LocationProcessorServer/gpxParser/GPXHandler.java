package LocationProcessorServer.gpxParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import LocationProcessorServer.datastructures.GPS_plus;
import LocationProcessorServer.gpxParser.CartoGpxParser.TrackSegment;

/**
 * This class handles the communication with the GPX parser
 * 
 * @author simon_000
 */
public class GPXHandler {

	/**
	 * Method to read a GPX file
	 * 
	 * @param filename
	 *            :name of the gpx file
	 * @return ArrayList of GPS_plus objects
	 */
	public static ArrayList<GPS_plus> readGPXFile(String filename) {
		CartoGpxParser gpxParser = new CartoGpxParser(filename);
		gpxParser.parse();
		List<TrackSegment> tracks = gpxParser.getTracks();

		ArrayList<GPS_plus> trajectory = new ArrayList<GPS_plus>();
		for (int i = 0; i < tracks.size(); i++) {
			List<TrackPoint> points = tracks.get(i).getPoints();
			for (int j = 0; j < points.size(); j++) {
				TrackPoint temp = points.get(j);
				GPS_plus point = new GPS_plus();

				point.setLatitude((float) temp.latitude);
				point.setLongitude((float) temp.longitude);
				point.setHead(temp.elevation);
				point.setTime(new Date(temp.time));

				trajectory.add(point);
			}
		}
		return trajectory;
	}
}

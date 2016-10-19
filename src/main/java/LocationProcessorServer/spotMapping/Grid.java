package LocationProcessorServer.spotMapping;

import java.util.ArrayList;

import cern.colt.matrix.impl.SparseObjectMatrix2D;
import LocationProcessorServer.datastructures.GPS_plus;
import entities.*;
/**
 * This class represents a grid over the GPS-data
 * 
 * -------------------------------------------------------
 * This class uses the colt framework, which is licensed as follows:
 * 
 * 		Copyright (c) 1999 CERN - European Organization for Nuclear Research.
 * 
 * 		Permission to use, copy, modify, distribute and sell this software and its
 * 		documentation for any purpose is hereby granted without fee, provided that
 * 		the above copyright notice appear in all copies and that both that copyright
 * 		notice and this permission notice appear in supporting documentation. CERN
 * 		makes no representations about the suitability of this software for any
 * 		purpose. It is provided "as is" without expressed or implied warranty.
 * -------------------------------------------------------
 * 
 * @author simon_000
 *
 */
public class Grid {

	/**
	 * Matrix to access the single grid fields
	 */
	private static SparseObjectMatrix2D matrix;

	/**
	 * Grid-size in decimal degree
	 */
	private static double gridsize = 0.0003;

	/**
	 * Minimal latitude value to define an area (Frankfurt latitude values: 48
	 * to 52)
	 */
	private static double minLat = 36;

	/**
	 * Maximal latitude value to define an area (Frankfurt latitude values: 48
	 * to 52)
	 */
	private static double maxLat = 43;

	/**
	 * Minimal longitude value to define an area (Frankfurt longitude values: 6
	 * to 11)
	 */
	private static double minLong = 113;

	/**
	 * Maximal longitude value to define an area (Frankfurt longitude values: 6
	 * to 11)
	 */
	private static double maxLong = 120;

	/**
	 * Generated rows & cols
	 */
	private static int rows;
	private static int cols;

	/**
	 * Creates a grid with the defined grid-size
	 */
	public static void createGrid() {
		double latV = (getMaxLat() - getMinLat()) / getGridsize();
		double longV = (getMaxLong() - getMinLong()) / getGridsize();
		rows = (int) latV;
		cols = (int) longV;
		if (((rows - latV) != 0.0)) {
			rows++;
		}
		if (((cols - longV) != 0.0)) {
			cols++;
		}
		matrix = new SparseObjectMatrix2D(rows, cols);
	}

	/**
	 * Automatically adds a spot to the grid
	 * 
	 * @param spot
	 *            :Spot to add
	 */
	static void add(Spot spot) {
		double x = ((spot.getLatitude() - getMinLat()) / getGridsize());
		double y = ((spot.getLongitude() - getMinLong()) / getGridsize());
		int arg1 = (int) x;
		int arg2 = (int) y;

		@SuppressWarnings("unchecked")
		ArrayList<Spot> spots = (ArrayList<Spot>) matrix.getQuick(arg1, arg2);
		if (spots == null) {
			spots = new ArrayList<Spot>();
		}
		// if already contained, overwrite
		for (int i = 0; i < spots.size();) {
			if (spot.getSpotID() == spots.get(i).getSpotID()) {
				spots.remove(i);
			} else {
				i++;
			}
		}
		spots.add(spot);
		matrix.setQuick(arg1, arg2, spots);
	}

	/**
	 * Automatically removes a spot from the grid
	 * 
	 * @param spot
	 *            :Spot to remove
	 */
	static void remove(Spot spot) {
		double x = (spot.getLatitude() - getMinLat()) / getGridsize();
		double y = (spot.getLongitude() - getMinLong()) / getGridsize();
		int arg1 = (int) x;
		int arg2 = (int) y;
		@SuppressWarnings("unchecked")
		ArrayList<Spot> spots = (ArrayList<Spot>) matrix.getQuick(arg1, arg2);
		for (int i = 0; i < spots.size();) {
			if (spot.getSpotID() == spots.get(i).getSpotID()) {
				spots.remove(i);
			} else {
				i++;
			}
		}
		matrix.setQuick(arg1, arg2, spots);
	}

	/**
	 * Returns a particular spot with a given ID and center-point
	 * 
	 * @param spotID
	 *            :ID of the Spot
	 * @param point
	 *            :GPS location of the Spot
	 * @return Spot :searched Spot
	 */
	static Spot getSpot(int spotID, GPS_plus point) {
		double x = ((point.getLatitude() - getMinLat()) / getGridsize());
		double y = ((point.getLongitude() - getMinLong()) / getGridsize());
		int arg1 = (int) x;
		int arg2 = (int) y;
		@SuppressWarnings("unchecked")
		ArrayList<Spot> spots = (ArrayList<Spot>) matrix.getQuick(arg1, arg2);
		Spot spot = null;
		for (int i = 0; i < spots.size(); i++) {
			if (spotID == spots.get(i).getSpotID()) {
				spot = spots.get(i);
			}
		}
		return spot;
	}
	static Spot getSpot(long spotID, float lat, float longi) {
		double x = ((lat - getMinLat()) / getGridsize());
		double y = ((longi - getMinLong()) / getGridsize());
		int arg1 = (int) x;
		int arg2 = (int) y;
		@SuppressWarnings("unchecked")
		ArrayList<Spot> spots = (ArrayList<Spot>) matrix.getQuick(arg1, arg2);
		Spot spot = null;
		for (int i = 0; i < spots.size(); i++) {
			if (spotID == spots.get(i).getSpotID()) {
				spot = spots.get(i);
			}
		}
		return spot;
	}

	/**
	 * Returns all spots stored in a specific single grid
	 * 
	 * @param arg1
	 *            latitude-index
	 * @param arg2
	 *            longitude-index
	 * @return ArrayList<Spot>
	 */
	static ArrayList<Spot> getSpots(int arg1, int arg2) {
		@SuppressWarnings("unchecked")
		ArrayList<Spot> spots = (ArrayList<Spot>) matrix.getQuick(arg1, arg2);
		return spots;
	}

	// --------------------------
	// Getter and Setter methods
	// --------------------------

	public static void setMinLat(double minLat) {
		Grid.minLat = minLat;
	}

	public static void setMaxLat(double maxLat) {
		Grid.maxLat = maxLat;
	}

	public static void setMinLong(double minLong) {
		Grid.minLong = minLong;
	}

	public static void setMaxLong(double maxLong) {
		Grid.maxLong = maxLong;
	}

	public static double getMaxLat() {
		return maxLat;
	}

	public static double getMinLat() {
		return minLat;
	}

	public static double getMinLong() {
		return minLong;
	}

	public static double getMaxLong() {
		return maxLong;
	}

	static double getGridsize() {
		return gridsize;
	}
}

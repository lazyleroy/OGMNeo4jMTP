package LocationProcessorServer.LocationProcessorServer;

import java.util.ArrayList;





import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import LocationProcessorServer.controller.SystemData;
import LocationProcessorServer.datastructures.*;
import LocationProcessorServer.graphStructure.*;
import LocationProcessorServer.spotMapping.SpotHandler;

import static javax.swing.text.html.FormSubmitEvent.MethodType.POST;

/**
 * This class is responsible to receive the HTTP requests of the client
 * 
 * @author Simon
 *
 */

public class Webservice {
/*
	/**
	 * This method receives a Route of the Client in JSON-Format, afterwards it
	 * directly processes it
	 * 
	 * @param jsonRoute
	 *            :Route in JSON-format
	 * @return String as response to the client

	@POST
	@Path("/post/singleRoute")
	@Consumes("application/json")
	public String getRouteInJSON(String jsonRoute) {

		ObjectMapper mapper = new ObjectMapper();
		Route route = null;
		try {
			route = mapper.readValue(jsonRoute, Route.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// map into spots
		route = SpotHandler.learningSpotStructure(route);
		SystemData.getRoutes().add(route);
		// abstract routes by spots
		Route abstractedBySpots = new Route(new ArrayList<GPS_plus>(), route.getUser());
		Spot lastSpot = null;
		for (int j = 0; j < route.size(); j++) {
			Spot spot = route.getTrajectory().get(j).getSpot();
			if (spot != lastSpot && spot != null) {
				abstractedBySpots.getTrajectory().add(route.getTrajectory().get(j));
				lastSpot = spot;
			}
		}
		SystemData.getAbstractedBySpots().add(abstractedBySpots);

		// maintain graph structue
		GraphHandler.updateGraph(route);
		// abstract by nodes
		Route abstractedByNodes = new Route(new ArrayList<GPS_plus>(), route.getUser());
		Node lastNode = null;
		for (int j = 0; j < route.size(); j++) {
			Node node = route.getTrajectory().get(j).getSpot().node;
			if (node != lastNode && node != null) {
				abstractedByNodes.getTrajectory().add(route.getTrajectory().get(j));
				lastNode = node;
			}
		}
		SystemData.getAbstractedByNodes().add(abstractedByNodes);
		return "Route processed";
	}

	/**
	 * This method receives multiple Routes of the Client in JSON-Format,
	 * afterwards it directly processes the routes.
	 * 
	 * @param jsonRoutes
	 *            :Routes in JSON-format
	 * @return String as response to the client


	public String getRoutesInJSON(String jsonRoutes) {

		ObjectMapper mapper = new ObjectMapper();
		ArrayList<Route> routes = null;
		try {
			routes = mapper.readValue(jsonRoutes, new TypeReference<ArrayList<Route>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < routes.size(); i++) {
			Route route = routes.get(i);
			// map into spots
			route = SpotHandler.learningSpotStructure(route);
			SystemData.getRoutes().add(route);
			// abstract routes by spots
			Route abstractedBySpots = new Route(new ArrayList<GPS_plus>(), route.getUser());
			Spot lastSpot = null;
			for (int j = 0; j < route.size(); j++) {
				Spot spot = route.getTrajectory().get(j).getSpot();
				if (spot != lastSpot && spot != null) {
					abstractedBySpots.getTrajectory().add(route.getTrajectory().get(j));
					lastSpot = spot;
				}
			}
			SystemData.getAbstractedBySpots().add(abstractedBySpots);
			
			// maintain graph structue
			GraphHandler.updateGraph(route);
			// abstract by nodes
			Route abstractedByNodes = new Route(new ArrayList<GPS_plus>(), route.getUser());
			Node lastNode = null;
			for (int j = 0; j < route.size(); j++) {
				Node node = route.getTrajectory().get(j).getSpot().node;
				if (node != lastNode && node != null) {
					abstractedByNodes.getTrajectory().add(route.getTrajectory().get(j));
					lastNode = node;
				}
			}
			SystemData.getAbstractedByNodes().add(abstractedByNodes);
		}
		return "Routes processed";
	}*/
}
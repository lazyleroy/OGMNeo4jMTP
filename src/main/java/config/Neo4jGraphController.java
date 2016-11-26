package config;

import Interfaces.DBController;
import entities.*;
import org.neo4j.ogm.exception.NotFoundException;
import org.neo4j.ogm.model.Result;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Felix on 10.11.2016.
 * Package: config
 * Project: OGMNeo4jMTP
 */
public class Neo4jGraphController implements DBController {

    private static Main main = new Main();

    @Override
    public void addSpot(Spot spot) {
        Neo4jTemplate template = main.createNeo4JTemplate();

        String spotID = spot.getSpotID();
        ArrayList<Spot> neighbors = spot.getNeighbors();
        String spotNeighborsQuery = "MERGE (n:Spot{spotID:\'"+spotID+"\', longitude:"+spot.getLongitude()+", latitude:"+spot.getLatitude()+", " +
                "spotHeading:"+spot.getSpotHeading()+", intersection:"+spot.isIntersection()+", numberCenterCalcPoints:"+spot.getNumberCenterCalcPoints()+", " +
                "headSum:"+spot.getHeadSum()+", headCalcPoints:"+spot.getHeadCalcPoints()+", latitudeSum:"+spot.getLatitudeSum()+", " +
                "longitudeSum:"+spot.getLongitudeSum()+", numberOfNeighbours:"+spot.getNeighbors().size()+"}) ";
        for(int i = 0; i < neighbors.size(); i++){
            String neighborID = neighbors.get(i).getSpotID();
            spotNeighborsQuery+= "MERGE (t"+i+":Spot{spotID:\'"+neighborID+"\'})";
        }
        for(int i = 0; i < neighbors.size(); i++){
            spotNeighborsQuery+= "MERGE (n)-[:CONNECTED_WITH]-(t"+i+") ";
        }
        template.query(spotNeighborsQuery, Collections.EMPTY_MAP, false);
        //System.out.println("addSpot: "+ spotNeighborsQuery);

    }

    @Override
    public void updateSpot(Spot spot) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        Spot s = template.loadByProperty(Spot.class, "spotID", spot.getSpotID());
        ArrayList<Spot> neighbors = s.getNeighbors();
        s.setNeighbors(null);
        s.setLatitude(spot.getLatitude());
        s.setLongitude(spot.getLongitude());
        s.setSpotHeading(spot.getSpotHeading());
        s.setIntersection(spot.isIntersection());
        s.setNumberCenterCalcPoints(spot.getNumberCenterCalcPoints());
        s.setHeadSum(spot.getHeadSum());
        s.setHeadCalcPoints(spot.getHeadCalcPoints());
        s.setLatitudeSum(spot.getLatitudeSum());
        s.setLongitudeSum(spot.getLongitudeSum());
        s.setNumberOfNeighbours(spot.getNumberOfNeighbours());
        template.save(s);
        s.setNeighbors(neighbors);
    }

    @Override
    public Spot getSpot(String spotID) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        try{
            Spot spot = template.loadByProperty(Spot.class, "spotID", spotID);
            if(spot.getNeighbors() == null){
                spot.setNeighbors(new ArrayList<Spot>());
            }
           // System.out.println("getSpot QUERY");
            return spot;
        }catch(NotFoundException nfe){
            return null;
        }
    }

    @Override
    public ArrayList<Spot> getSpots(float latitude, float longitude) {
        Neo4jTemplate template = main.createNeo4JTemplate();

        Result r = template.query("match(n:Spot) where distance(point(n),point({latitude:"+latitude+", longitude:"+longitude+"}))<50 return n", Collections.EMPTY_MAP, false);
        Iterator<Map<String, Object>> result = r.iterator();
        ArrayList<Spot> spots = new ArrayList<>();
        while(result.hasNext()){
            Map<String, Object> map = result.next();
            for(Map.Entry<String, Object> entry : map.entrySet()){
                if(entry.getValue() instanceof Spot){
                    spots.add((Spot)entry.getValue());
                }
            }
        }
        if(spots.isEmpty()){
            return null;
        }
        //System.out.println("getSpots: match(n:Spot) where distance(point(n),point({latitude:"+latitude+", longitude:"+longitude+"}))<50 return n");
        return spots;
    }

    @Override
    public void addGPSPoints(ArrayList<GPS_plus> gpspoints, String username, ArrayList<String> intersectionSpots) {

        Neo4jTemplate template = main.createNeo4JTemplate();
        HashSet<String> intersectionSpotStrings = new HashSet<>();


        String inList = "p.spotID IN ";
        for(int i = 0; i < intersectionSpots.size(); i++){
            if(i == 0){
                inList += "[\'"+intersectionSpots.get(i)+"\', ";
            }else if (i == intersectionSpots.size()-1){
                inList += "\'"+intersectionSpots.get(i)+"\']";
            }else {
                inList += "\'"+intersectionSpots.get(i)+"\', ";
            }
        }
        String finalizeQuery = "MATCH (p:Spot)-[:CONNECTED_WITH]-(c:Spot) WITH p,count(c) as rels WHERE rels > 2 AND "+inList+" set p.intersection = true return p";
        Result result = template.query(finalizeQuery, Collections.EMPTY_MAP, false);

        Iterator<Map<String, Object>> iterator = result.iterator();
        while(iterator.hasNext()){
            Map<String, Object> map = iterator.next();
            for(Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof Spot) {
                    intersectionSpotStrings.add(((Spot) entry.getValue()).getSpotID());
                }
            }
        }


        int waypointNumber = 0;
        boolean repeatedGPSinSpot = true;
        String gpsPlusID;
        String gpsPlusIDcheck = "";

        for(int i = 0; i < gpspoints.size(); i++){
            GPS_plus tempGPS = gpspoints.get(i);
            gpsPlusID = username+tempGPS.getTime()+tempGPS.getLatitude()+tempGPS.getLongitude();
            String gpsPlusQuery = "MERGE (n:User{username:\'"+username+"\'}) \n";
            String spotID = gpspoints.get(i).getSpot().getSpotID();
            gpsPlusQuery +=  "MERGE (GPS_Plus0:GPS_Plus{date:\'"+tempGPS.getTime()+"\', latitude:"+tempGPS.getLatitude()
                    +", longitude:"+tempGPS.getLongitude()+", head:"+tempGPS.getHead()+", gpsPlusID:\'"+gpsPlusID+"\', " +
                    //"speed:"+tempGPS.getSpeed()+"," +
                    " timeDiffToNextPoint:" +
                    tempGPS.getTimediffToNextPoint()+", distanceToNextPoint:"+tempGPS.getTimediffToNextPoint()+", dataID:"+tempGPS.getDataID()+"}) \n";


                gpsPlusQuery += "MERGE (spot:Spot{spotID:\'" + spotID + "\'}) \n";

            gpsPlusQuery += "MERGE (GPS_Plus0)-[:MAPPED_TO_SPOT]->(spot)";
            if(i==0){
                gpsPlusQuery += "MERGE (n)-[:STARTING_POINT]->(GPS_Plus0) \n";
                gpsPlusQuery += "MERGE (waypoint:Waypoint{id:"+String.valueOf(waypointNumber)+"}) \n MERGE (n)-[:ROUTE_START]->(waypoint) \n";
                gpsPlusQuery += "MERGE (GPS_Plus0)-[:WAYPOINT]->(waypoint) \n";

                waypointNumber++;
            }
            if(intersectionSpotStrings.contains(spotID)){
                if(!repeatedGPSinSpot){
                    gpsPlusQuery += "MERGE (waypoint1:Waypoint{id:"+String.valueOf(waypointNumber)+"}) \n MERGE (GPS_Plus0)-[:WAYPOINT]-(waypoint1) \n";
                    gpsPlusQuery += "MERGE(waypoint0:Waypoint{id:"+String.valueOf(waypointNumber-1)+"})";
                    gpsPlusQuery += "MERGE (waypoint0)-[:NEXT_WAYPOINT]->(waypoint1) \n ";
                    repeatedGPSinSpot = true;
                    waypointNumber++;
                }
            }else{
                repeatedGPSinSpot = false;
            }

            if(i >0){
                gpsPlusQuery += "MERGE (GPS_Plus1:GPS_Plus{gpsPlusID:\'"+gpsPlusIDcheck+"\'})";
                gpsPlusQuery += "MERGE (GPS_Plus0)-[:NEXT_GPS]->(GPS_Plus1) \n";
            }
            gpsPlusIDcheck = gpsPlusID;
            System.out.println(gpsPlusQuery);
            System.out.println();
            System.out.println();
            template.query(gpsPlusQuery, Collections.EMPTY_MAP, false);

        }




    }

    @Override
    public void addNeighbour(String spotID, String updatedSpotID, boolean intersectionCheck, boolean updatedIntersectionCheck){
        Neo4jTemplate template = main.createNeo4JTemplate();
        String addQuery = "MATCH (n:Spot{spotID:\'"+spotID+"\'}) MATCH (r:Spot{spotID:\'" +updatedSpotID +"\'}) MERGE (n)-[:CONNECTED_WITH]-(r)";
            System.out.println("spotID: " +spotID +" updatedSpotID: "+ updatedSpotID);

        template.query(addQuery, Collections.EMPTY_MAP, false);
        //System.out.println("addNeighbour: "+addQuery);

    }

    @Override
    public void setIntersections(ArrayList<String> spots){
        Neo4jTemplate template = main.createNeo4JTemplate();

        String inList = "p.spotID IN ";
        for(int i = 0; i < spots.size(); i++){
            if(i == 0){
                inList += "[\'"+spots.get(i)+"\', ";
            }else if (i == spots.size()-1){
                inList += "\'"+spots.get(i)+"\']";
            }else {
                inList += "\'"+spots.get(i)+"\', ";
            }
        }

        String finalizeQuery = "MATCH (p:Spot)-[:CONNECTED_WITH]-(c:Spot) WITH p,count(c) as rels WHERE rels > 2 AND "+inList+" set p.intersection = true return p";

        Result result = template.query(finalizeQuery, Collections.EMPTY_MAP, false);
        //System.out.println("setIntersections: "+finalizeQuery);


    }
}

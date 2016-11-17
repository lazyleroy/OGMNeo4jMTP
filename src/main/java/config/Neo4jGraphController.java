package config;

import Interfaces.DBController;
import entities.*;
import org.neo4j.ogm.exception.NotFoundException;
import org.neo4j.ogm.model.Result;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

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
                "longitudeSum:"+spot.getLongitudeSum()+" numberOfNeighbours:"+spot.getNeighbors().size()+"}) ";
        for(int i = 0; i < neighbors.size(); i++){
            String neighborID = neighbors.get(i).getSpotID();
            spotNeighborsQuery+= "MERGE (t"+i+":Spot{spotID:\'"+neighborID+"\'})";
        }
        for(int i = 0; i < neighbors.size(); i++){
            spotNeighborsQuery+= "MERGE (n)-[:CONNECTED_WITH]-(t"+i+") ";
        }
        template.query(spotNeighborsQuery, Collections.EMPTY_MAP, false);

    }

    @Override
    public void updateSpot(Spot spot) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        Spot s = template.loadByProperty(Spot.class, "spotID", spot.getSpotID());
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
        template.save(s);
    }

    @Override
    public Spot getSpot(String spotID) {
        Neo4jTemplate template = main.createNeo4JTemplate();
        try{
            Spot spot = template.loadByProperty(Spot.class, "spotID", spotID);
            if(spot.getNeighbors() == null){
                spot.setNeighbors(new ArrayList<Spot>());
                System.out.println(spot.getNeighbors()+"FFFFFFFFFFFFFFFFFFFFFFFFFFF");
            }
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
        return spots;
    }

    @Override
    public void addGPSPoints(ArrayList<GPS_plus> gpspoints, String username) {

        Neo4jTemplate template = main.createNeo4JTemplate();
        String gpsPlusQuery = "MERGE (n:User{username:\'"+username+"\'}) ";
        int j = 0;
        for(int i = 0; i < gpspoints.size(); i++){
            String spotID = gpspoints.get(i).getSpot().getSpotID();
            GPS_plus tempGPS = gpspoints.get(i);
            gpsPlusQuery +=  "MERGE (t"+i+":GPS_Plus{date:\'"+tempGPS.getTime()+"\', latitude:"+tempGPS.getLatitude()
                    +", longitude:"+tempGPS.getLongitude()+", head:"+tempGPS.getHead()+", speed:"+tempGPS.getSpeed()+", timeDiffToNextPoint:" +
                    tempGPS.getTimediffToNextPoint()+", distanceToNextPoint:"+tempGPS.getTimediffToNextPoint()+", dataID:"+tempGPS.getDataID()+"}) " +
                    "MERGE (r"+i+":Spot{spotID:\'"+spotID+"\'}) " +
                    "MERGE (t"+i+")-[:MAPPED_TO_SPOT]-(r"+i+") ";
            if(i >0){
            gpsPlusQuery += "MERGE (t"+j+")-[:NEXT_GPS]-(t"+i+") ";
                j++;
            }
            gpsPlusQuery += "MERGE (n)-[:STARTING_POINT]-(t0)";
        }
        System.out.println(gpsPlusQuery);
        template.query(gpsPlusQuery, Collections.EMPTY_MAP, false);
    }

    public void addNeighbour(String spotID, String updatedSpotID, boolean intersectionCheck, boolean updatedIntersectionCheck){
        Neo4jTemplate template = main.createNeo4JTemplate();
        String addQuery = "MATCH (n:Spot{spotID:\'"+spotID+"\'}) MATCH (r:Spot{spotID:\'" +updatedSpotID +"\'}) set n.intersection = "+intersectionCheck+" " +
                "set n.numberOfNeighbours = n.numberOfNeighbours+1 set r.intersection ="+updatedIntersectionCheck+ "" +
                "set r.numberOfNeighbours = r.numberOfNeighbours+1 MERGE (n)-[:CONNECTED_WITH]-(r)";


        template.query(addQuery, Collections.EMPTY_MAP, false);
    }

}
